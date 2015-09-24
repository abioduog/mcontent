package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.SeriesDeliverable;
import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeriesDeliverableMapper extends AbstractMapper<SeriesDeliverable, SeriesDeliverableEntity> {

    @Autowired
    private ContentMapper contentMapper;

    @Override
    public SeriesDeliverable toDomain(SeriesDeliverableEntity entity) {
        if (entity == null) {
            return null;
        }
        SeriesDeliverable domain = new SeriesDeliverable();
        domain.setId(entity.getId());
        domain.setContent(contentMapper.toDomain(entity.getContent()));
        domain.setDeliveryDaysAfterSubscription(entity.getDeliveryDaysAfterSubscription());
        return domain;
    }

    @Override
    public SeriesDeliverableEntity toEntity(SeriesDeliverable domain) {
        if (domain == null) {
            return null;
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
