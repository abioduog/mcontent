package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.domain.mapper.ServiceMapper;
import com.mnewservice.mcontent.repository.ServiceRepository;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
// TODO: take account user rights
@org.springframework.stereotype.Service
public class ServiceManager {

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private ServiceMapper mapper;

    private static final Logger LOG = Logger.getLogger(ServiceManager.class);

    @Transactional(readOnly = true)
    public Service getService(String keyword, int shortCode, String operator) {
        LOG.info("Looking service with keyword=" + keyword
                + ", shortCode=" + shortCode
                + ", and operator=" + operator);
        ServiceEntity entity = repository
                .findByKeywordAndShortCodeAndOperator(keyword, shortCode, operator);
        return mapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public Service getUnsubscribeService(String unsubscribeKeyword,
            int shortCode, String operator) {
        LOG.info("Looking service with unsubscribeKeyword=" + unsubscribeKeyword
                + ", shortCode=" + shortCode
                + ", and operator=" + operator);
        ServiceEntity entity = repository
                .findByUnsubscribeKeywordAndShortCodeAndOperator(
                        unsubscribeKeyword, shortCode, operator
                );
        return mapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public Collection<Service> getAllServices() {
        LOG.info("Getting all services");
        Collection<ServiceEntity> entities = mapper.makeCollection(repository.findAll());
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Service getService(long id) {
        LOG.info("Getting service with id=" + id);
        ServiceEntity entity = repository.findOne(id);
        return mapper.toDomain(entity);
    }

    @Transactional
    public void removeService(long id) {
        LOG.info("Removing service with id=" + id);
        ServiceEntity entity = repository.findOne(id);
        repository.delete(entity);
    }

    @Transactional
    public Service saveService(Service service) {
        LOG.info("Saving service with id=" + service.getId());
        ServiceEntity entity = mapper.toEntity(service);
        return mapper.toDomain(repository.save(entity));
    }
}
