package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.SeriesDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
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

    List<SeriesDeliverableEntity> findByService(ServiceEntity service);
}
