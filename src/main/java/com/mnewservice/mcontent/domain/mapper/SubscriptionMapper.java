package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class SubscriptionMapper extends AbstractMapper<Subscription, SubscriptionEntity> {

    private static final Logger LOG = Logger.getLogger(SubscriptionMapper.class);

    @Override
    public Subscription toDomain(SubscriptionEntity entity) {
        throw new UnsupportedOperationException("not implemented yet.");
    }

    @Override
    public SubscriptionEntity toEntity(Subscription domain) {
        SubscriptionEntity entity = new SubscriptionEntity();

        LOG.debug("mapping id: " + domain.getId());
        entity.setId(domain.getId());

        LOG.debug("mapping service: " + domain.getService());
        //entity.set

        LOG.debug("mapping subscriber: " + domain.getSubscriber());
        //entity.set

        LOG.debug("mapping periods: " + domain.getPeriods());
        //entity.set

        return entity;
    }

}
