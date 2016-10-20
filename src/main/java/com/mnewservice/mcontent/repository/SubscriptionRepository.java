package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface SubscriptionRepository
        extends CrudRepository<SubscriptionEntity, Long> {

    SubscriptionEntity findByServiceKeywordAndServiceShortCodeAndServiceOperatorAndSubscriberPhoneNumber(
            String serviceKeyword, int serviceShortCode, String serviceOperator, String subscriberPhoneNumber);

    SubscriptionEntity findByServiceIdAndSubscriberPhoneNumber(
            Long serviceId, String phoneNumber);

    SubscriptionEntity findByServiceIdAndSubscriberId(
            Long serviceId, Long subscriberId);

    Page<SubscriptionEntity> findByServiceAndPeriodsStartLessThanAndPeriodsEndGreaterThan(
            ServiceEntity service, Date d1, Date d2, Pageable pageable);

    Page<SubscriptionEntity> findByPeriodsEnd(Date end, Pageable pageable);

    @Query(nativeQuery = true)
    List<SubscriptionEntity> findByExpiry(
            @Param("serviceId") Long serviceId,
            @Param("expiryAt") Date expiryAt,
            @Param("minDuration") Integer minDuration,
            @Param("limit") Integer limit,
            @Param("offset") Long offsest
    );

    List<SubscriptionEntity> findByServiceId(Long serviceId);

    List<SubscriptionEntity> findByServiceIdIn(Collection<Long> services);

}
