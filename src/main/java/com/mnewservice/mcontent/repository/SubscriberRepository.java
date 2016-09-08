package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import java.util.Collection;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public interface SubscriberRepository
        extends CrudRepository<SubscriberEntity, Long> {

    SubscriberEntity findByPhoneNumber(String number);
    Collection<SubscriberEntity> findAllByOrderByPhoneNumberAsc();
}
