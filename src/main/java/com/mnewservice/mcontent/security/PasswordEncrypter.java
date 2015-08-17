package com.mnewservice.mcontent.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class PasswordEncrypter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static PasswordEncrypter instance;

    public static PasswordEncrypter getInstance() {
        if (instance == null) {
            instance = new PasswordEncrypter();
        }

        return instance;
    }

    public String encrypt(String pw) {
        return passwordEncoder.encode(pw);
    }
}
