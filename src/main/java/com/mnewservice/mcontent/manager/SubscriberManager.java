package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.domain.mapper.SubscriberMapper;
import com.mnewservice.mcontent.repository.SubscriberRepository;
import com.mnewservice.mcontent.repository.entity.RoleEntity;
import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import com.mnewservice.mcontent.util.SessionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@org.springframework.stereotype.Service
public class SubscriberManager {

    @Autowired
    private SubscriberRepository repository;


    @Autowired
    private ServiceManager serviceManager;

    @Autowired
    private SubscriptionManager subscriptionManager;

    @Autowired
    private SubscriberMapper mapper;

    private static final Logger LOG = Logger.getLogger(SubscriberManager.class);

    @Transactional(readOnly = true)
    public Subscriber getSubscriber(String number) {
        LOG.info("Looking subscriber with number=" + number);
        SubscriberEntity entity = repository.findByPhoneNumber(number);
        return mapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public Collection<Subscriber> getAllSubscribers() {
        LOG.info("Getting all subscribers");
        EnumSet<RoleEntity.RoleEnum> roles = SessionUtils.getCurrentUserRoles();
        Collection<SubscriberEntity> entities = new ArrayList<>();
        if (roles.contains(RoleEntity.RoleEnum.ADMIN)) {
            entities = mapper.makeCollection(repository.findAll());
        } else if (roles.contains(RoleEntity.RoleEnum.PROVIDER)) {
            Collection<Service> services = serviceManager.getAllServices();
            Collection<Subscription> subscriptions = subscriptionManager.getSubscribersDistinctByServices(services);
            Collection<Subscriber> retval = subscriptions.stream().map(p -> p.getSubscriber()).collect(Collectors.toList());
            return retval;
        }
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Long countAllSubscribers() {
        LOG.info("Getting all subscribers");
        EnumSet<RoleEntity.RoleEnum> roles = SessionUtils.getCurrentUserRoles();
        if (roles.contains(RoleEntity.RoleEnum.ADMIN)) {
            return new Long(repository.count());
        } else if (roles.contains(RoleEntity.RoleEnum.PROVIDER)) {
            Collection<Service> services = serviceManager.getAllServices();
            return subscriptionManager.countSubscribersDistinctByServices(services);
        }
        return 0L;
    }

    @Transactional(readOnly = true)
    public Collection<Subscriber> getAllSubscribersOrderByPhoneNumberAsc() {
        LOG.info("Getting all subscribers order by phonenumber asc");
        Collection<SubscriberEntity> entities = repository.findAllByOrderByPhoneNumberAsc();
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Collection<Subscriber> findByPhoneNumberContaining(String phoneNumber) {
        LOG.info("Getting all subscribers order by phonenumber asc (" + phoneNumber + ")");
        //Collection<SubscriberEntity> entities = repository.findByPhoneNumberContainingOrderByPhoneNumberAsc(phoneNumber);
        Collection<SubscriberEntity> entities = repository.findByPhoneNumberContainingOrderByPhoneNumberAsc(phoneNumber);
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Subscriber getSubscriber(long id) {
        LOG.info("Getting subscriber with id=" + id);
        SubscriberEntity entity = repository.findOne(id);
        return mapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public Subscriber getSubscriberWithSubscriptions(long id, boolean showAll) {
        LOG.info("Getting subscriber with id=" + id);
        SubscriberEntity entity = repository.findOne(id);
        return mapper.toDomainWithSubscriptions(entity, showAll);
    }

    @Transactional(readOnly = true)
    public Subscriber getSubscriberWithSubscriptions(String phone, boolean showAll) {
        LOG.info("Getting subscriber with phone=" + phone);
        SubscriberEntity entity = repository.findByPhoneNumber(phone);
        return mapper.toDomainWithSubscriptions(entity, showAll);
    }

    @Transactional
    public void removeSubscriber(long id) {
        LOG.info("Removing subscriber with id=" + id);
        SubscriberEntity entity = repository.findOne(id);
        repository.delete(entity);
    }

    @Transactional
    public void removeSubscriptions(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
