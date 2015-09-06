package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.domain.mapper.SubscriberMapper;
import com.mnewservice.mcontent.repository.SubscriberRepository;
import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class SubscriberManager {

    @Autowired
    private SubscriberRepository repository;

    @Autowired
    private SubscriberMapper mapper;

    private static final Logger LOG = Logger.getLogger(SubscriberManager.class);

    @Transactional(readOnly = true)
    public Subscriber getSubscriber(String number) {
        LOG.info("Looking subscriber with number=" + number);
        SubscriberEntity entity = repository.findByPhoneNumber(number);
        return mapper.toDomain(entity);
    }

    public Collection<Subscriber> getAllSubscribers() {
        LOG.info("Getting all subscribers");
        Collection<SubscriberEntity> entities = mapper.makeCollection(repository.findAll());
        return mapper.toDomain(entities);
    }

}
