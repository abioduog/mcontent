package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.HelloEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface HelloRepository extends CrudRepository<HelloEntity, Long> {
}
