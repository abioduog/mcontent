package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Email;
import com.mnewservice.mcontent.repository.entity.EmailEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class EmailMapper extends AbstractMapper<Email, EmailEntity> {

    @Override
    public Email toDomain(EmailEntity entity) {
        if (entity == null) {
            return null;
        }
        Email domain = new Email();
        domain.setId(entity.getId());
        domain.setAddress(entity.getAddress());
        return domain;
    }

    @Override
    public EmailEntity toEntity(Email domain) {
        if (domain == null) {
            return null;
        }
        EmailEntity entity = new EmailEntity();
        entity.setId(domain.getId());
        entity.setAddress(domain.getAddress());
        return entity;
    }

}
