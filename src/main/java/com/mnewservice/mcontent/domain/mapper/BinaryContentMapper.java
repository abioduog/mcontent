package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.BinaryContent;
import com.mnewservice.mcontent.repository.entity.BinaryContentEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class BinaryContentMapper
        extends AbstractMapper<BinaryContent, BinaryContentEntity> {

    @Override
    public BinaryContent toDomain(BinaryContentEntity entity) {
        if (entity == null) {
            return null;
        }
        BinaryContent domain = new BinaryContent();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setContentType(entity.getContentType());
        domain.setContent(entity.getContent());
        return domain;
    }

    @Override
    public BinaryContentEntity toEntity(BinaryContent domain) {
        if (domain == null) {
            return null;
        }
        BinaryContentEntity entity = new BinaryContentEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setContentType(domain.getContentType());
        entity.setContent(domain.getContent());
        return entity;
    }

}
