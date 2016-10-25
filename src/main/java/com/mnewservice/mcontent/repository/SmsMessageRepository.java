package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.ProviderEntity;
import com.mnewservice.mcontent.repository.entity.SmsMessageEntity;
import java.util.Collection;
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
    //List<SmsMessageEntity> findByReceiversByOrderByCreatedDesc();
        @Query("select db from SmsMessageEntity db "
         + "where db.receivers like ?1 order by db.sent desc")
    List<SmsMessageEntity> findAllByOrderBySentDesc(String nameFilter); 
    
    List<SmsMessageEntity> findByReceiversContainingOrderByCreatedDesc(String phoneNumber);
}
