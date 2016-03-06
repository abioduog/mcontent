package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.SmsMessageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Antti Vikman on 6.3.2016.
 */
@Repository
public interface SmsMessageRepository extends CrudRepository<SmsMessageEntity, Long> {
    List<SmsMessageEntity> findBySentIsNullAndTriesLessThan(int numberOfTries);
    List<SmsMessageEntity> findAllByOrderByCreatedDesc();
}
