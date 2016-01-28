/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.FileEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface FileRepository extends CrudRepository<FileEntity, Long> {

    @Query("select fe from FileEntity fe where fe.filename = ?1")
    FileEntity findByFilename(String filename);

}
