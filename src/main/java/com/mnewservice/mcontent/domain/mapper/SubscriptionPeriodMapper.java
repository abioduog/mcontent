package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.SubscriptionPeriod;
import com.mnewservice.mcontent.repository.entity.SubscriptionPeriodEntity;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class SubscriptionPeriodMapper extends AbstractMapper<SubscriptionPeriod, SubscriptionPeriodEntity> {

    private static final Logger LOG = Logger.getLogger(SubscriptionPeriodMapper.class);

    @Override
    public SubscriptionPeriod toDomain(SubscriptionPeriodEntity entity) {
        if (entity == null) {
            return null;
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SubscriptionPeriodEntity toEntity(SubscriptionPeriod domain) {
        if (domain == null) {
            return null;
        }

        SubscriptionPeriodEntity entity = new SubscriptionPeriodEntity();

        LOG.debug("mapping id: " + domain.getId());
        entity.setId(domain.getId());

        LOG.debug("mapping start: " + domain.getStart());
        entity.setStart(domain.getStart());

        LOG.debug("mapping end: " + domain.getEnd());
        entity.setEnd(domain.getEnd());

        LOG.debug("mapping message: " + domain.getMessage());
        entity.setMessage(domain.getMessage());

        LOG.debug("mapping shortCode: " + domain.getShortCode());
        entity.setShortCode(domain.getShortCode());

        LOG.debug("mapping operator: " + domain.getOperator());
        entity.setOperator(domain.getOperator());

        LOG.debug("mapping originalTimestamp: " + domain.getOriginalTimeStamp());
        entity.setOriginalTimestamp(domain.getOriginalTimeStamp());

        LOG.debug("mapping messageId: " + domain.getMessageId());
        entity.setMessageId(domain.getMessageId());

        LOG.debug("mapping sender: " + domain.getSender());
        entity.setSender(domain.getSender());

        return entity;
    }

}
