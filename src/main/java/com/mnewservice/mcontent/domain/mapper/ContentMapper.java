package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Content;
import com.mnewservice.mcontent.repository.entity.AbstractContentEntity;
import com.mnewservice.mcontent.repository.entity.CustomContentEntity;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper extends AbstractMapper<Content, AbstractContentEntity> {

    @Override
    public Content toDomain(AbstractContentEntity entity) {
        if (entity == null) {
            return null;
        }
        if(entity instanceof CustomContentEntity) {
            return toDomain((CustomContentEntity)entity);
        } else {
            return null;
        }
    }

    public Content toDomain(CustomContentEntity entity) {
        Content domain = new Content();
        domain.setId(entity.getId());
        domain.setUuid(entity.getShortUuid());
        domain.setTitle(entity.getTitle());
        domain.setMessage(entity.getMessage());
        domain.setContent(entity.getContent());
        return domain;
    }

    @Override
    public CustomContentEntity toEntity(Content domain) {
        CustomContentEntity entity = new CustomContentEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setMessage(domain.getMessage());
        entity.setContent(domain.getContent());
        return entity;
    }
}
