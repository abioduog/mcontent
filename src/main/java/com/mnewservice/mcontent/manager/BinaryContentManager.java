package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.BinaryContent;
import com.mnewservice.mcontent.domain.mapper.BinaryContentMapper;
import com.mnewservice.mcontent.repository.BinaryContentRepository;
import com.mnewservice.mcontent.repository.entity.BinaryContentEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class BinaryContentManager {

    @Autowired
    private BinaryContentRepository repository;

    @Autowired
    private BinaryContentMapper mapper;

    private static final Logger LOG = Logger.getLogger(BinaryContentManager.class);

    @Transactional(readOnly = true)
    public BinaryContent getBinaryContent(long id) {
        LOG.info("Getting binary content with id=" + id);
        BinaryContentEntity entity = repository.findOne(id);
        return mapper.toDomain(entity);
    }

    @Transactional
    public boolean removeBinaryContent(long id) {
        LOG.info("Removing binary content with id=" + id);
        try {
            repository.delete(id);
        } catch (EmptyResultDataAccessException erdae) {
            LOG.info(erdae.getMessage());
            return false;
        }
        return true;
    }
}
