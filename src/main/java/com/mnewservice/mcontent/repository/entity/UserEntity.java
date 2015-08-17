package com.mnewservice.mcontent.repository.entity;

import com.mnewservice.mcontent.security.PasswordEncrypter;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "users")
public class UserEntity extends AbstractEntity {

    @Version
    private Long version;

    private String username;
    private String password;
    private String email;

    @OneToMany
    private Set<RoleEntity> roles;

    public UserEntity() {
    }

    public UserEntity(String username, String password, String email, Collection<RoleEntity> roles) {
        this.username = username;
        this.password = PasswordEncrypter.getInstance().encrypt(password);
        this.email = email;
        this.roles = roles.stream().collect(Collectors.toSet());
    }

// <editor-fold defaultstate="collapsed" desc="autogenerated getters / setters">
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }
// </editor-fold>

}
