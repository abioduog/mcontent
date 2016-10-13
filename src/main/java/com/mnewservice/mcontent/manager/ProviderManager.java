package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.domain.mapper.ProviderMapper;
import com.mnewservice.mcontent.repository.ProviderRepository;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.ProviderEntity;
import com.mnewservice.mcontent.repository.entity.RoleEntity;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class ProviderManager {

    @Autowired
    private ProviderRepository repository;

    @Autowired
    private DeliveryPipeManager deliveryPipeManager;

    @Autowired
    private ProviderMapper mapper;

    private static final Logger LOG = Logger.getLogger(ProviderManager.class);

    @Transactional(readOnly = true)
    public Collection<Provider> getAllProviders() {
        LOG.info("Getting all providers");
       // Collection<ProviderEntity> entities
       //         = mapper.makeCollection(repository.findAll());
                Collection<ProviderEntity> entities
                = repository.findAllByOrderByNameAsc();
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Provider getProvider(long id) {
        LOG.info("Getting provider with id=" + id);
        ProviderEntity entity = repository.findOne(id);
        return mapper.toDomain(entity);
    }

    @Transactional
    public boolean removeCorrespondence(long correspondenceId) {
        LOG.info("Removing correspondence with id=" + correspondenceId);
        ProviderEntity entity = repository.findByCorrespondencesId(correspondenceId);
        if (entity == null) {
            return false;
        }
        entity.getCorrespondences().removeIf(c -> c.getId() == correspondenceId);
        return (repository.save(entity) != null);
    }

    @Transactional
    public Provider saveProvider(Provider provider) {
        LOG.info("Saving provider with id=" + provider.getId());
        ProviderEntity entity = mapper.toEntity(provider);
        return mapper.toDomain(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Provider findByUserId(long userId) {
        LOG.info("Finding provider with userId=" + userId);
        return mapper.toDomain(repository.findByUserId(userId));
    }
    
    @Transactional(readOnly = true)
    public Provider findByEmail(String email) {
        LOG.info("Finding provider with email=" + email);
        return mapper.toDomain(repository.findByEmail(email));
    }

    @Transactional
    public void removeProvider(Long id) {
        LOG.info("Removing provider with id=" + id);
        ProviderEntity entity = repository.findOne(id);
        deliveryPipeManager.removeProviderFromDeliveryPipes(entity.getId(), entity.getUser());
        repository.delete(entity);
    }

    @Transactional(readOnly = true)
    public long getPipeCount(Long id) {
        LOG.info("Checking for provider delivery pipe count with id=" + id);
        Collection<DeliveryPipe> retval = deliveryPipeManager.getDeliveryPipesByProvider(id);
        return retval == null ? -1 : retval.size();
    }


    
    
    @Transactional(readOnly = true)
    public Collection<Provider> getFilteredProviders(String nameFilter) {
        String filter = (nameFilter == null || nameFilter.length() == 0) ? "%" : "%" + nameFilter + "%";
        LOG.info("Getting providers filtered by name [" + filter + "]");
        EnumSet<RoleEntity.RoleEnum> roles = getCurrentUserRoles();

        Collection<ProviderEntity> entities = Arrays.asList();
        if (roles.contains(RoleEntity.RoleEnum.ADMIN)) {
            //entities = mapper.makeCollection(repository.findAll(filter));
            entities = mapper.makeCollection(repository.findByNameContainingOrderByNameAsc(filter));
        } /*else if (roles.contains(RoleEntity.RoleEnum.PROVIDER)) {
            //entities = mapper.makeCollection(repository.findByProvidersUsername(filter, getCurrentUserUsername()));
            entities = mapper.makeCollection(repository.findByProvidersUsernameOrderByNameAsc(filter, getCurrentUserUsername()));
        }*/
        LOG.info("Found " + entities.size() + " entity.");
        return mapper.toDomain(entities);
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

}
