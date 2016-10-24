package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.domain.mapper.ServiceMapper;
import com.mnewservice.mcontent.repository.ServiceRepository;
import com.mnewservice.mcontent.repository.entity.RoleEntity;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import com.mnewservice.mcontent.util.SessionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
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
    private DeliveryPipeManager deliveryPipeManager;

    @Autowired
    private ProviderManager providerManager;

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
        LOG.info("Looking unsubscribe service with keyword=" + unsubscribeKeyword
                + ", shortCode=" + shortCode
                + ", and operator=" + operator);
        ServiceEntity entity = repository
                .findByUnsubscribeKeywordAndShortCodeAndOperator(
                        unsubscribeKeyword, shortCode, operator
                );
        return mapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public Collection<Service> getAllServicesByDeliveryPipe(Long pipeId) {
        LOG.info("Getting all services by delivery pipe id = " + pipeId);
        Collection<ServiceEntity> entities = mapper.makeCollection(repository.findAllByPipeId(pipeId));
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Collection<Service> getAllServices() {
        LOG.info("Getting all services");
        Collection<ServiceEntity> entities = new ArrayList<>();
        EnumSet<RoleEntity.RoleEnum> roles = SessionUtils.getCurrentUserRoles();
        if (roles.contains(RoleEntity.RoleEnum.ADMIN)) {
            entities = repository.findAllByOrderByOperatorAscShortCodeAscKeywordAsc();
        } else if (roles.contains(RoleEntity.RoleEnum.PROVIDER)) {
            Provider provider = providerManager.findByName(SessionUtils.getCurrentUserUsername());
            if (provider != null) {
                Collection<DeliveryPipe> deliveryPipes = deliveryPipeManager.getDeliveryPipesByProvider(provider.getId());
                List<Long> pipeIds = deliveryPipes.stream().map(p -> p.getId()).collect(Collectors.toList());
                entities.addAll(mapper.makeCollection(repository.findByDeliveryPipeIdInOrderByOperatorAscShortCodeAscKeywordAsc(pipeIds)));
            }
        }

        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Long countAllServices() {
        LOG.info("Getting all services");
        EnumSet<RoleEntity.RoleEnum> roles = SessionUtils.getCurrentUserRoles();
        if (roles.contains(RoleEntity.RoleEnum.ADMIN)) {
            return new Long(repository.count());
        } else if (roles.contains(RoleEntity.RoleEnum.PROVIDER)) {
            Provider provider = providerManager.findByName(SessionUtils.getCurrentUserUsername());
            if (provider != null) {
                Collection<DeliveryPipe> deliveryPipes = deliveryPipeManager.getDeliveryPipesByProvider(provider.getId());
                List<Long> pipeIds = deliveryPipes.stream().map(p -> p.getId()).collect(Collectors.toList());
                return repository.countByDeliveryPipeIdInOrderByOperatorAscShortCodeAscKeywordAsc(pipeIds);
            }
        }

        return 0L;
    }

    @Transactional(readOnly = true)
    public Collection<Service> getFilteredServices(String nameFilter) {
        LOG.info("Getting filtered services, filterName = " + nameFilter);
        Collection<ServiceEntity> entities = new ArrayList<>();
        EnumSet<RoleEntity.RoleEnum> roles = SessionUtils.getCurrentUserRoles();
        if (roles.contains(RoleEntity.RoleEnum.ADMIN)) {
            entities = repository.findByKeywordContainingOrderByOperatorAscShortCodeAscKeywordAsc(nameFilter);
        } else if (roles.contains(RoleEntity.RoleEnum.PROVIDER)) {
            Provider provider = providerManager.findByName(SessionUtils.getCurrentUserUsername());
            if (provider != null) {
                Collection<DeliveryPipe> deliveryPipes = deliveryPipeManager.getDeliveryPipesByProvider(provider.getId());
                List<Long> pipeIds = deliveryPipes.stream().map(p -> p.getId()).collect(Collectors.toList());
                entities.addAll(mapper.makeCollection(repository.findByDeliveryPipeIdInAndKeywordContainingOrderByOperatorAscShortCodeAscKeywordAsc(pipeIds, nameFilter)));
            }
        }
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
        entity.setDeliveryPipe(null);
        repository.save(entity);
        repository.delete(entity);
    }

    @Transactional
    public Service saveService(Service service) {
        LOG.info("Saving service with id=" + service.getId());
        ServiceEntity entity = mapper.toEntity(service);
        return mapper.toDomain(repository.save(entity));
    }

}
