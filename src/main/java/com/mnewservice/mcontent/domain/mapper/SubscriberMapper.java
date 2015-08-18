package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class SubscriberMapper extends AbstractMapper<Subscriber, SubscriberEntity> {

    private static final Logger LOG = Logger.getLogger(SubscriberMapper.class);

    @Autowired
    private PhoneNumberMapper phoneNumberMapper;

    @Override
    public Subscriber toDomain(SubscriberEntity entity) {
        if (entity == null) {
            return null;
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SubscriberEntity toEntity(Subscriber domain) {
        if (domain == null) {
            return null;
        }

        SubscriberEntity entity = new SubscriberEntity();
        LOG.debug("mapping id: " + domain.getId());
        entity.setId(domain.getId());
        LOG.debug("mapping phone: " + domain.getPhone());
        entity.setPhone(phoneNumberMapper.toEntity(domain.getPhone()));

        return entity;
    }

}
