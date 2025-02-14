package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.BinaryContentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface BinaryContentRepository
        extends CrudRepository<BinaryContentEntity, Long> {

}
