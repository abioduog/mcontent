package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.PhoneNumber;
import com.mnewservice.mcontent.repository.entity.PhoneNumberEntity;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class PhoneNumberMapper extends AbstractMapper<PhoneNumber, PhoneNumberEntity> {

    private static final Logger LOG = Logger.getLogger(PhoneNumberMapper.class);

    @Override
    public PhoneNumber toDomain(PhoneNumberEntity entity) {
        if (entity == null) {
            return null;
        }

        PhoneNumber domain = new PhoneNumber();
        LOG.debug("mapping id: " + entity.getId());
        domain.setId(entity.getId());
        LOG.debug("mapping number: " + entity.getNumber());
        domain.setNumber(entity.getNumber());

        return domain;
    }

    @Override
    public PhoneNumberEntity toEntity(PhoneNumber domain) {
        if (domain == null) {
            return null;
        }

        PhoneNumberEntity entity = new PhoneNumberEntity();
        LOG.debug("mapping id: " + domain.getId());
        entity.setId(domain.getId());
        LOG.debug("mapping number: " + domain.getNumber());
        entity.setNumber(domain.getNumber());

        return entity;
    }

}
