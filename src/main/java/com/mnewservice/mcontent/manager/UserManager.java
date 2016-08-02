package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Role;
import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.domain.mapper.RoleMapper;
import com.mnewservice.mcontent.domain.mapper.UserMapper;
import com.mnewservice.mcontent.repository.RoleRepository;
import com.mnewservice.mcontent.repository.UserRepository;
import com.mnewservice.mcontent.repository.entity.RoleEntity;
import com.mnewservice.mcontent.repository.entity.RoleEntity.RoleEnum;
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
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private RoleMapper roleMapper;

    private static final Logger LOG = Logger.getLogger(UserManager.class);

    @Transactional(readOnly = true)
    public Collection<User> getAllUsers() {
        LOG.info("Getting all users");
        Collection<UserEntity> entities = mapper.makeCollection(repository.findAll());
        return mapper.toDomain(entities);
    }

    @Transactional(readOnly = true)
    public User getUser(long id) {
        LOG.info("Getting user with id=" + id);
        UserEntity entity = repository.findOne(id);
        return mapper.toDomain(entity);
    }
    
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        LOG.info("Getting user with username=" + username);
        UserEntity entity = repository.findByUsername(username);
        return mapper.toDomain(entity);
    }


    @Transactional(readOnly = true)
    public Collection<User> getAllUsersByRoleName(String roleName) {
        LOG.info("Getting all users by role name " + roleName);
        RoleEnum roleEnum = RoleMapper.toRoleEnum(roleName);
        Collection<UserEntity> entities
                = repository.findByRolesNameOrderByUsernameAsc(roleEnum);
        return mapper.toDomain(entities);
    }

    @Transactional
    public User saveUser(User user) {
        LOG.info("Saving user with id=" + user.getId());
        UserEntity entity = mapper.toEntity(user);
        return mapper.toDomain(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Role getRoleByName(String roleName) {
        LOG.info("Getting role by name " + roleName);
        RoleEnum roleEnum = RoleMapper.toRoleEnum(roleName);
        RoleEntity entity = roleRepository.findByName(roleEnum);
        return roleMapper.toDomain(entity);
    }
}
