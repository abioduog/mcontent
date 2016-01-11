package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.domain.mapper.ProviderMapper;
import com.mnewservice.mcontent.repository.ProviderRepository;
import com.mnewservice.mcontent.repository.entity.ProviderEntity;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ProviderMapper mapper;

    private static final Logger LOG = Logger.getLogger(ProviderManager.class);

    @Transactional(readOnly = true)
    public Collection<Provider> getAllProviders() {
        LOG.info("Getting all providers");
        Collection<ProviderEntity> entities
                = mapper.makeCollection(repository.findAll());
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

    @Transactional
    public void removeProvider(Long id) {
        LOG.info("Removing provider with id=" + id);
        ProviderEntity entity = repository.findOne(id);
        repository.delete(entity);
    }

}
