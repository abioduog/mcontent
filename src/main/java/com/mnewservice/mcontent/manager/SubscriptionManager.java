package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.domain.mapper.SubscriptionMapper;
import com.mnewservice.mcontent.repository.SubscriptionRepository;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class SubscriptionManager {

    @Autowired
    private SubscriptionRepository repository;

    @Autowired
    private SubscriptionMapper mapper;

    private static final Logger LOG = Logger.getLogger(SubscriptionManager.class);

    public boolean registerSubscription(Subscription subscription) {
        // TODO: tuki tilauksen päivittämiselle
        LOG.debug("registerSubscription() with subscription=" + subscription);
        SubscriptionEntity entity = repository.save(mapper.toEntity(subscription));
        if (entity != null && entity.getId() != null) {
            LOG.info("Saved entity with id " + entity.getId());
            return true;
        } else {
            LOG.error("Failed to save entity!");
            return false;
        }
    }

    public boolean unRegisterSubscription(Subscription subscription) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
