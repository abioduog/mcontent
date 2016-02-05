/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.ScheduledDeliverable;
import com.mnewservice.mcontent.domain.mapper.ScheduledDeliverableMapper;
import com.mnewservice.mcontent.repository.ContentRepository;
import com.mnewservice.mcontent.repository.DeliveryPipeRepository;
import com.mnewservice.mcontent.repository.ScheduledDeliverableRepository;
import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.ScheduledDeliverableEntity;
import com.mnewservice.mcontent.util.ShortUrlUtils;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
public class ScheduledDeliverableManager {

    @Autowired
    private DeliveryPipeRepository deliveryPipeRepository;

    @Autowired
    private ScheduledDeliverableRepository repository;

    @Autowired
    private AbstractDeliverableManager deliverableManager;

    @Autowired
    private ScheduledDeliverableMapper scheduledMapper;

    @Autowired
    private ContentRepository contentRepository;

    private static final Logger LOG = Logger.getLogger(ScheduledDeliverableManager.class);

    @Transactional(readOnly = true)
    public Collection<ScheduledDeliverable> getDeliveryPipeScheduledContent(long id) {
        LOG.info("Getting scheduled content for delivery pipe with id=" + id);
        DeliveryPipeEntity entity = deliveryPipeRepository.findOne(id);
        List<ScheduledDeliverableEntity> contents = repository.findByDeliveryPipeOrderByDeliveryDateAsc(entity);
        return scheduledMapper.toDomain(contents);
    }

    @Transactional(readOnly = true)
    public ScheduledDeliverable getScheduledContent(long id) {
        ScheduledDeliverableEntity content = repository.findOne(id);
        return scheduledMapper.toDomain(content);
    }

    @Transactional
    public ScheduledDeliverable saveScheduledContent(long deliveryPipeId, ScheduledDeliverable deliverable) {
        ScheduledDeliverableEntity entity = scheduledMapper.toEntity(deliverable);
        if (deliverable.getId() == null || deliverable.getId() == 0) {
            entity.setStatus(AbstractDeliverableEntity.DeliverableStatusEnum.PENDING_APPROVAL);
            entity.setDeliveryPipe(deliveryPipeRepository.findOne(deliveryPipeId));
        }
        if (entity.getContent().getShortUuid() == null) {
            String shortUuid;
            while (contentRepository.findByShortUuid(shortUuid = ShortUrlUtils.getRandomShortIdentifier()) != null);
            entity.getContent().setShortUuid(shortUuid);
        }

        // TODO: for the providers: allow save if and only if status == PENDING_APPROVAL
        return scheduledMapper.toDomain(repository.save(entity));
    }

    @Transactional
    public void removeScheduledContentAndFiles(Long id) {
        LOG.info("Removing scheduled content with id=" + id);
        ScheduledDeliverableEntity entity = repository.findOne(id);
        deliverableManager.removeDeliverable(entity);
    }

}
