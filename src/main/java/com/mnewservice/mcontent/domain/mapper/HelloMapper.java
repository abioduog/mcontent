package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Hello;
import com.mnewservice.mcontent.repository.entity.HelloEntity;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class HelloMapper extends AbstractMapper<Hello, HelloEntity> {

    private static final Logger LOG = Logger.getLogger(HelloMapper.class);

    @Override
    public Hello toDomain(HelloEntity entity) {
        LOG.debug("mapping: " + entity.getId() + "; " + entity.getName());
        Hello domain = new Hello();
        domain.setId(entity.getId());
        domain.setName(entity.getName());

        return domain;
    }

    @Override
    public HelloEntity toEntity(Hello domain) {
        LOG.debug("mapping: " + domain.getId() + "; " + domain.getName());
        HelloEntity entity = new HelloEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());

        return entity;
    }

}
