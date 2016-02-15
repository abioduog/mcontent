/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import java.util.Collection;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface AbstractDeliverableRepository extends CrudRepository<AbstractDeliverableEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sd from AbstractDeliverableEntity sd where sd.id = :id")
    public AbstractDeliverableEntity findOneAndLockIt(@Param("id") Long id);

    @Query("select sd.files from AbstractDeliverableEntity sd where sd.id = :id")
    public Collection<FileEntity> findDeliverableFiles(@Param("id") Long id);

    @Query("select sd from AbstractDeliverableEntity sd "
            + "left join sd.files f "
            + "where f.uuid = :uuid")
    public Collection<AbstractDeliverableEntity> findDeliverablesByFileUuid(@Param("uuid") String fileUuid);

}
