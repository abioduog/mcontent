package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Content;
import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.domain.ScheduledDeliverable;
import com.mnewservice.mcontent.domain.SeriesDeliverable;
import com.mnewservice.mcontent.domain.mapper.ContentMapper;
import com.mnewservice.mcontent.domain.mapper.DeliveryPipeMapper;
import com.mnewservice.mcontent.domain.mapper.ScheduledDeliverableMapper;
import com.mnewservice.mcontent.domain.mapper.SeriesDeliverableMapper;
import com.mnewservice.mcontent.repository.ContentRepository;
import com.mnewservice.mcontent.repository.DeliveryPipeRepository;
import com.mnewservice.mcontent.repository.ScheduledDeliverableRepository;
import com.mnewservice.mcontent.repository.SeriesDeliverableRepository;
import com.mnewservice.mcontent.repository.entity.AbstractContentEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.ScheduledDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@org.springframework.stereotype.Service
@javax.transaction.Transactional
public class DeliveryPipeManager {

    @Autowired
    private DeliveryPipeRepository repository;

    @Autowired
    private SeriesDeliverableRepository seriesRepository;

    @Autowired
    private ScheduledDeliverableRepository scheduledRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private DeliveryPipeMapper mapper;

    @Autowired
    private SeriesDeliverableMapper seriesMapper;

    @Autowired
    private ScheduledDeliverableMapper scheduledMapper;

    @Autowired
    private ContentMapper contentdMapper;

    private static final Logger LOG = Logger.getLogger(DeliveryPipeManager.class);

//<editor-fold defaultstate="collapsed" desc="delivery pipes">
    @Transactional(readOnly = true)
    public Collection<DeliveryPipe> getAllDeliveryPipes() {
        LOG.info("Getting all delivery pipes");
        Collection<DeliveryPipeEntity> entities = mapper.makeCollection(repository.findAll());
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public DeliveryPipe getDeliveryPipe(long id) {
        LOG.info("Getting delivery pipe with id=" + id);
        DeliveryPipeEntity entity = repository.findOne(id);
        return mapper.toDomain(entity);
    }

    @Transactional
    public DeliveryPipe saveDeliveryPipe(DeliveryPipe service) {
        LOG.info("Saving delivery pipe with id=" + service.getId());
        DeliveryPipeEntity entity = mapper.toEntity(service);
        return mapper.toDomain(repository.save(entity));
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="series content">
    @Transactional(readOnly = true)
    public Collection<SeriesDeliverable> getDeliveryPipeSeriesContent(long id) {
        LOG.info("Getting series content for delivery with id=" + id);
        DeliveryPipeEntity entity = repository.findOne(id);
        List<SeriesDeliverableEntity> contents = seriesRepository.findByDeliveryPipeOrderByDeliveryDaysAfterSubscriptionAsc(entity);
        return seriesMapper.toDomain(contents);
    }

    @Transactional(readOnly = true)
    public SeriesDeliverable getSeriesContent(long id) {
        SeriesDeliverableEntity content = seriesRepository.findOne(id);
        return seriesMapper.toDomain(content);
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="scheduled content">
    @Transactional(readOnly = true)
    public Collection<ScheduledDeliverable> getDeliveryPipeScheduledContent(long id) {
        LOG.info("Getting scheduled content for delivery with id=" + id);
        DeliveryPipeEntity entity = repository.findOne(id);
        List<ScheduledDeliverableEntity> contents = scheduledRepository.findByDeliveryPipeOrderByDeliveryDateAsc(entity);
        return scheduledMapper.toDomain(contents);
    }

    @Transactional(readOnly = true)
    public ScheduledDeliverable getScheduledContent(long id) {
        ScheduledDeliverableEntity content = scheduledRepository.findOne(id);
        return scheduledMapper.toDomain(content);
    }
//</editor-fold>


    public SeriesDeliverable saveSeriesContent(long deliveryPipeId, SeriesDeliverable deliverable) {
        SeriesDeliverableEntity entity = seriesMapper.toEntity(deliverable);
        if (deliverable.getId() == null || deliverable.getId() == 0) {
            entity.setDeliveryPipe(repository.findOne(deliveryPipeId));
            entity.setDeliveryDaysAfterSubscription((int) (seriesRepository.countByDeliveryPipeId(deliveryPipeId) + 1));
        }
        return seriesMapper.toDomain(seriesRepository.save(entity));
    }

    public ScheduledDeliverable saveScheduledContent(long deliveryPipeId, ScheduledDeliverable deliverable) {
        ScheduledDeliverableEntity entity = scheduledMapper.toEntity(deliverable);
        if (deliverable.getId() == null || deliverable.getId() == 0) {
            entity.setDeliveryPipe(repository.findOne(deliveryPipeId));
        }
        return scheduledMapper.toDomain(scheduledRepository.save(entity));
    }

    public Content getContentByUuid(String shortUuid) {
        AbstractContentEntity entity = contentRepository.findByShortUuid(shortUuid);
        return contentdMapper.toDomain(entity);
    }
}
