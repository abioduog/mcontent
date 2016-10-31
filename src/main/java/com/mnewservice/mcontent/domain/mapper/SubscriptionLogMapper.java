package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.SubscriptionLog;
import com.mnewservice.mcontent.repository.entity.SubscriptionLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionLogMapper extends AbstractMapper<SubscriptionLog, SubscriptionLogEntity> {

    @Autowired
    private SubscriptionLogActionMapper enumMapper;

    @Override
    public SubscriptionLog toDomain(SubscriptionLogEntity entity) {
        if (entity == null) {
            return null;
        }
        SubscriptionLog domain = new SubscriptionLog();
        domain.setId(entity.getId());
        domain.setServiceId(entity.getServiceId());
        domain.setSubscriberId(entity.getSubscriberId());
        domain.setSubscriptionId(entity.getSubscriptionId());
        domain.setTimeStamp(entity.getTimeStamp());
        domain.setAction(enumMapper.toDomain(entity.getAction()));
        return domain;

    }

    @Override
    public SubscriptionLogEntity toEntity(SubscriptionLog domain) {
        if (domain == null) {
            return null;
        }
        SubscriptionLogEntity entity = new SubscriptionLogEntity();
        entity.setId(domain.getId());
        entity.setServiceId(domain.getServiceId());
        entity.setSubscriberId(domain.getSubscriberId());
        entity.setSubscriptionId(domain.getSubscriptionId());
        entity.setTimeStamp(domain.getTimeStamp());
        entity.setAction(enumMapper.toEntity(domain.getAction()));

        return entity;
    }

}
