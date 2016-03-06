package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.repository.entity.SmsMessageEntity;
import org.springframework.stereotype.Component;

/**
 * Created by Antti Vikman on 6.3.2016.
 */
@Component
public class SmsMessageMapper extends AbstractMapper<SmsMessage, SmsMessageEntity> {

    @Override
    public SmsMessage toDomain(SmsMessageEntity entity) {
        SmsMessage domain = new SmsMessage();
        domain.setFromNumber(entity.getFromNumber());
        domain.setMessage(entity.getMessage());
        domain.setReceivers(entity.getReceivers());
        domain.setCreated(entity.getCreated());
        domain.setSent(entity.getSent());
        domain.setTries(entity.getTries());
        domain.setLog(entity.getLog());
        return domain;
    }

    @Override
    public SmsMessageEntity toEntity(SmsMessage domain) {
        SmsMessageEntity entity = new SmsMessageEntity();
        entity.setFromNumber(domain.getFromNumber());
        entity.setMessage(domain.getMessage());
        entity.setReceivers(domain.getReceivers());
        entity.setCreated(domain.getCreated());
        entity.setSent(domain.getSent());
        entity.setTries(domain.getTries());
        entity.setLog(domain.getLog());
        return entity;
    }
}
