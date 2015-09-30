package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.domain.SubscriptionPeriod;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class SubscriptionMapper extends AbstractMapper<Subscription, SubscriptionEntity> {

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private SubscriberMapper subscriberMapper;

    @Autowired
    private SubscriptionPeriodMapper subscriptionPeriodMapper;

    @Override
    public Subscription toDomain(SubscriptionEntity entity) {
        if (entity == null) {
            return null;
        }
        throw new UnsupportedOperationException("not implemented yet.");
    }

    @Override
    public SubscriptionEntity toEntity(Subscription domain) {
        if (domain == null) {
            return null;
        }

        SubscriptionEntity entity = new SubscriptionEntity();
        entity.setId(domain.getId());
        entity.setService(serviceMapper.toEntity(domain.getService()));
        entity.setSubscriber(subscriberMapper.toEntity(domain.getSubscriber()));
        entity.setPeriods(
                subscriptionPeriodMapper.toEntity(
                        domain.getPeriods()).stream().collect(Collectors.toSet()
                )
        );

        return entity;
    }

}
