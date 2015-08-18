package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.DeliveryTime;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class DeliveryTimeMapper extends AbstractMapper<DeliveryTime, ServiceEntity.DeliveryTime> {

    @Override
    public DeliveryTime toDomain(ServiceEntity.DeliveryTime entity) {
        if (entity == null) {
            return null;
        }
        return DeliveryTime.valueOf(entity.name());
    }

    @Override
    public ServiceEntity.DeliveryTime toEntity(DeliveryTime domain) {
        if (domain == null) {
            return null;
        }
        return ServiceEntity.DeliveryTime.valueOf(domain.name());
    }

}
