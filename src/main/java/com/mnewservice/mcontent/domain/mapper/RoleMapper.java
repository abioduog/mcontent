package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Role;
import com.mnewservice.mcontent.repository.entity.RoleEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class RoleMapper extends AbstractMapper<Role, RoleEntity> {

    @Override
    public Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }

        Role domain = new Role();
        domain.setId(entity.getId());
        domain.setName(entity.getName().name());

        return domain;
    }

    @Override
    public RoleEntity toEntity(Role domain) {
        if (domain == null) {
            return null;
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(domain.getId());
        entity.setName(RoleEntity.RoleEnum.valueOf(domain.getName()));

        return entity;
    }

}
