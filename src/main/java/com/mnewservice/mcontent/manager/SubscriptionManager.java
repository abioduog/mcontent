package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.PhoneNumber;
import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.domain.SubscriptionPeriod;
import com.mnewservice.mcontent.domain.SubscriptionLog;
import com.mnewservice.mcontent.domain.SubscriptionLogAction;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@org.springframework.stereotype.Service
public class SubscriptionManager {

    @Autowired
    private MessageCenter messageCenter;

    @Autowired
    private SubscriptionLogManager subscriptionLogManager;

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

    private static final String ERROR_UNSUBSCRIPTION_WAS_NOT_FOUND
            = "Unsubscription was not found with keyword=%s, shortCode=%d, "
            + "operator=%s";

    private static final String ERROR_SERVICE_WAS_NOT_FOUND
            = "Service was not found with keyword=%s, shortCode=%d, "
            + "operator=%s";

    @Transactional(readOnly = true)
    public Collection<Subscription> getAllSubscriptionsByService(Long serviceId) {
        LOG.info("Getting all subscriptions by service id = " + serviceId);
        Collection<SubscriptionEntity> entities = subscriptionMapper.makeCollection(subscriptionRepository.findByServiceId(serviceId));
        return subscriptionMapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Collection<Subscription> getAllSubscriptionsByServices(Collection<Service> services) {
        LOG.info("Getting all subscriptions for collection of services ");
        Collection<Long> serviceIdList = services.stream().map(p -> p.getId()).collect(Collectors.toList());
        Collection<SubscriptionEntity> entities = subscriptionMapper.makeCollection(subscriptionRepository.findByServiceIdIn(serviceIdList));
        return subscriptionMapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Collection<Subscription> getSubscribersDistinctByServices(Collection<Service> services) {
        LOG.info("Getting all subscriptions for collection of services ");
        Collection<Long> serviceIdList = services.stream().map(p -> p.getId()).collect(Collectors.toList());
        Collection<SubscriptionEntity> entities = subscriptionMapper.makeCollection(subscriptionRepository.findDistinctSubscriberByServiceIdIn(serviceIdList));
        return subscriptionMapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Long countSubscribersDistinctByServices(Collection<Service> services) {
        LOG.info("Getting all subscriptions for collection of services ");
        Collection<Long> serviceIdList = services.stream().map(p -> p.getId()).collect(Collectors.toList());
        return subscriptionRepository.countDistinctSubscriberByServiceIdIn(serviceIdList);
    }

    @Transactional
    public void removeSubscription(Subscription subscription) {
        LOG.debug("Removing subscription=" + subscription.getId());
        SubscriptionEntity subscriptionEntity = subscriptionMapper.toEntity(subscription);
        subscriptionRepository.delete(subscriptionEntity.getId());
    }

    @Transactional
    public boolean registerSubscription(Subscription subscription) {
        LOG.debug("registerSubscription() with subscription=" + subscription);

        SubscriptionEntity subscriptionEntity = doRegisterSubscription(subscription);
        Long id = subscriptionEntity.getId();


        /*
         //
         Collection<SubscriptionPeriodEntity> periodEntities
         = subscriptionPeriodMapper.toEntity(subscription.getPeriods());
         */
        SubscriptionEntity savedEntity
                = subscriptionRepository.save(subscriptionEntity);

        if (savedEntity != null && savedEntity.getId() != null) {
            if (savedEntity.getId().equals(id)) {
                subscriptionLogManager.saveSubscriptionLog(new SubscriptionLog(savedEntity, SubscriptionLogAction.RENEWAL));
                sendRenewMessage(savedEntity);
            } else {
                subscriptionLogManager.saveSubscriptionLog(new SubscriptionLog(savedEntity, SubscriptionLogAction.SUBSCRIPTION));
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

    private void mergePeriods(List<SubscriptionPeriodEntity> periods,
            SubscriptionPeriodEntity periodToBeMerged) {

        Date currDate = DateUtils.getCurrentDateAtMidnight();
        Date maxEndDate = periods.stream().map(p -> p.getEnd()).max(Date::compareTo).get();

        if (currDate.compareTo(maxEndDate) < 0) {
            // add periodToBeMerged after latest period
            int diffInDays
                    = DateUtils.calculateDifferenceInDays(
                            maxEndDate, periodToBeMerged.getStart());
            // period starts after previous ends.
            periodToBeMerged.setStart(
                    DateUtils.addDays(periodToBeMerged.getStart(), (diffInDays + 1))
            );
            periodToBeMerged.setEnd(
                    DateUtils.addDays(periodToBeMerged.getEnd(), (diffInDays + 1))
            );

        }

        periods.add(periodToBeMerged);
    }

    private void sendWelcomeMessage(SubscriptionEntity savedEntity) {
        SmsMessage message = createSmsMessage(
                savedEntity.getService().getShortCode(),
                String.format(
                        savedEntity.getService().getWelcomeMessage(),
                        savedEntity.getService().getSubscriptionPeriod()
                ),
                phoneNumberMapper.toDomain(savedEntity.getSubscriber().getPhone())
        );

        sendMessage(message);
    }

    private void sendRenewMessage(SubscriptionEntity savedEntity) {

        SubscriptionPeriodEntity last = null;
        for (SubscriptionPeriodEntity sp : savedEntity.getPeriods()) {
            //System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sp.getEnd()));
            if (last != null) {
                if (sp.getEnd().after(last.getEnd())) {
                    last = sp;
                }
            } else {
                last = sp;
            }
        }
        SmsMessage message = createSmsMessage(
                savedEntity.getService().getShortCode(),
                // how many days and then date
                // In String it is like "Lorem ipsum %d dolor sit %s amet"
                String.format(
                        savedEntity.getService().getRenewMessage(),
                        savedEntity.getService().getSubscriptionPeriod(),
                        new SimpleDateFormat("yyyy-MM-dd").format(last.getEnd())
                ),
                phoneNumberMapper.toDomain(savedEntity.getSubscriber().getPhone())
        );

        sendMessage(message);
    }

    @Transactional
    public boolean unRegisterSubscription(Subscription subscription) {
        LOG.debug("unRegisterSubscription() with subscription=" + subscription);
        LOG.info("unRegisterSubscription() with subscription=" + subscription);

        SubscriptionEntity savedEntity;
        try {
            savedEntity = subscriptionRepository.save(
                    doUnRegisterSubscription(subscription)
            );
        } catch (NoSuchElementException nsee) {
            LOG.error(nsee.getMessage());
            //throw nsee;
            sendUnsubscriptionNotFoundMessage(subscription);
            return false;
        }

        if (savedEntity != null && savedEntity.getId() != null) {
            subscriptionLogManager.saveSubscriptionLog(new SubscriptionLog(savedEntity, SubscriptionLogAction.UNSUBSCRIPTION));
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
                savedEntity.getService().getShortCode(),
                String.format(
                        savedEntity.getService().getUnsubscribeMessage(),
                        savedEntity.getService().getSubscriptionPeriod()
                ),
                phoneNumberMapper.toDomain(savedEntity.getSubscriber().getPhone())
        );

        sendMessage(message);
    }

    private void sendUnsubscriptionNotFoundMessage(Subscription subscription) {
        subscription.getSubscriber().getPhone();
        subscription.getService().getShortCode();
        String msg = String.format(
                ERROR_UNSUBSCRIPTION_WAS_NOT_FOUND,
                subscription.getService().getKeyword(),
                subscription.getService().getShortCode(),
                subscription.getService().getOperator()
        );

           // LOG.info("msg = " + msg);
        SmsMessage message = createSmsMessage(subscription.getService().getShortCode(), msg, subscription.getSubscriber().getPhone());
        //sendMessage()
        sendMessage(message);
    }

    public void sendServiceNotFoundMessage(Integer shortcode, String msg1, String phonenumber) {

        PhoneNumber pn = new PhoneNumber();

        pn.setNumber(phonenumber);
           // LOG.info("msg = " + msg);

        SmsMessage message = createSmsMessage(shortcode, msg1, pn);
        //sendMessage()
        sendMessage(message);
    }

    private SmsMessage createSmsMessage(Integer shortCode, String content, PhoneNumber receiver) {
        SmsMessage message = new SmsMessage();
        message.setFromNumber(shortCode.toString());
        message.setMessage(content);
        message.getReceivers().add(receiver);
        return message;
    }

    private void sendMessage(SmsMessage message) {
        messageCenter.queueMessage(message);
    }

}
