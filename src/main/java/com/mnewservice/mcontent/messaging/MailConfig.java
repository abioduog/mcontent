package com.mnewservice.mcontent.messaging;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Configuration
public class MailConfig {

    @Value("${application.mail.protocol}")
    private String protocol;
    @Value("${application.mail.host}")
    private String host;
    @Value("${application.mail.port}")
    private int port;
    @Value("${application.mail.smtp.auth}")
    private boolean auth;
    @Value("${application.mail.smtp.starttls.enable}")
    private boolean starttls;
    @Value("${application.mail.username}")
    private String username;
    @Value("${application.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.starttls.enable", starttls);
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setProtocol(protocol);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        return mailSender;
    }
}
