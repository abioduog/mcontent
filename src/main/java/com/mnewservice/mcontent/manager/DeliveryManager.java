package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.*;
import com.mnewservice.mcontent.domain.mapper.ContentMapper;
import com.mnewservice.mcontent.domain.mapper.DeliveryTimeMapper;
import com.mnewservice.mcontent.domain.mapper.PhoneNumberMapper;
import com.mnewservice.mcontent.messaging.MessageCenter;
import com.mnewservice.mcontent.repository.ScheduledDeliverableRepository;
import com.mnewservice.mcontent.repository.SeriesDeliverableRepository;
import com.mnewservice.mcontent.repository.ServiceRepository;
import com.mnewservice.mcontent.repository.SubscriptionRepository;
import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.ScheduledDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionPeriodEntity;
import com.mnewservice.mcontent.util.DateUtils;
import com.mnewservice.mcontent.util.exception.MessagingException;
import java.text.DateFormat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
@Transactional
public class DeliveryManager {

    private static final int MAXIMUM_LENGTH_FOR_SERIES = 128;
    private static final String OPERATOR_FILTER_SEPARATOR = ",";
    private static final Logger LOG = Logger.getLogger(DeliveryManager.class);

    @Value("${application.delivery.fetch.pageSize}")
    private Integer pageSize;

    @Value("${application.sms.gateway.maxRecipients}")
    private Integer sendSize;

    @Value("${application.delivery.reminder.daysBefore}")
    private Integer leadInDays;

    @Value("${application.delivery.reminder.minDurationInDays}")
    private Integer leadInMinDuration;

    @Value("${application.delivery.reminder.operatorFilter}")
    private String operatorFilter;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ScheduledDeliverableRepository scheduledDeliverableRepository;

    @Autowired
    private SeriesDeliverableRepository seriesDeliverableRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private MessageCenter messageCenter;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private PhoneNumberMapper phoneNumberMapper;

    @Autowired
    private DeliveryTimeMapper deliveryTimeMapper;

    public void deliverContent(DeliveryTime deliveryTime) {
        LOG.info(String.format(
                "Delivering content @ %s",
                deliveryTime.name())
        );

        for (ServiceEntity service : getServices(deliveryTime)) {
            LOG.info(String.format("Service in progress, id=%s", service.getId()));
            ScheduledDeliverableEntity scheduledDeliverable
                    = getScheduledDeliverables(service.getDeliveryPipe());
            SeriesDeliverableEntity[] seriesDeliverables
                    = getSeriesDeliverables(service.getDeliveryPipe());

            if (scheduledDeliverable != null || seriesDeliverables != null) {
                doDeliverContent(
                        service, scheduledDeliverable, seriesDeliverables,
                        pageSize, sendSize
                );
            } else {
                LOG.info(String.format(
                        "Nothing to deliver today (service id=%d)!",
                        service.getId())
                );
            }
        }

    }

