package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.DeliveryTime;
import com.mnewservice.mcontent.domain.PhoneNumber;
import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.domain.mapper.PhoneNumberMapper;
import com.mnewservice.mcontent.messaging.MessageCenter;
import com.mnewservice.mcontent.repository.ScheduledDeliverableRepository;
import com.mnewservice.mcontent.repository.SeriesDeliverableRepository;
import com.mnewservice.mcontent.repository.ServiceRepository;
import com.mnewservice.mcontent.repository.SubscriptionRepository;
import com.mnewservice.mcontent.repository.entity.AbstractContentEntity;
import com.mnewservice.mcontent.repository.entity.CustomContentEntity;
import com.mnewservice.mcontent.repository.entity.ScheduledDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import com.mnewservice.mcontent.util.DateUtils;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class DeliveryManager {

    private static final Logger LOG = Logger.getLogger(DeliveryManager.class);

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

    public void deliverContent(DeliveryTime deliveryTime) {
        LOG.debug("deliveryTime: " + deliveryTime);
        for (ServiceEntity service : serviceRepository.findAll()) {
            LOG.debug("service in process: " + service.getId() + "; " + service.getKeyword() + "; " + service.getOperator());

            Date currentDateAtMidnight = DateUtils.getCurrentDateAtMidnight();
            LOG.debug("currentDateAtMidnight: " + currentDateAtMidnight);
            List<ScheduledDeliverableEntity> scheduledDeliverables
                    = scheduledDeliverableRepository
                    .findByServiceAndDeliveryDate(service, currentDateAtMidnight);
            LOG.debug("scheduledDeliverables found: " + scheduledDeliverables.size());
            List<SeriesDeliverableEntity> seriesDeliverables
                    = seriesDeliverableRepository.findByService(service);
            LOG.debug("seriesDeliverables found: " + seriesDeliverables.size());
            for (SubscriptionEntity subscription : subscriptionRepository.findByService(service)) {
                LOG.debug("subscription in process: id=" + subscription.getId() + "; phone=" + subscription.getSubscriber().getPhone().getNumber());
                AbstractContentEntity content
                        = getContent(subscription, scheduledDeliverables, seriesDeliverables);
                LOG.debug("content summary: " + content.getSummary());

                // TODO: support also for email message(?)
                // TODO: support for sending message same time for multiple recipients
                SmsMessage message = new SmsMessage();
                PhoneNumber phoneNumber = phoneNumberMapper.toDomain(subscription.getSubscriber().getPhone());
                message.getReceivers().add(phoneNumber);
                if (content instanceof CustomContentEntity) {
                    message.setMessage(((CustomContentEntity) content).getContent());
                    messageCenter.sendMessage(message);
                } else {
                    // TODO
                    throw new UnsupportedOperationException("not implemented");
                }

                // TODO: send or add to the send queue
            }
        }

    }

    public void deliverExpirationNotification(DeliveryTime deliveryTime) {
        LOG.debug("deliveryTime: " + deliveryTime);
        //TODO implement
    }

    private AbstractContentEntity getContent(
            SubscriptionEntity subscription,
            List<ScheduledDeliverableEntity> scheduledDeliverables,
            List<SeriesDeliverableEntity> seriesDeliverables) {
        // TODO: implement properly
        if (scheduledDeliverables != null && scheduledDeliverables.size() > 0) {
            return scheduledDeliverables.get(0).getContent();
        }
        throw new UnsupportedOperationException("not implemented yet");
    }
}
