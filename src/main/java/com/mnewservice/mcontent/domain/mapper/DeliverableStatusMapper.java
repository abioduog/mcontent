package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.DeliverableStatus;
import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class DeliverableStatusMapper
        extends AbstractMapper<DeliverableStatus, AbstractDeliverableEntity.DeliverableStatusEnum> {

    @Override
    public DeliverableStatus toDomain(AbstractDeliverableEntity.DeliverableStatusEnum entity) {
        if (entity == null) {
            return null;
        }
        return DeliverableStatus.valueOf(entity.name());
    }

    @Override
    public AbstractDeliverableEntity.DeliverableStatusEnum toEntity(DeliverableStatus domain) {
        if (domain == null) {
            return null;
        }
        return AbstractDeliverableEntity.DeliverableStatusEnum.valueOf(domain.name());
    }

}
