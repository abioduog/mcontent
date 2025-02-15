package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.ScheduledDeliverableEntity;
import java.util.Date;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface ScheduledDeliverableRepository
        extends CrudRepository<ScheduledDeliverableEntity, Long> {

    ScheduledDeliverableEntity findByDeliveryPipeAndDeliveryDateAndStatus(
            DeliveryPipeEntity deliveryPipe, Date deliveryDate,
            AbstractDeliverableEntity.DeliverableStatusEnum status);

    List<ScheduledDeliverableEntity> findByDeliveryPipeOrderByDeliveryDateAsc(DeliveryPipeEntity entity);
}
