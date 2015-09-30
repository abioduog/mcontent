package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.ProviderEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface ProviderRepository extends CrudRepository<ProviderEntity, Long> {

    public ProviderEntity findByCorrespondencesId(Long correspondencesId);

}
