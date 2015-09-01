package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.domain.mapper.SubscriptionMapper;
import com.mnewservice.mcontent.domain.mapper.SubscriptionPeriodMapper;
import com.mnewservice.mcontent.repository.ServiceRepository;
import com.mnewservice.mcontent.repository.SubscriberRepository;
import com.mnewservice.mcontent.repository.SubscriptionRepository;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionPeriodEntity;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class SubscriptionManager {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Autowired
    private SubscriptionPeriodMapper subscriptionPeriodMapper;

    private static final Logger LOG = Logger.getLogger(SubscriptionManager.class);

    @Transactional
    public boolean registerSubscription(Subscription subscription) {
        LOG.debug("registerSubscription() with subscription=" + subscription);

        SubscriptionEntity savedEntity
                = subscriptionRepository.save(doRegisterSubscription(subscription));
        if (savedEntity != null && savedEntity.getId() != null) {
            LOG.info("Saved entity with id " + savedEntity.getId());
            return true;
        } else {
            LOG.error("Failed to save entity!");
            return false;
        }
    }

    private SubscriptionEntity doRegisterSubscription(Subscription subscription) {
        String serviceKeyword = subscription.getService().getKeyword();
        int serviceShortCode = subscription.getService().getShortCode();
        String serviceOperator = subscription.getService().getOperator();
        String subscriberPhoneNumber
                = subscription.getSubscriber().getPhone().getNumber();

        SubscriptionEntity subscriptionEntity = subscriptionRepository.
                findByServiceKeywordAndServiceShortCodeAndServiceOperatorAndSubscriberPhoneNumber(
                        serviceKeyword, serviceShortCode, serviceOperator, subscriberPhoneNumber
                );

        if (subscriptionEntity != null) {
            Collection<SubscriptionPeriodEntity> periodEntities
                    = subscriptionPeriodMapper.toEntity(subscription.getPeriods());
            // TODO: logic for merging periods
            subscriptionEntity.getPeriods().addAll(periodEntities);
        } else {
            subscriptionEntity = createSubscription(
                    subscription, serviceKeyword, serviceShortCode,
                    serviceOperator, subscriberPhoneNumber
            );
        }
        return subscriptionEntity;
    }

    private SubscriptionEntity createSubscription(
            Subscription subscription, String serviceKeyword,
            int serviceShortCode, String serviceOperator,
            String subscriberPhoneNumber) {
        SubscriptionEntity subscriptionEntity
                = subscriptionMapper.toEntity(subscription);
        ServiceEntity serviceEntity = serviceRepository.
                findByKeywordAndShortCodeAndOperator(
                        serviceKeyword, serviceShortCode, serviceOperator
                );
        if (serviceEntity != null) {
            subscriptionEntity.setService(serviceEntity);
        }
        SubscriberEntity subscriberEntity
                = subscriberRepository.findByPhoneNumber(subscriberPhoneNumber);
        if (subscriberEntity != null) {
            subscriptionEntity.setSubscriber(subscriberEntity);
        }
        return subscriptionEntity;
    }

    public boolean unRegisterSubscription(Subscription subscription) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
