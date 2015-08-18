package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public interface ServiceRepository extends CrudRepository<ServiceEntity, Long> {

    public ServiceEntity findByKeywordAndShortCodeAndOperator(
            String keyword, int shortCode, String operator);
}
