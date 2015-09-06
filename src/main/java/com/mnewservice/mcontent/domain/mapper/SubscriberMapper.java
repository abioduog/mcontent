package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class SubscriberMapper extends AbstractMapper<Subscriber, SubscriberEntity> {

    private static final Logger LOG = Logger.getLogger(SubscriberMapper.class);

    @Autowired
    private PhoneNumberMapper phoneNumberMapper;

    @Override
    public Subscriber toDomain(SubscriberEntity entity) {
        if (entity == null) {
            return null;
        }
        Subscriber domain = new Subscriber();
        LOG.debug("mapping id: " + entity.getId());
        domain.setId(entity.getId());
        LOG.debug("mapping phone: " + entity.getPhone());
        domain.setPhone(phoneNumberMapper.toDomain(entity.getPhone()));

        int activeSubscriptions = 0, inactiveSubscriptions = 0;
        for (SubscriptionEntity subscription : entity.getSubscriptions()) {
            if (subscription.isActive()) {
                activeSubscriptions++;
            } else {
                inactiveSubscriptions++;
            }
        }

        domain.setActiveSubscriptionCount(activeSubscriptions);
        domain.setInactiveSubscriptionCount(inactiveSubscriptions);

        return domain;
    }

    @Override
    public SubscriberEntity toEntity(Subscriber domain) {
        if (domain == null) {
            return null;
        }

        SubscriberEntity entity = new SubscriberEntity();
        LOG.debug("mapping id: " + domain.getId());
        entity.setId(domain.getId());
        LOG.debug("mapping phone: " + domain.getPhone());
        entity.setPhone(phoneNumberMapper.toEntity(domain.getPhone()));

        return entity;
    }

}
