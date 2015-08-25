package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface SubscriptionRepository extends CrudRepository<SubscriptionEntity, Long> {

    List<SubscriptionEntity> findByService(ServiceEntity service); // TODO: find only valid ones (where subscription is still active)
}
