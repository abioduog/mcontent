package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import java.util.Collection;
import java.util.List;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface SeriesDeliverableRepository
        extends CrudRepository<SeriesDeliverableEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)

    List<SeriesDeliverableEntity> findByDeliveryPipeOrderByDeliveryDaysAfterSubscriptionAsc(
            DeliveryPipeEntity deliveryPipe);

    List<SeriesDeliverableEntity> findByDeliveryPipeAndStatusOrderByDeliveryDaysAfterSubscriptionAsc(
            DeliveryPipeEntity deliveryPipe,
            AbstractDeliverableEntity.DeliverableStatusEnum status);

    Long countByDeliveryPipeId(Long deliveryPipeId);

    @Query("select sd.files from SeriesDeliverableEntity sd "
            + "where sd.id like ?1")
    Collection<FileEntity> findSeriesFiles(Long id);

}
