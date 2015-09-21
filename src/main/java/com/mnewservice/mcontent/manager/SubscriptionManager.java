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
import com.mnewservice.mcontent.util.DateUtils;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
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
        if (subscription.getPeriods() == null || subscription.getPeriods().size() != 1) {
            throw new IllegalArgumentException("assumed one and only one period");
        }

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
            Optional<SubscriptionPeriodEntity> period = periodEntities.stream().findFirst();
            mergePeriods(subscriptionEntity.getPeriods(), period.get());

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

    private void mergePeriods(Set<SubscriptionPeriodEntity> periods,
            SubscriptionPeriodEntity periodToBeMerged) {

        Date currDate = DateUtils.getCurrentDateAtMidnight();
        Date maxEndDate = periods.stream().map(p -> p.getEnd()).max(Date::compareTo).get();

        if (currDate.compareTo(maxEndDate) < 0) {
            // add periodToBeMerged after latest period
            int diffInDays
                    = DateUtils.calculateDifferenceInDays(
                            maxEndDate, periodToBeMerged.getStart());
            periodToBeMerged.setStart(
                    DateUtils.addDays(periodToBeMerged.getStart(), diffInDays)
            );
            periodToBeMerged.setEnd(
                    DateUtils.addDays(periodToBeMerged.getEnd(), diffInDays)
            );

        }

        periods.add(periodToBeMerged);
    }

    @Transactional
    public boolean unRegisterSubscription(Subscription subscription) {
        LOG.debug("unRegisterSubscription() with subscription=" + subscription);

        SubscriptionEntity savedEntity
                = subscriptionRepository.save(doUnRegisterSubscription(subscription));
        if (savedEntity != null && savedEntity.getId() != null) {
            LOG.info("Saved entity with id " + savedEntity.getId());
            return true;
        } else {
            LOG.error("Failed to save entity!");
            return false;
        }
    }

    private SubscriptionEntity doUnRegisterSubscription(Subscription subscription) {
        SubscriptionEntity subscriptionEntity = subscriptionRepository.
                findByServiceKeywordAndServiceShortCodeAndServiceOperatorAndSubscriberPhoneNumber(
                        subscription.getService().getKeyword(),
                        subscription.getService().getShortCode(),
                        subscription.getService().getOperator(),
                        subscription.getSubscriber().getPhone().getNumber()
                );

        Date currDate = DateUtils.getCurrentDateAtMidnight();
        for (SubscriptionPeriodEntity period : subscriptionEntity.getPeriods()) {
            if (period.getStart().compareTo(currDate) > 0) {
                period.setStart(currDate);
            }
            if (period.getEnd().compareTo(currDate) > 0) {
                period.setEnd(currDate);
            }
        }

        return subscriptionEntity;
    }
}
