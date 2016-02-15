package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.SeriesDeliverable;
import com.mnewservice.mcontent.domain.mapper.SeriesDeliverableMapper;
import com.mnewservice.mcontent.repository.ContentRepository;
import com.mnewservice.mcontent.repository.DeliveryPipeRepository;
import com.mnewservice.mcontent.repository.SeriesDeliverableRepository;
import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import com.mnewservice.mcontent.util.ShortUrlUtils;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class SeriesDeliverableManager {

    @Autowired
    private DeliveryPipeRepository deliveryPipeRepository;

    @Autowired
    private SeriesDeliverableRepository repository;

    @Autowired
    private AbstractDeliverableManager deliverableManager;

    @Autowired
    private SeriesDeliverableMapper seriesMapper;

    @Autowired
    private ContentRepository contentRepository;

    private static final Logger LOG = Logger.getLogger(SeriesDeliverableManager.class);

    @Transactional(readOnly = true)
    public Integer getNextDeliveryDay(Long deliveryPipeId) {
        LOG.info("Getting next delivery day with deliveryPipeId=" + deliveryPipeId);
        return repository.countByDeliveryPipeId(deliveryPipeId).intValue() + 1;
    }

    @Transactional(readOnly = true)
    public Collection<SeriesDeliverable> getDeliveryPipeSeriesDeliverable(long id) {
        LOG.info("Getting series deliverable for delivery pipe with id=" + id);
        DeliveryPipeEntity entity = deliveryPipeRepository.findOne(id);
        List<SeriesDeliverableEntity> deliverables = repository.findByDeliveryPipeOrderByDeliveryDaysAfterSubscriptionAsc(entity);
        return seriesMapper.toDomain(deliverables);
    }

    @Transactional(readOnly = true)
    public SeriesDeliverable getSeriesDeliverable(long id) {
        SeriesDeliverableEntity deliverable = repository.findOne(id);
        return seriesMapper.toDomain(deliverable);
    }

    @Transactional
    public SeriesDeliverable saveSeriesDeliverable(long deliveryPipeId, SeriesDeliverable deliverable) {
        SeriesDeliverableEntity entity = seriesMapper.toEntity(deliverable);
        if (deliverable.getId() == null || deliverable.getId() == 0) {
            entity.setStatus(AbstractDeliverableEntity.DeliverableStatusEnum.PENDING_APPROVAL);
            entity.setDeliveryPipe(deliveryPipeRepository.findOne(deliveryPipeId));
            entity.setDeliveryDaysAfterSubscription((int) (repository.countByDeliveryPipeId(deliveryPipeId) + 1));
        }
        if (entity.getContent().getShortUuid() == null) {
            String shortUuid;
            while (contentRepository.findByShortUuid(shortUuid = ShortUrlUtils.getRandomShortIdentifier()) != null);
            entity.getContent().setShortUuid(shortUuid);
        }

        // TODO: for the providers: allow save if and only if status == PENDING_APPROVAL
        return seriesMapper.toDomain(repository.save(entity));
    }

    @Transactional
    public void removeSeriesDeliverable(Long id) {
        LOG.info("Removing series deliverable with id=" + id);
        SeriesDeliverableEntity entity = repository.findOne(id);
        deliverableManager.removeDeliverable(entity);
    }


}
