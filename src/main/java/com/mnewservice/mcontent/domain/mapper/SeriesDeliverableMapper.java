package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.ContentFile;
import com.mnewservice.mcontent.domain.SeriesDeliverable;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeriesDeliverableMapper extends AbstractMapper<SeriesDeliverable, SeriesDeliverableEntity> {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private DeliverableStatusMapper deliverableStatusMapper;

    @Override
    public SeriesDeliverable toDomain(SeriesDeliverableEntity entity) {
        if (entity == null) {
            return null;
        }
        SeriesDeliverable domain = new SeriesDeliverable();
        domain.setId(entity.getId());
        domain.setStatus(deliverableStatusMapper.toDomain(entity.getStatus()));
        domain.setContent(contentMapper.toDomain(entity.getContent()));
        domain.setDeliveryDaysAfterSubscription(entity.getDeliveryDaysAfterSubscription());
        domain.setFiles((List<ContentFile>) fileMapper.toDomain(entity.getFiles()));
        return domain;
    }

    @Override
    public SeriesDeliverableEntity toEntity(SeriesDeliverable domain) {
        if (domain == null) {
            return null;
        }
        SeriesDeliverableEntity entity = new SeriesDeliverableEntity();
        entity.setId(domain.getId());
        entity.setStatus(deliverableStatusMapper.toEntity(domain.getStatus()));
        entity.setContent(contentMapper.toEntity(domain.getContent()));
        entity.setDeliveryDaysAfterSubscription(domain.getDeliveryDaysAfterSubscription());
        entity.setFiles((List<FileEntity>) fileMapper.toEntity(domain.getFiles()));
        return entity;
    }

}
