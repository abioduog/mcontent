package com.mnewservice.mcontent.security;

import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class PasswordEncrypter {

    private static final Logger LOG = Logger.getLogger(PasswordEncrypter.class);

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static PasswordEncrypter instance;

    public static PasswordEncrypter getInstance() {
        if (instance == null) {
            instance = new PasswordEncrypter();
        }

        return instance;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public String encrypt(String pw) {
        return passwordEncoder.encode(pw);
    }
}