    //expiry notification (this is executed once a day)
    public void deliverExpirationNotification(DeliveryTime deliveryTime) {
        LOG.info(String.format(
                "Delivering expiration notifications @ %s",
                deliveryTime.name())
        );

        Date expiryAt = DateUtils.addDays(
                DateUtils.getCurrentDateAtMidnight(),
                leadInDays
        );

        List<ServiceEntity> services = serviceRepository.findByOperatorNotIn(
                Arrays.asList(
                        operatorFilter.split(OPERATOR_FILTER_SEPARATOR)
                )
        );
        for (ServiceEntity service : services) {
            LOG.info(String.format("Service in progress, id=%s", service.getId()));
            // {expirationdate} = %1$s = # of days
            // {daystoexpire} = %2$s = actual date
            String expireMsg = service.getExpireMessage();
            expireMsg.replace("{daystoexpire}", "%1$d");
            expireMsg.replace("{expirationdate}", "%2$s");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, leadInDays);
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
            String expiryMessage = String.format(
                    expireMsg,
                    leadInDays,
                    dateFormatter.format(calendar.getTime())
            );

            doDeliverExpirationNotification(
                    service, pageSize, expiryAt,
                    expiryMessage, leadInMinDuration, sendSize
            );
        }
    }

    private void doDeliverExpirationNotification(
            ServiceEntity service,
            Integer pageSize,
            Date expiryAt,
            String expiryMessage,
            Integer expiryMinDuration,
            Integer sendSize) {
        List<SubscriptionEntity> subscriptions;
        boolean subscriptionsFound;
        long offSet = 0L;
        do {
            LOG.info("Getting subscriptions, start");
            subscriptions = subscriptionRepository.findByExpiry(
                    service.getId(), expiryAt, expiryMinDuration, pageSize, offSet);
            subscriptionsFound = subscriptions != null && subscriptions.size() > 0;

            if (subscriptionsFound) {
                LOG.info(String.format(
                        "Getting subscriptions, end (count %d)",
                        subscriptions.size())
                );
                processExpiringSubscriptions(service,
                        subscriptions,
                        expiryMessage,
                        sendSize
                );
            } else {
                LOG.info(String.format(
                        "Getting subscriptions, end (count %d)",
                        0)
                );
            }
            offSet += pageSize;
        } while (subscriptionsFound);
    }

    private ScheduledDeliverableEntity getScheduledDeliverables(
            DeliveryPipeEntity deliveryPipe) {
        LOG.info("Getting scheduled deliverables, start");
        ScheduledDeliverableEntity scheduledDeliverable
                = scheduledDeliverableRepository.findByDeliveryPipeAndDeliveryDateAndStatus(
                        deliveryPipe,
                        DateUtils.getCurrentDateAtMidnight(),
                        AbstractDeliverableEntity.DeliverableStatusEnum.APPROVED
                );
        LOG.info(String.format(
                "Getting scheduled deliverables, end (count %d)",
                scheduledDeliverable != null ? 1 : 0)
        );
        return scheduledDeliverable;
    }

    private List<ServiceEntity> getServices(DeliveryTime deliveryTime) {
        LOG.info("Getting services, start");
        ServiceEntity.DeliveryTime deliveryTimeEntity
                = deliveryTimeMapper.toEntity(deliveryTime);
        List<ServiceEntity> services
                = serviceRepository.findByDeliveryTime(deliveryTimeEntity);
        LOG.info(String.format("Getting services, end (count %d)", services.size()));
        return services;
    }

    // returns list of SeriesDeliverableEntity ordered such, that in index i
    // there is deliverable, which deliveryDaysAfterSubscription is i
    private SeriesDeliverableEntity[] getSeriesDeliverables(DeliveryPipeEntity deliveryPipe) {
        LOG.info("Getting series deliverables, start");
        List<SeriesDeliverableEntity> seriesDeliverables
                = seriesDeliverableRepository.findByDeliveryPipeAndStatusOrderByDeliveryDaysAfterSubscriptionAsc(
                        deliveryPipe,
                        AbstractDeliverableEntity.DeliverableStatusEnum.APPROVED);
        SeriesDeliverableEntity[] seriesDeliverablesOrdered
                = new SeriesDeliverableEntity[MAXIMUM_LENGTH_FOR_SERIES + 1];
        for (SeriesDeliverableEntity deliverable : seriesDeliverables) {
            int index = deliverable.getDeliveryDaysAfterSubscription();
            seriesDeliverablesOrdered[index] = deliverable;
        }
        LOG.info(String.format(
                "Getting series deliverables, end (count %d)",
                seriesDeliverables.size())
        );
        return seriesDeliverables.size() > 0 ? seriesDeliverablesOrdered : null;
    }

    private void doDeliverContent(ServiceEntity service,
            ScheduledDeliverableEntity scheduledDeliverable,
            SeriesDeliverableEntity[] seriesDeliverables,
            Integer pageSize, Integer sendSize) {
        Pageable page = new PageRequest(0, pageSize);

        Page<SubscriptionEntity> subscriptionsPage;
        do {
            LOG.info("Getting subscriptions, start");
            Date currDate = DateUtils.getCurrentDate();
            subscriptionsPage = subscriptionRepository
                    .findByServiceAndPeriodsStartLessThanAndPeriodsEndGreaterThan(
                            service, currDate, currDate, page
                    );

            if (subscriptionsPage.hasContent()) {
                LOG.info(String.format(
                        "Getting subscriptions, end (count %d)",
                        subscriptionsPage.getContent().size())
                );

                processSubscriptions(
                        subscriptionsPage.getContent(),
                        service.getShortCode(),
                        scheduledDeliverable,
                        seriesDeliverables, sendSize);
            } else {
                LOG.info(String.format(
                        "Getting subscriptions, end (count %d)",
                        0)
                );
            }
            page = subscriptionsPage.nextPageable();
        } while (subscriptionsPage.hasNext());
    }

    private void processSubscriptions(List<SubscriptionEntity> subscriptions,
            Integer shortCode, ScheduledDeliverableEntity scheduledDeliverable,
            SeriesDeliverableEntity[] seriesDeliverables,
            Integer sendSize) throws UnsupportedOperationException {
        LOG.info("Processing subscriptions, start");
        Map<AbstractDeliverableEntity, AbstractMessage> messagesMap = new HashMap<>();
        for (SubscriptionEntity subscription : subscriptions) {
            AbstractDeliverableEntity deliverable
                    = getDeliverable(subscription, scheduledDeliverable, seriesDeliverables);

            if (deliverable == null) {
                // nothing to deliver
                LOG.debug("Nothing to deliver for subscription, id=" + subscription.getId());
                continue;
            }

            SmsMessage message = createMessage(shortCode, messagesMap, deliverable, subscription);

            if (message.getReceivers().size() >= sendSize) {
                sendMessage(message);
                messagesMap.remove(deliverable);
            }
        }
        // send possible "leftover messages"
        for (Map.Entry<AbstractDeliverableEntity, AbstractMessage> entry : messagesMap.entrySet()) {
            sendMessage((SmsMessage) entry.getValue());
        }
        LOG.info("Processing subscriptions, end");
    }

    private void processExpiringSubscriptions(
            ServiceEntity service, List<SubscriptionEntity> subscriptions,
            String expiryMessage, Integer sendSize) {
        LOG.info("Processing expiring subscriptions, start");
        SmsMessage message = createExpiryMessage(expiryMessage);
        message.setFromNumber(service.getShortCode().toString());
        for (SubscriptionEntity subscription : subscriptions) {
            addPhoneNumberToMessage(subscription, message);

            if (message.getReceivers().size() >= sendSize) {
                sendMessage(message);
                message = createExpiryMessage(expiryMessage);
            }
        }
        // send possible "leftover message"
        if (((SmsMessage) message).getReceivers().size() > 0) {
            sendMessage(message);
        }

        LOG.info("Processing expiring subscriptions, end");
    }

    private SmsMessage createExpiryMessage(String expiryMessage) {
        SmsMessage message = new SmsMessage();
        message.setMessage(expiryMessage);
        return message;
    }

    private void addPhoneNumberToMessage(
            SubscriptionEntity subscription,
            AbstractMessage message) {
        PhoneNumber phoneNumber = phoneNumberMapper
                .toDomain(subscription.getSubscriber().getPhone());
        ((SmsMessage) message).getReceivers().add(phoneNumber);
    }

    private SmsMessage createMessage(
            Integer shortCode,
            Map<AbstractDeliverableEntity, AbstractMessage> messagesMap,
            AbstractDeliverableEntity deliverable,
            SubscriptionEntity subscription) throws UnsupportedOperationException {
        AbstractMessage message = messagesMap.get(deliverable);
        if (message == null) {
            message = new SmsMessage();
            message.setMessage(contentMapper.toDomain(deliverable.getContent()).getSmsMessageContent());
            messagesMap.put(deliverable, message);
        }
        ((SmsMessage) message).setFromNumber(shortCode.toString());
        addPhoneNumberToMessage(subscription, message);
        return (SmsMessage) message;
    }

    private void sendMessage(SmsMessage message) {
        LOG.info(String.format(
                "Sending message, start (%d receivers)",
                message.getReceivers().size())
        );
        messageCenter.queueMessage(message);
        LOG.info("Sending message, end");
    }

    private AbstractDeliverableEntity getDeliverable(
            SubscriptionEntity subscription,
            ScheduledDeliverableEntity scheduledDeliverable,
            SeriesDeliverableEntity[] seriesDeliverableOrdered) {
        // assumption: there is either one scheduledDeliverable..
        if (scheduledDeliverable != null) {
            return scheduledDeliverable;
        }

        // ..or one or more seriesDeliverables
        int activeDayNumber = subscription.getActiveDaysOverall();
        LOG.debug(subscription.getSubscriber().getPhone().getNumber() +
                    "'s subscription to " + subscription.getService().getKeyword() +
                    "(" + subscription.getService().getOperator()
                    + ") has been active for " + activeDayNumber);
        if (activeDayNumber > 0 && activeDayNumber <= seriesDeliverableOrdered.length) {
           return seriesDeliverableOrdered[activeDayNumber-1];
        }

        return null;
    }
}
