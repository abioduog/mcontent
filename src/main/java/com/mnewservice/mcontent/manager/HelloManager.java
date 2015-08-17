package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Hello;
import com.mnewservice.mcontent.domain.mapper.HelloMapper;
import com.mnewservice.mcontent.repository.HelloRepository;
import com.mnewservice.mcontent.repository.entity.HelloEntity;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class HelloManager {

    private static final Logger LOG = Logger.getLogger(HelloManager.class);

    @Autowired
    private HelloRepository helloRepository;

    @Autowired
    private HelloMapper helloMapper;

    public Collection<Hello> getAll() {
        Collection<HelloEntity> entities
                = helloMapper.makeCollection(helloRepository.findAll());

        int count = entities != null ? entities.size() : 0;
        LOG.info("Found " + count + " hellos.");

        return helloMapper.toDomain(entities);
    }

}
