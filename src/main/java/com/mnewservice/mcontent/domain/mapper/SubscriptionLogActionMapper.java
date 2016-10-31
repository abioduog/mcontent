package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.SubscriptionLogAction;
import com.mnewservice.mcontent.repository.entity.SubscriptionLogEntity;
import org.springframework.stereotype.Component;


@Component
public class SubscriptionLogActionMapper
        extends AbstractMapper<SubscriptionLogAction, SubscriptionLogEntity.ActionTypeEnum> {

    @Override
    public SubscriptionLogAction toDomain(SubscriptionLogEntity.ActionTypeEnum entity) {
        if (entity == null) {
            return null;
        }
        return SubscriptionLogAction.valueOf(entity.name());
    }

    @Override
    public SubscriptionLogEntity.ActionTypeEnum toEntity(SubscriptionLogAction domain) {
        if (domain == null) {
            return null;
        }
        return SubscriptionLogEntity.ActionTypeEnum.valueOf(domain.name());
    }

}
