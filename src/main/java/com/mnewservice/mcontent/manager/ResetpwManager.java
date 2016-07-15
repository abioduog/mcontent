/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnewservice.mcontent.manager;


import com.mnewservice.mcontent.domain.Resetpw;
import com.mnewservice.mcontent.domain.mapper.ResetpwMapper;
import com.mnewservice.mcontent.repository.ResetpwRepository;
import com.mnewservice.mcontent.repository.entity.ResetpwEntity;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Pasi Saukkonen <pasi.saukkonen at nolwenture.com>
 */
@Service
public class ResetpwManager {
    
    @Autowired
    private ResetpwRepository repository;

    @Autowired
    private ResetpwMapper mapper;
    
    private static final Logger LOG = Logger.getLogger(ResetpwManager.class);
    
     @Transactional(readOnly = true)
    public Collection<Resetpw> getAllResetpws() {
        LOG.info("Getting all providers");
        Collection<ResetpwEntity> entities
                = mapper.makeCollection(repository.findAll());
        return mapper.toDomain(entities);
    }
    
    @Transactional(readOnly = true)
    public Resetpw findByChecksum(String checkcsum) {
        LOG.info("Finding resetpw with checksum=" + checkcsum);
        return mapper.toDomain(repository.findByChecksum(checkcsum));
    }
        @Transactional
    public Resetpw saveResetpw(Resetpw resetpw) {
        LOG.info("Saving resetpw with checksum=" + resetpw.getChecksum());
        ResetpwEntity entity = mapper.toEntity(resetpw);
        return mapper.toDomain(repository.save(entity));
    }
    
}
