package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.repository.SubscriberRepository;
import com.mnewservice.mcontent.repository.entity.SubscriberEntity;
import com.mnewservice.mcontent.security.PasswordEncrypter;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class ContentUserDetailsManager implements UserDetailsService {

    private static final String PASSWORD = " ";

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber)
            throws UsernameNotFoundException {
        SubscriberEntity subscriber
                = subscriberRepository.findByPhoneNumber(phoneNumber);
        if (subscriber == null) {
            throw new UsernameNotFoundException("not found " + phoneNumber);
        }

        PasswordEncoder encoder = NoOpPasswordEncoder.getInstance();
        return new User(
                subscriber.getPhone().getNumber(),
                encoder.encode(PASSWORD),
                new ArrayList<>()
        );

    }
}
