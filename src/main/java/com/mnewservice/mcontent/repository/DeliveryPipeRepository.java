package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface DeliveryPipeRepository extends CrudRepository<DeliveryPipeEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select db from DeliveryPipeEntity db where db.id = :id")
    public DeliveryPipeEntity findOneAndLockIt(@Param("id") Long id);

    @Query("select db from DeliveryPipeEntity db "
            + "where db.name like ?1")
    Collection<DeliveryPipeEntity> findAll(String nameFilter);
    
        @Query("select db from DeliveryPipeEntity db "
            + "where db.name like ?1 order by name asc")
    Collection<DeliveryPipeEntity> findAllByOrderByName(String nameFilter);

    @Query("select db from DeliveryPipeEntity db "
            + "left join db.providers u "
            + "where db.name like ?1 and u.username = ?2")
    Collection<DeliveryPipeEntity> findByProvidersUsername(String nameFilter, String providerUsername);

    @Query("select db from DeliveryPipeEntity db "
            + "left join db.providers u "
            + "where u.id = (select p.user.id from ProviderEntity p where p.id = ?1)")
    Collection<DeliveryPipeEntity> findByProviders(Long providerId);

    @Query("select db.theme from DeliveryPipeEntity db "
            + "left join db.deliverables d "
            + "where d.content.shortUuid = ?1")
    String getThemeForContentByUuid(String shortUuid);
}
