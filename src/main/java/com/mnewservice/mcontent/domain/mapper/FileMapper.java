/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.ContentFile;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class FileMapper extends AbstractMapper<ContentFile, FileEntity> {

    @Override
    public ContentFile toDomain(FileEntity entity) {
        if (entity == null) {
            return null;
        }
        ContentFile domain = new ContentFile();
        domain.setId(entity.getId());
        domain.setFilename(entity.getFilename());
        domain.setOriginalFilename(entity.getOriginalFilename());
        domain.setMimeType(entity.getMimeType());
        domain.setPath(entity.getPath());

        return domain;
    }

    @Override
    public FileEntity toEntity(ContentFile domain) {
        if (domain == null) {
            return null;
        }
        FileEntity entity = new FileEntity();
        entity.setId(domain.getId());
        entity.setFilename(domain.getFilename());
        entity.setOriginalFilename(domain.getOriginalFilename());
        entity.setMimeType(domain.getMimeType());
        entity.setPath(domain.getPath());
        return entity;
    }

}
