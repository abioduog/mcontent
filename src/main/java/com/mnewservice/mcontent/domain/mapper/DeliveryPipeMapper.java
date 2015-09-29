package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import com.mnewservice.mcontent.repository.entity.UserEntity;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class DeliveryPipeMapper extends AbstractMapper<DeliveryPipe, DeliveryPipeEntity> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeliverableTypeMapper deliverableTypeMapper;

    @Override
    public DeliveryPipe toDomain(DeliveryPipeEntity entity) {
        if (entity == null) {
            return null;
        }
        DeliveryPipe domain = new DeliveryPipe();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setDeliverableType(
                deliverableTypeMapper.toDomain(entity.getDeliverableType()));
        domain.setProviders(userMapper.toDomain(entity.getProviders()));
        return domain;

    }

    @Override
    public DeliveryPipeEntity toEntity(DeliveryPipe domain) {
        if (domain == null) {
            return null;
        }
        DeliveryPipeEntity entity = new DeliveryPipeEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDeliverableType(
                deliverableTypeMapper.toEntity(domain.getDeliverableType()));
        Collection<UserEntity> providers = userMapper.toEntity(domain.getProviders());
        entity.setProviders((providers == null)
                ? null
                : providers.stream().collect(Collectors.toSet())
        );

        return entity;
    }

}