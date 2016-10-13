package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.SmsMessageEntity;
import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public interface SubscriberRepository
        extends CrudRepository<SubscriberEntity, Long> {

    SubscriberEntity findByPhoneNumber(String number);
    
    
    Collection<SubscriberEntity> findAllByOrderByPhoneNumberAsc();

/*    
    @Query("select db from SmsMessageEntity db "
         + "where db.receivers like ?1 order by db.sent desc")
    Collection<SubscriberEntity> findAllByOrderBySentdDesc(String nameFilter); 
*/
    Collection<SubscriberEntity> findByPhoneNumberContainingOrderByPhoneNumberAsc(String phoneNumber);

}
