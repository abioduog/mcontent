package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.PhoneNumber;
import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.domain.mapper.PhoneNumberMapper;
import com.mnewservice.mcontent.domain.mapper.SubscriptionMapper;
import com.mnewservice.mcontent.domain.mapper.SubscriptionPeriodMapper;
import com.mnewservice.mcontent.messaging.MessageCenter;
import com.mnewservice.mcontent.repository.ServiceRepository;
import com.mnewservice.mcontent.repository.SubscriberRepository;
import com.mnewservice.mcontent.repository.SubscriptionRepository;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionPeriodEntity;
import com.mnewservice.mcontent.util.DateUtils;
import com.mnewservice.mcontent.util.exception.MessagingException;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;
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
    private MessageCenter messageCenter;

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

    @Autowired
    private PhoneNumberMapper phoneNumberMapper;

    private static final Logger LOG = Logger.getLogger(SubscriptionManager.class);

    private static final String ERROR_SUBSCRIPTION_WAS_NOT_FOUND
            = "Subscription was not found with keyword=%s, shortCode=%d, "
            + "operator=%s, and phone number=%s";

    @Transactional
    public boolean registerSubscription(Subscription subscription) {
        LOG.debug("registerSubscription() with subscription=" + subscription);

        SubscriptionEntity subscriptionEntity = doRegisterSubscription(subscription);
        Long id = subscriptionEntity.getId();

        SubscriptionEntity savedEntity
                = subscriptionRepository.save(subscriptionEntity);
        if (savedEntity != null && savedEntity.getId() != null) {
            if (savedEntity.getId().equals(id)) {
                sendRenewMessage(savedEntity);
            } else {
                sendWelcomeMessage(savedEntity);
            }
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

    private void sendWelcomeMessage(SubscriptionEntity savedEntity) {
        SmsMessage message = createSmsMessage(
                String.format(
                        savedEntity.getService().getWelcomeMessage(),
                        savedEntity.getService().getSubscriptionPeriod()
                ),
                phoneNumberMapper.toDomain(savedEntity.getSubscriber().getPhone())
        );

        sendMessge(message, savedEntity.getService().getShortCode());
    }

    private void sendRenewMessage(SubscriptionEntity savedEntity) {
        SmsMessage message = createSmsMessage(
                String.format(
                        savedEntity.getService().getRenewMessage(),
                        savedEntity.getService().getSubscriptionPeriod()
                ),
                phoneNumberMapper.toDomain(savedEntity.getSubscriber().getPhone())
        );

        sendMessge(message, savedEntity.getService().getShortCode());
    }

    @Transactional
    public boolean unRegisterSubscription(Subscription subscription) {
        LOG.debug("unRegisterSubscription() with subscription=" + subscription);

        SubscriptionEntity savedEntity;
        try {
            savedEntity = subscriptionRepository.save(
                    doUnRegisterSubscription(subscription)
            );
        } catch (NoSuchElementException nsee) {
            LOG.error(nsee.getMessage());
            throw nsee;
        }

        if (savedEntity != null && savedEntity.getId() != null) {
            sendUnsubscribeMessage(savedEntity);
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

        if (subscriptionEntity == null) {
            String msg = String.format(
                    ERROR_SUBSCRIPTION_WAS_NOT_FOUND,
                    subscription.getService().getKeyword(),
                    subscription.getService().getShortCode(),
                    subscription.getService().getOperator(),
                    subscription.getSubscriber().getPhone().getNumber()
            );
            throw new NoSuchElementException(msg);
        }

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

    private void sendUnsubscribeMessage(SubscriptionEntity savedEntity) {
        SmsMessage message = createSmsMessage(
                String.format(
                        savedEntity.getService().getUnsubscribeMessage(),
                        savedEntity.getService().getSubscriptionPeriod()
                ),
                phoneNumberMapper.toDomain(savedEntity.getSubscriber().getPhone())
        );

        sendMessge(message, savedEntity.getService().getShortCode());
    }

    private SmsMessage createSmsMessage(String content, PhoneNumber receiver) {
        SmsMessage message = new SmsMessage();
        message.setMessage(content);
        message.getReceivers().add(receiver);
        return message;
    }

    private void sendMessge(SmsMessage message, int shortCode) {
        try {
            messageCenter.sendMessage(message, shortCode);
        } catch (MessagingException ex) {
            LOG.error("Sending message failed: " + ex.getMessage());
            throw new RuntimeException(ex);
        } finally {
        }
    }
}
