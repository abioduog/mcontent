package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.domain.mapper.RoleMapper;
import com.mnewservice.mcontent.domain.mapper.ServiceMapper;
import com.mnewservice.mcontent.domain.mapper.UserMapper;
import com.mnewservice.mcontent.repository.ServiceRepository;
import com.mnewservice.mcontent.repository.UserRepository;
import com.mnewservice.mcontent.repository.entity.RoleEntity.RoleEnum;
import com.mnewservice.mcontent.repository.entity.ServiceEntity;
import com.mnewservice.mcontent.repository.entity.UserEntity;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
// TODO: take account user rights
@org.springframework.stereotype.Service
public class UserManager {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper mapper;

    private static final Logger LOG = Logger.getLogger(UserManager.class);

    @Transactional(readOnly = true)
    public Collection<User> getAllUsers() {
        LOG.info("Getting all users");
        Collection<UserEntity> entities = mapper.makeCollection(repository.findAll());
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public Collection<User> getAllUsersByRoleName(String roleName) {
        LOG.info("Getting all users by role name " + roleName);
        RoleEnum roleEnum = RoleMapper.toRoleEnum(roleName);
        Collection<UserEntity> entities
                = repository.findByRolesNameOrderByUsernameAsc(roleEnum);
        return mapper.toDomain(entities);
    }
}
