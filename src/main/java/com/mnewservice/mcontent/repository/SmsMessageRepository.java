package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.SmsMessageEntity;
import java.util.Date;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Antti Vikman on 6.3.2016.
 */
@Repository
public interface SmsMessageRepository extends CrudRepository<SmsMessageEntity, Long> {

    List<SmsMessageEntity> findBySentIsNullAndTriesLessThan(int numberOfTries);

    List<SmsMessageEntity> findAllByOrderByCreatedDesc();

    List<SmsMessageEntity> findAllByOrderByCreatedAsc();

    @Query("select sme from SmsMessageEntity sme "
            + "where sme.sent >= ?1 and sme.receivers like %?2% "
            + "order by sme.sent desc")
    List<SmsMessageEntity> findNewerThanAndReceiverPhone(Date newerThan, String receiverPhone);

    List<SmsMessageEntity> findByReceiversContainingOrderByCreatedDesc(String phoneNumber);
}
