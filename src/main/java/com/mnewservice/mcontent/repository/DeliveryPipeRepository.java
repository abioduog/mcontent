package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface DeliveryPipeRepository extends CrudRepository<DeliveryPipeEntity, Long> {

    @Query("select db from DeliveryPipeEntity db "
            + "left join db.providers p "
            + "where p.username = ?1")
    Collection<DeliveryPipeEntity> findByProvidersUsername(String providerUsername);

    @Query("select db from DeliveryPipeEntity db "
            + "left join db.providers p "
            + "where p.id = ?1")
    Collection<DeliveryPipeEntity> findByProviders(Long providerId);

    @Query("select db.theme from DeliveryPipeEntity db "
            +            "left join db.deliverables d " +
            "where d.content.shortUuid = ?1")
    String getThemeForContentByUuid(String shortUuid);
}
