package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class ServiceMapper extends AbstractMapper<Service, ServiceEntity> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeliveryTimeMapper deliveryTimeMapper;

    private static final Logger LOG = Logger.getLogger(ServiceMapper.class);

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
        domain.setProvider(userMapper.toDomain(entity.getProvider()));
        domain.setUnsubscribeKeyword(entity.getUnsubscribeKeyword());
        domain.setSubscriptionPeriod(entity.getSubscriptionPeriod());
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
        entity.setProvider(userMapper.toEntity(domain.getProvider()));
        entity.setUnsubscribeKeyword(domain.getUnsubscribeKeyword());
        entity.setSubscriptionPeriod(domain.getSubscriptionPeriod());
        entity.setDeliveryTime(deliveryTimeMapper.toEntity(domain.getDeliveryTime()));

        return entity;
    }

}
