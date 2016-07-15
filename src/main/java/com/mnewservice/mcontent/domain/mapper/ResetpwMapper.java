/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Resetpw;
import com.mnewservice.mcontent.repository.entity.ResetpwEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author Pasi Saukkonen <pasi.saukkonen at nolwenture.com>
 */
@Component
public class ResetpwMapper extends AbstractMapper<Resetpw, ResetpwEntity> {
    
        @Override
    public Resetpw toDomain(ResetpwEntity entity) {
        if (entity == null) {
            return null;
        }
        Resetpw domain = new Resetpw();
        domain.setChecksum(entity.getChecksum());
        domain.setExpires(entity.getExpires());
        domain.setUserid(entity.getUserid());
        return domain;
    }

    @Override
    public ResetpwEntity toEntity(Resetpw domain) {
        if (domain == null) {
            return null;
        }
        ResetpwEntity entity = new ResetpwEntity();
        entity.setChecksum(domain.getChecksum());
        entity.setExpires(domain.getExpires());
        entity.setUserid(domain.getUserid());
        return entity;
    }
}
