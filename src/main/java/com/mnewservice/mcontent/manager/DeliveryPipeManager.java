package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.domain.mapper.DeliveryPipeMapper;
import com.mnewservice.mcontent.repository.DeliveryPipeRepository;
import com.mnewservice.mcontent.repository.entity.DeliveryPipeEntity;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@org.springframework.stereotype.Service
@javax.transaction.Transactional
public class DeliveryPipeManager {

    @Autowired
    private DeliveryPipeRepository repository;

    @Autowired
    private DeliveryPipeMapper mapper;

    private static final Logger LOG = Logger.getLogger(DeliveryPipeManager.class);

    @Transactional(readOnly = true)
    public Collection<DeliveryPipe> getAllDeliveryPipes() {
        LOG.info("Getting all delivery pipes");
        Collection<DeliveryPipeEntity> entities = mapper.makeCollection(repository.findAll());
        return mapper.toDomain(entities);
    }
}
