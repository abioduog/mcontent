/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.ContentFile;
import com.mnewservice.mcontent.domain.mapper.FileMapper;
import com.mnewservice.mcontent.repository.AbstractDeliverableRepository;
import com.mnewservice.mcontent.repository.ContentRepository;
import com.mnewservice.mcontent.repository.DeliveryPipeRepository;
import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.CustomContentEntity;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import com.mnewservice.mcontent.util.ShortUrlUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private ContentRepository contentRepository;

    @Autowired
    private DeliveryPipeRepository deliveryPipeRepository;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private AbstractDeliverableRepository repository;

    @Autowired
    private FileMapper fileMapper;

    @Value("${application.content.themes.autogenerated}")
    private String autogeratedthemes;

    private static final Logger LOG = Logger.getLogger(AbstractDeliverableManager.class);

    private boolean isAutogeneratedTheme(String theme) {
        return Arrays.asList(autogeratedthemes.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList()).contains(theme);
    }

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
            LOG.info("2. Locking deliverable " + entity.getId());
            entity = repository.findOneAndLockIt(entity.getId());
            LOG.info("3. Removing from deliverable " + entity.getId());
            entity.getFiles().remove(fileEntity);
            LOG.info("4. Regenerating content for deliverable " + entity.getId());
            entity = this.regenerateImageContent(entity);
            repository.save(entity);
        }
        LOG.info("5. Removing file ");
        fileManager.deleteFile(fileEntity);

    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void removeDeliverable(AbstractDeliverableEntity entity) {
        LOG.info("Removing deliverable id=" + entity.getId());

        DeliveryPipeEntity pipeEntity = deliveryPipeRepository.findOne(entity.getDeliveryPipe().getId());
        if (pipeEntity != null) {
            if (pipeEntity.getDeliverables().remove(entity)) {
                LOG.info("Removing deliverable from pipe id=" + pipeEntity.getId());
                deliveryPipeRepository.save(pipeEntity);
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

    private void autoGenerateContent(AbstractDeliverableEntity entity, FileEntity file) {
        if (entity.getContent() instanceof CustomContentEntity && isAutogeneratedTheme(entity.getDeliveryPipe().getTheme())) {
            LOG.info("Generating HTML image block for content");
            CustomContentEntity contentEntity = (CustomContentEntity) entity.getContent();
            contentEntity.setContent(contentEntity.getContent() + fileMapper.toDomain(file).createAndSetImageHtmlBlock());
            entity.setContent(contentEntity);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.NESTED)
    public FileEntity addFile(AbstractDeliverableEntity entity, FileEntity file) {
        LOG.info("Adding image content to deliverable id=" + entity.getId());
        entity = repository.findOneAndLockIt(entity.getId());
        entity.getFiles().add(file);
        entity = repository.save(entity);
        LOG.info("File added [" + file.getPath() + "] to deliverable id=" + entity.getId());
        return file;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.NESTED)
    public ContentFile addFileAndContent(AbstractDeliverableEntity entity, ContentFile file) {
        LOG.info("Adding image content to deliverable id=" + entity.getId());
        entity = repository.findOneAndLockIt(entity.getId());
        FileEntity fileEntity = fileMapper.toEntity(file);
        this.autoGenerateContent(entity, fileEntity);
        entity.getFiles().add(fileEntity);
        entity = repository.save(entity);
        LOG.info("File added [" + file.getPath() + "] to deliverable id=" + entity.getId());
        return file;
    }

    public AbstractDeliverableEntity regenerateImageContent(AbstractDeliverableEntity entity) {
        LOG.info("Re-generating image content for deliverable id=" + entity.getId());

        if (entity.getContent() instanceof CustomContentEntity && isAutogeneratedTheme(entity.getDeliveryPipe().getTheme())) {
            CustomContentEntity contentEntity = (CustomContentEntity) entity.getContent();
            contentEntity.setContent("");  // clear all
            for (FileEntity file : entity.getFiles()) {
                // generate html blocks file by file
                this.autoGenerateContent(entity, file);
            }
        }
        LOG.info("Content re-generated for deliverable id=" + entity.getId());
        return entity;
    }


    @Transactional(readOnly = true)
    public Collection<ContentFile> getDeliverablesContentFiles(long id) {
        LOG.info("Getting deliverables files with deliverable id=" + id);
        Collection<FileEntity> files = repository.findDeliverableFiles(id);
        return fileMapper.toDomain(files);
    }

    @Transactional(readOnly = true)
    public Collection<FileEntity> getDeliverablesFileEntities(long id) {
        LOG.info("Getting deliverables files with deliverable id=" + id);
        return repository.findDeliverableFiles(id);
    }

    @Transactional
    public AbstractDeliverableEntity saveDeliverable(long deliveryPipeId, AbstractDeliverableEntity entity) {
        LOG.info("Saving series deliverable");
        if (entity.getId() == null || entity.getId() == 0) {
            entity.setStatus(AbstractDeliverableEntity.DeliverableStatusEnum.PENDING_APPROVAL);
            entity.setDeliveryPipe(deliveryPipeRepository.findOne(deliveryPipeId));
        }
        if (entity.getContent().getShortUuid() == null) {
            String shortUuid;
            while (contentRepository.findByShortUuid(shortUuid = ShortUrlUtils.getRandomShortIdentifier()) != null);
            entity.getContent().setShortUuid(shortUuid);
        }
        if (entity.getId() != 0 && entity.getId() != null) {
            entity.setFiles(new ArrayList(this.getDeliverablesFileEntities(entity.getId())));
        }
        // TODO: for the providers: allow save if and only if status == PENDING_APPROVAL
        return repository.save(entity);
    }

}