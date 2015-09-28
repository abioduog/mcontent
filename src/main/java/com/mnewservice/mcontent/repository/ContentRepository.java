package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.AbstractContentEntity;
import com.mnewservice.mcontent.repository.entity.CustomContentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository
        extends CrudRepository<CustomContentEntity, Long> {

    CustomContentEntity findByShortUuid(String shortUuid);
}
