package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.ScheduledDeliverable;
import com.mnewservice.mcontent.repository.entity.ScheduledDeliverableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledDeliverableMapper extends AbstractMapper<ScheduledDeliverable, ScheduledDeliverableEntity> {

    @Autowired
    private ContentMapper contentMapper;

    @Override
    public ScheduledDeliverable toDomain(ScheduledDeliverableEntity entity) {
        ScheduledDeliverable domain = new ScheduledDeliverable();
        domain.setId(entity.getId());
        domain.setContent(contentMapper.toDomain(entity.getContent()));
        domain.setDeliveryDate(entity.getDeliveryDate());
        return domain;
    }

    @Override
    public ScheduledDeliverableEntity toEntity(ScheduledDeliverable domain) {
        ScheduledDeliverableEntity entity = new ScheduledDeliverableEntity();
        entity.setId(domain.getId());
        entity.setContent(contentMapper.toEntity(domain.getContent()));
        entity.setDeliveryDate(domain.getDeliveryDate());
        return entity;
    }

}
