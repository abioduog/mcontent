package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class ServiceMapper extends AbstractMapper<Service, ServiceEntity> {

    @Autowired
    private DeliveryTimeMapper deliveryTimeMapper;

    @Autowired
    private DeliveryPipeMapper deliveryPipeMapper;

    @Override
    public Service toDomain(ServiceEntity entity) {
        if (entity == null) {
            return null;
        }
        Service domain = new Service();
        domain.setId(entity.getId());
        domain.setKeyword(entity.getKeyword());
        domain.setShortCode(entity.getShortCode());
        domain.setOperator(entity.getOperator());
        domain.setUnsubscribeKeyword(entity.getUnsubscribeKeyword());
        domain.setSubscriptionPeriod(entity.getSubscriptionPeriod());
        domain.setWelcomeMessage(entity.getWelcomeMessage());
        domain.setRenewMessage(entity.getRenewMessage());
        domain.setExpireMessage(entity.getExpireMessage());
        domain.setUnsubscribeMessage(entity.getUnsubscribeMessage());
        domain.setServiceDescription(entity.getServiceDescription());
        domain.setDeliveryPipe(deliveryPipeMapper.toDomain(entity.getDeliveryPipe()));
        domain.setDeliveryTime(deliveryTimeMapper.toDomain(entity.getDeliveryTime()));

        return domain;
    }

    @Override
    public ServiceEntity toEntity(Service domain) {
        if (domain == null) {
            return null;
        }
        ServiceEntity entity = new ServiceEntity();

        entity.setId(domain.getId());
        entity.setKeyword(domain.getKeyword());
        entity.setShortCode(domain.getShortCode());
        entity.setOperator(domain.getOperator());
        entity.setUnsubscribeKeyword(domain.getUnsubscribeKeyword());
        entity.setSubscriptionPeriod(domain.getSubscriptionPeriod());
        entity.setWelcomeMessage(domain.getWelcomeMessage());
        entity.setRenewMessage(domain.getRenewMessage());
        entity.setExpireMessage(domain.getExpireMessage());
        entity.setUnsubscribeMessage(domain.getUnsubscribeMessage());
        entity.setServiceDescription(domain.getServiceDescription());
        entity.setDeliveryPipe(deliveryPipeMapper.toEntity(domain.getDeliveryPipe()));
        entity.setDeliveryTime(deliveryTimeMapper.toEntity(domain.getDeliveryTime()));

        return entity;
    }

}
