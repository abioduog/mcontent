package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.AbstractContentEntity;
import com.mnewservice.mcontent.repository.entity.CustomContentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository
        extends CrudRepository<CustomContentEntity, Long> {

    CustomContentEntity findByShortUuid(String shortUuid);

    @Query("select c from AbstractDeliverableEntity de " +
            "left join de.deliveryPipe.providers p " +
            "left join de.content c " +
            "where c.shortUuid = ?1 and p.username = ?2")
    CustomContentEntity findByShortUuidAndProviderUsername(String shortUuid, String providerUsername);

    @Query("select cc from SubscriptionEntity se " +
            "left join se.service.deliveryPipe.deliverables d " +
            "left join d.content cc " +
            "where cc.shortUuid = ?1 and se.subscriber.phone.number = ?2")
    CustomContentEntity findByShortUuidWithValidSubscription(String shortUuid, String subscriberNumber);
}
