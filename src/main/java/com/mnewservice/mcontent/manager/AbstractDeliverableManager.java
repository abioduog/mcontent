/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.AbstractDeliverable;
import com.mnewservice.mcontent.domain.ContentFile;
import com.mnewservice.mcontent.domain.ScheduledDeliverable;
import com.mnewservice.mcontent.domain.SeriesDeliverable;
import com.mnewservice.mcontent.domain.mapper.FileMapper;
import com.mnewservice.mcontent.repository.AbstractDeliverableRepository;
import com.mnewservice.mcontent.repository.ContentRepository;
import com.mnewservice.mcontent.repository.DeliveryPipeRepository;
import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.CustomContentEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
public class AbstractDeliverableManager {

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    DeliveryPipeRepository deliverypipeRepository;

    @Autowired
    FileManager fileManager;

    @Autowired
    AbstractDeliverableRepository repository;

    @Autowired
    private FileMapper fileMapper;

    private static final Logger LOG = Logger.getLogger(AbstractDeliverableManager.class);

    @Transactional(readOnly = true)
    public AbstractDeliverableEntity findOne(Long id) {
        LOG.info("Getting Deliverable with Id=" + id);
        return repository.findOne(id);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void removeFileFromDeliverables(FileEntity fileEntity) {
        String uuid = fileEntity.getUuid().toString();
        LOG.info("Removing from deliverables the file with UUID=" + uuid);
        LOG.info("1. Finding Deliverables with File UUID=" + uuid);
        Collection<AbstractDeliverableEntity> entities = repository.findDeliverablesByFileUuid(uuid);
        for (AbstractDeliverableEntity entity : entities) {
            entity.getFiles().remove(fileEntity);
            LOG.info("2. Removing from deliverable " + entity.getId());
            repository.save(entity);
        }
        LOG.info("3. Removing file ");
        fileManager.deleteFile(fileEntity);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void removeDeliverable(AbstractDeliverableEntity entity) {
        LOG.info("Removing deliverable id=" + entity.getId());

        DeliveryPipeEntity pipeEntity = deliverypipeRepository.findOne(entity.getDeliveryPipe().getId());
        if (pipeEntity != null) {
            if (pipeEntity.getDeliverables().remove(entity)) {
                LOG.info("Removing deliverable from pipe id=" + pipeEntity.getId());
                deliverypipeRepository.save(pipeEntity);
            }
        }

        LOG.info("Removing deliverable's files");
        entity.getFiles().stream().forEach(f -> {
            LOG.info("File: " + f.getOriginalFilename());
            fileManager.deleteFile(f);
        });
        if (entity.getContent() instanceof CustomContentEntity) {
            LOG.info("Removing deliverable's content id=" + entity.getContent().getId());
            contentRepository.delete((CustomContentEntity) entity.getContent());
        }
        repository.delete(entity);
        LOG.info("Removing finished.");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.NESTED)
    public ContentFile addFileAndContent(AbstractDeliverableEntity entity, ContentFile file) {
        LOG.info("Adding image content to deliverable id=" + entity.getId());

        entity = repository.findOneAndLockIt(entity.getId());

        if (entity.getContent() instanceof CustomContentEntity) {
            LOG.info("Generating HTML image block for content");
            CustomContentEntity contentEntity = (CustomContentEntity) entity.getContent();
            LOG.info("HTML before: " + contentEntity.getContent());
            contentEntity.setContent(contentEntity.getContent() + file.createAndSetImageHtmlBlock(entity.getDeliveryPipe().getTheme()));
            LOG.info("HTML after: " + contentEntity.getContent());
            entity.setContent(contentEntity);
        }

        LOG.info("Adding file [" + file.getPath() + "] to deliverable id=" + entity.getId());

        FileEntity fileEntity = fileMapper.toEntity(file);
        entity.getFiles().add(fileEntity);

        entity = repository.save(entity);
        LOG.info("File added [" + file.getPath() + "] to deliverable id=" + entity.getId());
        return file;
    }

    private AbstractDeliverable regenerateImageContent(AbstractDeliverable domain) {
        LOG.info("Re-generating image content for deliverable id=" + domain.getId());

        domain.getContent().setContent("");  // clear all
        AbstractDeliverableEntity entity = repository.findOne(domain.getId());
        String theme = entity.getDeliveryPipe().getTheme();
        for (ContentFile file : domain.getFiles()) {
            // generate html blocks file by file
            LOG.info("Generating HTML image block for file: " + file.getOriginalFilename());
            domain.getContent().setContent(domain.getContent().getContent() + file.createAndSetImageHtmlBlock(theme));
        }
        LOG.info("Content re-generated for deliverable id=" + domain.getId());
        return domain;
    }

    public SeriesDeliverable regenerateSeriesImageContent(SeriesDeliverable domain) {
        return (SeriesDeliverable) regenerateImageContent(domain);
    }

    public ScheduledDeliverable regenerateScheduledImageContent(ScheduledDeliverable domain) {
        return (ScheduledDeliverable) regenerateImageContent(domain);
    }

    @Transactional(readOnly = true)
    public Collection<ContentFile> getDeliverablesFiles(long id) {
        LOG.info("Getting deliverables files with deliverable id=" + id);
        Collection<FileEntity> files = repository.findDeliverableFiles(id);
        return fileMapper.toDomain(files);
    }


}
