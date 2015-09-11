package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.DeliverableType;
import com.mnewservice.mcontent.domain.DeliveryTime;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity.DeliverableTypeEnum;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class DeliverableTypeMapper extends AbstractMapper<DeliverableType, DeliverableTypeEnum> {

    @Override
    public DeliverableType toDomain(DeliverableTypeEnum entity) {
        if (entity == null) {
            return null;
        }
        return DeliverableType.valueOf(entity.name());
    }

    @Override
    public DeliverableTypeEnum toEntity(DeliverableType domain) {
        if (domain == null) {
            return null;
        }
        return DeliverableTypeEnum.valueOf(domain.name());
    }

}
