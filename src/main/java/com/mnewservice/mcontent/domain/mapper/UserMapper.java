package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.repository.entity.RoleEntity;
import com.mnewservice.mcontent.repository.entity.UserEntity;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class UserMapper extends AbstractMapper<User, UserEntity> {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        User domain = new User();
        domain.setId(entity.getId());
        domain.setUsername(entity.getUsername());
        domain.setPassword(entity.getPassword());
        domain.setActive(entity.isActive());
        domain.setRoles(roleMapper.toDomain(entity.getRoles()));

        return domain;
    }

    @Override
    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setActive(domain.isActive());
        Collection<RoleEntity> roles = roleMapper.toEntity(domain.getRoles());
        entity.setRoles(roles == null ? null : roles.stream().collect(Collectors.toSet()));

        return entity;
    }

}
