package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.ProviderEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface ProviderRepository extends CrudRepository<ProviderEntity, Long> {

    ProviderEntity findByCorrespondencesId(Long correspondencesId);

    ProviderEntity findByUserId(Long userId);
    
    ProviderEntity findByEmail(String email);
    
    List<ProviderEntity> findAllByOrderByNameAsc();
    
    @Query("select db from ProviderEntity db "
         + "where db.name like ?1 order by db.name asc")
    Collection<ProviderEntity> findAllByOrderByNameAsc(String nameFilter); 
    
    Collection<ProviderEntity> findByNameContainingOrderByNameAsc(String nameFilter); 

}
