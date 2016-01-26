package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class SubscriberMapper extends AbstractMapper<Subscriber, SubscriberEntity> {

    @Autowired
    private PhoneNumberMapper phoneNumberMapper;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Override
    public Subscriber toDomain(SubscriberEntity entity) {
        if (entity == null) {
            return null;
        }
        Subscriber domain = new Subscriber();
        domain.setId(entity.getId());
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

    public Subscriber toDomainWithSubscriptions(SubscriberEntity entity, boolean showAll) {
        if (entity == null) {
            return null;
        }
        Subscriber domain = new Subscriber();
        domain.setId(entity.getId());
        domain.setPhone(phoneNumberMapper.toDomain(entity.getPhone()));
        domain.setSubscriptions(new HashSet<>());

        int activeSubscriptions = 0, inactiveSubscriptions = 0;
        for (SubscriptionEntity subscription : entity.getSubscriptions()) {
            if (subscription.isActive()) {
                activeSubscriptions++;
                domain.getSubscriptions().add(subscriptionMapper.toDomain(subscription));
            } else {
                inactiveSubscriptions++;
                if (showAll) {
                    domain.getSubscriptions().add(subscriptionMapper.toDomain(subscription));
                }
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
        entity.setId(domain.getId());
        entity.setPhone(phoneNumberMapper.toEntity(domain.getPhone()));

        return entity;
    }

}
