package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface SeriesDeliverableRepository
        extends CrudRepository<SeriesDeliverableEntity, Long> {

    List<SeriesDeliverableEntity> findByDeliveryPipeOrderByDeliveryDaysAfterSubscriptionAsc(
            DeliveryPipeEntity deliveryPipe);

    List<SeriesDeliverableEntity> findByDeliveryPipeAndStatusOrderByDeliveryDaysAfterSubscriptionAsc(
            DeliveryPipeEntity deliveryPipe,
            AbstractDeliverableEntity.DeliverableStatusEnum status);

    Long countByDeliveryPipeId(Long deliveryPipeId);

}
