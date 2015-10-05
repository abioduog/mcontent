package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    RoleEntity findByName(RoleEntity.RoleEnum name);
}
