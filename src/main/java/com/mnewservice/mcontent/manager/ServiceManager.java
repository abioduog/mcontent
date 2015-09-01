package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.domain.mapper.ServiceMapper;
import com.mnewservice.mcontent.repository.ServiceRepository;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@org.springframework.stereotype.Service
public class ServiceManager {

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private ServiceMapper mapper;

    private static final Logger LOG = Logger.getLogger(ServiceManager.class);

    @Transactional(readOnly = true)
    public Service getService(String keyword, int shortCode, String operator) {
        LOG.info("Looking service with keyword=" + keyword
                + ", shortCode=" + shortCode
                + ", and operator=" + operator);
        ServiceEntity entity = repository
                .findByKeywordAndShortCodeAndOperator(keyword, shortCode, operator);
        return mapper.toDomain(entity);
    }
}
