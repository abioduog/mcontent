package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.ScheduledDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
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

    ScheduledDeliverableEntity findByServiceAndDeliveryDate(
            ServiceEntity service, Date deliveryDate);
}
