package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.SubscriptionLog;
import com.mnewservice.mcontent.domain.mapper.SubscriptionLogMapper;
import com.mnewservice.mcontent.repository.SubscriptionLogRepository;
import com.mnewservice.mcontent.repository.entity.SubscriptionLogEntity;
import java.util.Collection;
import java.util.Date;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionLogManager {

    @Autowired
    private SubscriptionLogRepository repository;

    @Autowired
    private SubscriptionLogMapper mapper;

    private static final Logger LOG = Logger.getLogger(SubscriptionLogManager.class);

    @Transactional(readOnly = true)
    public Collection<SubscriptionLog> getAllSubscriptionsByService(Long serviceId) {
        LOG.info("Getting all subscription log entries by delivery pipe id = " + serviceId);
        Collection<SubscriptionLogEntity> entities = mapper.makeCollection(repository.findAllByServiceId(serviceId));
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Collection<SubscriptionLog> getAllSubscriptionsByService(Long serviceId, Date firstDate, Date lastDate) {
        LOG.info("Getting all subscription log entries by delivery pipe id = " + serviceId);
        //Collection<SubscriptionLogEntity> entities = mapper.makeCollection(repository.findAllByServiceIdBetweenDates(serviceId, firstDate, lastDate));
        Collection<SubscriptionLogEntity> entities = mapper.makeCollection(repository.findAllByServiceId(serviceId));
        return mapper.toDomain(entities);
    }

    @Transactional
    public SubscriptionLog saveSubscriptionLog(SubscriptionLog logEntry) {
        LOG.info("Saving subscription log entry with id=" + logEntry.getId());
        SubscriptionLogEntity entity = mapper.toEntity(logEntry);
        return mapper.toDomain(repository.save(entity));
    }

}
