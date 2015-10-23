package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Content;
import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.domain.ScheduledDeliverable;
import com.mnewservice.mcontent.domain.SeriesDeliverable;
import com.mnewservice.mcontent.domain.mapper.ContentMapper;
import com.mnewservice.mcontent.domain.mapper.DeliveryPipeMapper;
import com.mnewservice.mcontent.domain.mapper.ScheduledDeliverableMapper;
import com.mnewservice.mcontent.domain.mapper.SeriesDeliverableMapper;
import com.mnewservice.mcontent.repository.ContentRepository;
import com.mnewservice.mcontent.repository.DeliveryPipeRepository;
import com.mnewservice.mcontent.repository.ScheduledDeliverableRepository;
import com.mnewservice.mcontent.repository.SeriesDeliverableRepository;
import com.mnewservice.mcontent.repository.entity.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.mnewservice.mcontent.util.ShortUrlUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@org.springframework.stereotype.Service
@javax.transaction.Transactional
public class DeliveryPipeManager {

    @Autowired
    private DeliveryPipeRepository repository;

    @Autowired
    private SeriesDeliverableRepository seriesRepository;

    @Autowired
    private ScheduledDeliverableRepository scheduledRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private DeliveryPipeMapper mapper;

    @Autowired
    private SeriesDeliverableMapper seriesMapper;

    @Autowired
    private ScheduledDeliverableMapper scheduledMapper;

    @Autowired
    private ContentMapper contentdMapper;

    private static final Logger LOG = Logger.getLogger(DeliveryPipeManager.class);

//<editor-fold defaultstate="collapsed" desc="delivery pipes">
    @Transactional(readOnly = true)
    public Collection<DeliveryPipe> getAllDeliveryPipes() {
        LOG.info("Getting all delivery pipes");
        EnumSet<RoleEntity.RoleEnum> roles = getCurrentUserRoles();

        Collection<DeliveryPipeEntity> entities = Arrays.asList();
        if(roles.contains(RoleEntity.RoleEnum.ADMIN)) {
            entities = mapper.makeCollection(repository.findAll());
        } else if(roles.contains(RoleEntity.RoleEnum.PROVIDER)){
            entities = mapper.makeCollection(repository.findByProvidersUsername( getCurrentUserUsername()));
        }
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public DeliveryPipe getDeliveryPipe(long id) {
        LOG.info("Getting delivery pipe with id=" + id);
        DeliveryPipeEntity entity = repository.findOne(id);
        return mapper.toDomain(entity);
    }

    @Transactional
    public DeliveryPipe saveDeliveryPipe(DeliveryPipe service) {
        LOG.info("Saving delivery pipe with id=" + service.getId());
        DeliveryPipeEntity entity = mapper.toEntity(service);
        return mapper.toDomain(repository.save(entity));
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="series content">
    @Transactional(readOnly = true)
    public Collection<SeriesDeliverable> getDeliveryPipeSeriesContent(long id) {
        LOG.info("Getting series content for delivery with id=" + id);
        DeliveryPipeEntity entity = repository.findOne(id);
        List<SeriesDeliverableEntity> contents = seriesRepository.findByDeliveryPipeOrderByDeliveryDaysAfterSubscriptionAsc(entity);
        return seriesMapper.toDomain(contents);
    }

    @Transactional(readOnly = true)
    public SeriesDeliverable getSeriesContent(long id) {
        SeriesDeliverableEntity content = seriesRepository.findOne(id);
        return seriesMapper.toDomain(content);
    }

    public SeriesDeliverable saveSeriesContent(long deliveryPipeId, SeriesDeliverable deliverable) {
        SeriesDeliverableEntity entity = seriesMapper.toEntity(deliverable);
        if (deliverable.getId() == null || deliverable.getId() == 0) {
            entity.setStatus(AbstractDeliverableEntity.DeliverableStatusEnum.PENDING_APPROVAL);
            entity.setDeliveryPipe(repository.findOne(deliveryPipeId));
            entity.setDeliveryDaysAfterSubscription((int) (seriesRepository.countByDeliveryPipeId(deliveryPipeId) + 1));
        }
        if(entity.getContent().getShortUuid() == null) {
            String shortUuid = ShortUrlUtils.getRandomShortIdentifier();
            while(contentRepository.findByShortUuid(shortUuid) != null) {
                shortUuid = ShortUrlUtils.getRandomShortIdentifier();
            }
            entity.getContent().setShortUuid(shortUuid);
        }

        // TODO: for the providers: allow save if and only if status == PENDING_APPROVAL
        return seriesMapper.toDomain(seriesRepository.save(entity));
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="scheduled content">
    @Transactional(readOnly = true)
    public Collection<ScheduledDeliverable> getDeliveryPipeScheduledContent(long id) {
        LOG.info("Getting scheduled content for delivery with id=" + id);
        DeliveryPipeEntity entity = repository.findOne(id);
        List<ScheduledDeliverableEntity> contents = scheduledRepository.findByDeliveryPipeOrderByDeliveryDateAsc(entity);
        return scheduledMapper.toDomain(contents);
    }

    @Transactional(readOnly = true)
    public ScheduledDeliverable getScheduledContent(long id) {
        ScheduledDeliverableEntity content = scheduledRepository.findOne(id);
        return scheduledMapper.toDomain(content);
    }

    public ScheduledDeliverable saveScheduledContent(long deliveryPipeId, ScheduledDeliverable deliverable) {
        ScheduledDeliverableEntity entity = scheduledMapper.toEntity(deliverable);
        if (deliverable.getId() == null || deliverable.getId() == 0) {
            entity.setStatus(AbstractDeliverableEntity.DeliverableStatusEnum.PENDING_APPROVAL);
            entity.setDeliveryPipe(repository.findOne(deliveryPipeId));
        }
        if(entity.getContent().getShortUuid() == null) {
            String shortUuid = ShortUrlUtils.getRandomShortIdentifier();
            while(contentRepository.findByShortUuid(shortUuid) != null) {
                shortUuid = ShortUrlUtils.getRandomShortIdentifier();
            }
            entity.getContent().setShortUuid(shortUuid);
        }

        // TODO: for the providers: allow save if and only if status == PENDING_APPROVAL
        return scheduledMapper.toDomain(scheduledRepository.save(entity));
    }
//</editor-fold>

    public Content getContentByUuid(String shortUuid) {
        EnumSet<RoleEntity.RoleEnum> roles = getCurrentUserRoles();
        AbstractContentEntity entity = null;
        if(roles.contains(RoleEntity.RoleEnum.ADMIN)) {
            entity = contentRepository.findByShortUuid(shortUuid);
        } else if(roles.contains(RoleEntity.RoleEnum.PROVIDER)) {
            entity = contentRepository.findByShortUuidAndProviderUsername(shortUuid, getCurrentUserUsername());
        } else {
            entity = contentRepository.findByShortUuidWithValidSubscription(shortUuid, getCurrentUserUsername());
        }
        return contentdMapper.toDomain(entity);
    }

    protected EnumSet<RoleEntity.RoleEnum> getCurrentUserRoles() {
        EnumSet<RoleEntity.RoleEnum> roles = EnumSet.noneOf(RoleEntity.RoleEnum.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach( authority -> {
            roles.add(RoleEntity.RoleEnum.valueOf(authority.getAuthority()));
        });

        return roles;
    }

    protected String getCurrentUserUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((org.springframework.security.core.userdetails.User)authentication.getPrincipal()).getUsername();
    }

    public String getThemeForContentByUuid(String shortUuid) {
        return repository.getThemeForContentByUuid(shortUuid);
    }
}
