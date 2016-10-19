package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.SubscriptionLogEntity;
import java.util.Collection;
import java.util.Date;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionLogRepository
        extends CrudRepository<SubscriptionLogEntity, Long> {

    Collection<SubscriptionLogEntity> findAllByServiceIdAndSubscriberId(Long serviceId, Long subscriberId);

    Collection<SubscriptionLogEntity> findAllByServiceId(Long serviceId);

    @Query("select se from SubscriptionLogEntity se "
            + "where se.serviceId = :serviceId and se.timeStamp between :firstDate and :lastDate")
    Collection<SubscriptionLogEntity> findAllByServiceIdBetweenDates(@Param("serviceId") Long serviceId,
                                                                     @Param("firstDate") Date firstDate,
                                                         @Param("lastDate") Date lastDate);


}
