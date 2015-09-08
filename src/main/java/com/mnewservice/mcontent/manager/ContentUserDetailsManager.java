package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.repository.SubscriberRepository;
import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class ContentUserDetailsManager implements UserDetailsService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber)
            throws UsernameNotFoundException {
        SubscriberEntity subscriber
                = subscriberRepository.findByPhoneNumber(phoneNumber);
        if (subscriber == null) {
            throw new UsernameNotFoundException("not found " + phoneNumber);
        }

        return new User(
                subscriber.getPhone().getNumber(),
                passwordEncoder.encode(""),
                new ArrayList<>());

    }
}
