package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Content;
import com.mnewservice.mcontent.repository.entity.AbstractContentEntity;
import com.mnewservice.mcontent.repository.entity.CustomContentEntity;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper extends AbstractMapper<Content, AbstractContentEntity> {

    public Content toDomain(AbstractContentEntity entity) {
        Content domain = new Content();
        domain.setTitle(entity.getSummary());
        if (entity instanceof CustomContentEntity) {
            CustomContentEntity custom = (CustomContentEntity) entity;
            domain.setContent(custom.getContent());
        }
        return domain;
    }

    @Override
    public AbstractContentEntity toEntity(Content domain) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
