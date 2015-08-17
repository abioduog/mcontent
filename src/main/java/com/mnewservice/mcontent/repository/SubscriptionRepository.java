package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public interface SubscriptionRepository extends CrudRepository<SubscriptionEntity, Long> {

}
