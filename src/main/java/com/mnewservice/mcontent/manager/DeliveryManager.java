package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.AbstractMessage;
import com.mnewservice.mcontent.domain.DeliveryTime;
import com.mnewservice.mcontent.domain.PhoneNumber;
import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.domain.mapper.DeliveryTimeMapper;
import com.mnewservice.mcontent.domain.mapper.PhoneNumberMapper;
import com.mnewservice.mcontent.messaging.MessageCenter;
import com.mnewservice.mcontent.repository.ScheduledDeliverableRepository;
import com.mnewservice.mcontent.repository.SeriesDeliverableRepository;
import com.mnewservice.mcontent.repository.ServiceRepository;
import com.mnewservice.mcontent.repository.SubscriptionRepository;
import com.mnewservice.mcontent.repository.entity.AbstractContentEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.ScheduledDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionPeriodEntity;
import com.mnewservice.mcontent.util.DateUtils;
import java.util.ArrayList;
import java.util.Arrays;
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

    private static final String DEFAULT_EXPIRY_MESSAGE
            = "Your mContent subscription is expiring in %d days. Please renew "
            + "your subscription, if you wish to receive messages also in the "
            + "future.";
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

        doDeliverExpirationNotification(pageSize, leadInDays, leadInMinDuration, sendSize);
    }

    private void doDeliverExpirationNotification(
            Integer pageSize,
            Integer expirationNotificationLeadInDays,
            Integer expirationNotificationMinDuration,
            Integer sendSize) {
        Date expiryAt = DateUtils.addDays(
                DateUtils.getCurrentDateAtMidnight(),
                expirationNotificationLeadInDays
        );
        List<SubscriptionEntity> subscriptions;
        boolean subscriptionsFound;
        long startId = 0L;
        do {
            LOG.info("Getting subscriptions, start");
            subscriptions = subscriptionRepository.findByExpiry(
                    startId, pageSize, expiryAt,
                    expirationNotificationMinDuration,
                    Arrays.asList(operatorFilter.split(OPERATOR_FILTER_SEPARATOR))
            );
            subscriptionsFound = subscriptions != null && subscriptions.size() > 0;

            if (subscriptionsFound) {
                LOG.info(String.format(
                        "Getting subscriptions, end (count %d)",
                        subscriptions.size())
                );
                startId = subscriptions.get(subscriptions.size() - 1).getId();
                processExpiringSubscriptions(
                        subscriptions,
                        String.format(
                                DEFAULT_EXPIRY_MESSAGE,
                                expirationNotificationLeadInDays
                        ),
                        sendSize);
            } else {
                LOG.info(String.format(
                        "Getting subscriptions, end (count %d)",
                        0)
                );
            }
        } while (subscriptionsFound);
    }

    private ScheduledDeliverableEntity getScheduledDeliverables(
            DeliveryPipeEntity deliveryPipe) {
        LOG.info("Getting scheduled deliverables, start");
        ScheduledDeliverableEntity scheduledDeliverable
                = scheduledDeliverableRepository.findByDeliveryPipeAndDeliveryDate(
                        deliveryPipe,
                        DateUtils.getCurrentDateAtMidnight()
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
                = seriesDeliverableRepository.findByDeliveryPipe(deliveryPipe);
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
            ScheduledDeliverableEntity scheduledDeliverable,
            SeriesDeliverableEntity[] seriesDeliverables,
            Integer sendSize) throws UnsupportedOperationException {
        LOG.info("Processing subscriptions, start");
        Map<AbstractContentEntity, AbstractMessage> messagesMap = new HashMap<>();
        for (SubscriptionEntity subscription : subscriptions) {
            AbstractContentEntity content
                    = getContent(subscription, scheduledDeliverable, seriesDeliverables);

            if (content == null) {
                // nothing to deliver
                LOG.debug("Nothing to deliver for subscription, id=" + subscription.getId());
                continue;
            }

            AbstractMessage message = createMessage(messagesMap, content, subscription);

            if (((SmsMessage) message).getReceivers().size() >= sendSize) {
                sendMessage(message);
                messagesMap.remove(content);
            }
        }
        // send possible "leftover messages"
        for (Map.Entry<AbstractContentEntity, AbstractMessage> entry : messagesMap.entrySet()) {
            sendMessage(entry.getValue());
        }
        LOG.info("Processing subscriptions, end");
    }

    private void processExpiringSubscriptions(
            List<SubscriptionEntity> subscriptions, String expiryMessage, Integer sendSize) {
        LOG.info("Processing expiring subscriptions, start");
        AbstractMessage message = createExpiryMessage(expiryMessage);
        for (SubscriptionEntity subscription : subscriptions) {
            addPhoneNumberToMessage(subscription, message);

            if (((SmsMessage) message).getReceivers().size() >= sendSize) {
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

    private AbstractMessage createExpiryMessage(String expiryMessage) {
        // TODO: support also for email message(?)
        AbstractMessage message = new SmsMessage();
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

    // TODO: support also for email message(?)
    private AbstractMessage createMessage(
            Map<AbstractContentEntity, AbstractMessage> messagesMap,
            AbstractContentEntity content,
            SubscriptionEntity subscription) throws UnsupportedOperationException {
        AbstractMessage message = messagesMap.get(content);
        if (message == null) {
            message = new SmsMessage();
            message.setMessage(content.getSummary());
            messagesMap.put(content, message);
        }
        addPhoneNumberToMessage(subscription, message);
        return message;
    }

    private void sendMessage(AbstractMessage message) {
        LOG.info(String.format(
                "Sending message, start (%d receivers)",
                ((SmsMessage) message).getReceivers().size())
        );
        messageCenter.sendMessage(message);
        LOG.info("Sending message, end");
    }

    private AbstractContentEntity getContent(
            SubscriptionEntity subscription,
            ScheduledDeliverableEntity scheduledDeliverable,
            SeriesDeliverableEntity[] seriesDeliverableOrdered) {
        // assumption: there is either one scheduledDeliverable..
        if (scheduledDeliverable != null) {
            return scheduledDeliverable.getContent();
        }

        // ..or one or more seriesDeliverables
        for (SubscriptionPeriodEntity period : subscription.getPeriods()) {
            int days = DateUtils.calculateDifferenceInDays(
                    DateUtils.getCurrentDate(),
                    period.getStart());

            if (days < 0 || days > MAXIMUM_LENGTH_FOR_SERIES) {
                // omit, since there cannot be series information for these ones
                // TODO: log error/warning ?
                continue;
            }

            SeriesDeliverableEntity deliverable
                    = seriesDeliverableOrdered[days];
            if (deliverable != null) {
                // TODO: is it possible to have more than one active period?
                return deliverable.getContent();
            }
        }

        return null;
    }
}
