package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.repository.UserRepository;
import com.mnewservice.mcontent.repository.entity.UserEntity;
import com.mnewservice.mcontent.repository.entity.RoleEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 *
 * Convert UserEntity and UserRoleEntity into their internal Spring Security
 * counterparts
 *
 */
@Service
public class CustomUserDetailsManager implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("not found " + username);
        }
        List<GrantedAuthority> authorities = buildUserAuthority(user.getRoles());

        return buildUserForAuthentication(user, authorities);

    }

    private List<GrantedAuthority> buildUserAuthority(Set<RoleEntity> roles) {
        Set<GrantedAuthority> setAuths = new HashSet<>();

        // Build user's authorities
        for (RoleEntity role : roles) {
            setAuths.add(new SimpleGrantedAuthority(role.getName().name()));
        }

        return new ArrayList<>(setAuths);
    }

    private UserDetails buildUserForAuthentication(UserEntity user, List<GrantedAuthority> authorities) {
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
