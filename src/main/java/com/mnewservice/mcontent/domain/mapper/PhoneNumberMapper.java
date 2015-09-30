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

    @Override
    public PhoneNumber toDomain(PhoneNumberEntity entity) {
        if (entity == null) {
            return null;
        }

        PhoneNumber domain = new PhoneNumber();
        domain.setId(entity.getId());
        domain.setNumber(entity.getNumber());

        return domain;
    }

    @Override
    public PhoneNumberEntity toEntity(PhoneNumber domain) {
        if (domain == null) {
            return null;
        }

        PhoneNumberEntity entity = new PhoneNumberEntity();
        entity.setId(domain.getId());
        entity.setNumber(domain.getNumber());

        return entity;
    }

}
