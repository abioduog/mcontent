package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.ResetpwEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetpwRepository extends CrudRepository<ResetpwEntity, Long> {

    ResetpwEntity findByChecksum(String checksum);
}
