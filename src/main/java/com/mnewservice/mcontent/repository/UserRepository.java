package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.RoleEntity;
import com.mnewservice.mcontent.repository.entity.UserEntity;
import java.util.Collection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    public UserEntity findByUsername(String username);

    public Collection<UserEntity> findByRolesNameOrderByUsernameAsc(RoleEntity.RoleEnum rolesName);
}
