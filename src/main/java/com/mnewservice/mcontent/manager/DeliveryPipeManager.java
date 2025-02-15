package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Content;
import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.domain.mapper.ContentMapper;
import com.mnewservice.mcontent.domain.mapper.DeliveryPipeMapper;
import com.mnewservice.mcontent.repository.ContentRepository;
import com.mnewservice.mcontent.repository.DeliveryPipeRepository;
import com.mnewservice.mcontent.repository.entity.*;
import com.mnewservice.mcontent.util.SessionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Collections;
import java.util.HashSet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@org.springframework.stereotype.Service
@javax.transaction.Transactional
public class DeliveryPipeManager {

    @Autowired
    private AbstractDeliverableManager deliverableManager;

    @Autowired
    private DeliveryPipeRepository repository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private DeliveryPipeMapper mapper;

    @Autowired
    private ContentMapper contentMapper;

    private static final Logger LOG = Logger.getLogger(DeliveryPipeManager.class);

    @Transactional(readOnly = true)
    public Collection<DeliveryPipe> getDeliveryPipes(String nameFilter) {
        String filter = (nameFilter == null || nameFilter.length() == 0) ? "%" : "%" + nameFilter + "%";
        LOG.info("Getting delivery pipes filtered by name [" + filter + "]");
        EnumSet<RoleEntity.RoleEnum> roles = SessionUtils.getCurrentUserRoles();

        Collection<DeliveryPipeEntity> entities = Arrays.asList();
        if (roles.contains(RoleEntity.RoleEnum.ADMIN)) {
            entities = mapper.makeCollection(repository.findAllByOrderByNameAsc(filter));
        } else if (roles.contains(RoleEntity.RoleEnum.PROVIDER)) {
            entities = mapper.makeCollection(repository.findByProvidersUsernameOrderByNameAsc(filter, SessionUtils.getCurrentUserUsername()));
        }
        LOG.info("Found " + entities.size() + " entity.");
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Collection<DeliveryPipe> getAllDeliveryPipes() {
        LOG.info("Getting all delivery pipes");
        return getDeliveryPipes("");
    }

    @Transactional(readOnly = true)
    public DeliveryPipe getDeliveryPipe(long id) {
        LOG.info("Getting delivery pipe with id=" + id);
        DeliveryPipeEntity entity = repository.findOne(id);
        return mapper.toDomain(entity);
    }

    @Transactional
    public DeliveryPipe saveDeliveryPipe(DeliveryPipe deliveryPipe) {
        LOG.info("Saving delivery pipe with id=" + deliveryPipe.getId());
        DeliveryPipeEntity entity = mapper.toEntity(deliveryPipe);
        if (deliveryPipe.getId() == null) { // create
            entity.setDeliverables(Collections.<AbstractDeliverableEntity>emptySet());
        } else { // update
            DeliveryPipeEntity oldEntityWithDeliverables = repository.findOne(deliveryPipe.getId());
            entity.setDeliverables(oldEntityWithDeliverables.getDeliverables());
        }
        return mapper.toDomain(repository.save(entity));
    }

    @Transactional
    public void removeDeliveryPipe(Long id) {
        LOG.info("Removing delivery pipe with id=" + id);
        DeliveryPipeEntity entity = repository.findOneAndLockIt(id);
        Collection<AbstractDeliverableEntity> deliverables = new HashSet<>();
        entity.getDeliverables().stream().forEach(d -> deliverables.add(d)); // Otetaan alkuperäinen lista talteen
        for (AbstractDeliverableEntity deliverable : deliverables) {
            deliverableManager.removeDeliverable(deliverable); // Tämä muokkaa listaa
        };
        entity = repository.findOne(id);
        repository.delete(entity);
    }

    @Transactional(readOnly = true)
    public boolean hasContent(Long id) {
        LOG.info("Checking for delivery pipe content with id=" + id);
        DeliveryPipeEntity entity = repository.findOne(id);
        return entity.getDeliverables() != null && entity.getDeliverables().size() > 0;
    }

    @Transactional(readOnly = true)
    public Collection<DeliveryPipe> getDeliveryPipesByProvider(Long id) {
        LOG.info("Getting DeliveryPipes by provider with id=" + id);
        //return mapper.toDomain(repository.findByProviders(id));
        return mapper.toDomain(repository.findByProvidersOrderByNameAsc(id));
    }

    @Transactional
    public void removeProviderFromDeliveryPipes(Long id, UserEntity user) {
        LOG.info("Removing provider(" + id + ") from delivery pipes.");
        //Collection<DeliveryPipeEntity> pipes = repository.findByProviders(id);
        Collection<DeliveryPipeEntity> pipes = repository.findByProvidersOrderByNameAsc(id);
        pipes.stream().map((pipe) -> {
            pipe.getProviders().remove(user);
            return pipe;
        }).forEach((pipe) -> {
            repository.save(pipe);
        });
    }

    public Content getContentByUuid(String shortUuid) {
        EnumSet<RoleEntity.RoleEnum> roles = SessionUtils.getCurrentUserRoles();
        AbstractContentEntity entity = null;
        if(roles.contains(RoleEntity.RoleEnum.ADMIN)) {
            entity = contentRepository.findByShortUuid(shortUuid);
        } else if(roles.contains(RoleEntity.RoleEnum.PROVIDER)) {
            entity = contentRepository.findByShortUuidAndProviderUsername(shortUuid, SessionUtils.getCurrentUserUsername());
        } else {
            entity = contentRepository.findByShortUuidWithValidSubscription(shortUuid, SessionUtils.getCurrentUserUsername());
        }
        return contentMapper.toDomain(entity);
    }

    public String getThemeForContentByUuid(String shortUuid) {
        return repository.getThemeForContentByUuid(shortUuid);
    }
}
