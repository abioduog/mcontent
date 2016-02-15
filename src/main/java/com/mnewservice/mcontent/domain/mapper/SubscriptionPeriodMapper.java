package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.SubscriptionPeriod;
import com.mnewservice.mcontent.repository.entity.SubscriptionPeriodEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class SubscriptionPeriodMapper extends AbstractMapper<SubscriptionPeriod, SubscriptionPeriodEntity> {

    @Override
    public SubscriptionPeriod toDomain(SubscriptionPeriodEntity entity) {
        if (entity == null) {
            return null;
        }
        SubscriptionPeriod domain = new SubscriptionPeriod();
        domain.setId(entity.getId());
        domain.setMessage(entity.getMessage());
        domain.setMessageId(entity.getMessageId());
        domain.setOperator(entity.getOperator());
        domain.setOriginalTimeStamp(entity.getOriginalTimestamp());
        domain.setSender(entity.getSender());
        domain.setShortCode(entity.getShortCode());
        domain.setStart(entity.getStart());
        domain.setEnd(entity.getEnd());

        return domain;
    }

    @Override
    public SubscriptionPeriodEntity toEntity(SubscriptionPeriod domain) {
        if (domain == null) {
            return null;
        }

        SubscriptionPeriodEntity entity = new SubscriptionPeriodEntity();
        entity.setId(domain.getId());
        entity.setStart(domain.getStart());
        entity.setEnd(domain.getEnd());
        entity.setMessage(domain.getMessage());
        entity.setShortCode(domain.getShortCode());
        entity.setOperator(domain.getOperator());
        entity.setOriginalTimestamp(domain.getOriginalTimeStamp());
        entity.setMessageId(domain.getMessageId());
        entity.setSender(domain.getSender());

        return entity;
    }

}
