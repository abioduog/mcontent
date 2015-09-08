package com.mnewservice.mcontent.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static PasswordEncoder encoder;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        if (encoder == null) {
            encoder = new BCryptPasswordEncoder();
        }

        return encoder;
    }

    @Configuration
    @Order(1)
    public static class ContentWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsService contentUserDetailsManager;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/show/**")
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/show/login").permitAll()
                    .and()
                    .logout()
                    .logoutUrl("/show/logout")
                    .logoutSuccessUrl("/show/login?logout").permitAll();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(contentUserDetailsManager)
                    .passwordEncoder(passwordEncoder());
        }
    }

    @Configuration
    @Order(2)
    public static class ApplicationWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsService applicationUserDetailsManager;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests().antMatchers("/subscription").permitAll() //.hasIpAddress("127.0.0.1")
                    .and()
                    .authorizeRequests().antMatchers("/register/provider").permitAll()
                    .and()
                    .authorizeRequests().anyRequest().hasAnyAuthority("ADMIN", "PROVIDER")
                    .and()
                    .formLogin()
                    .loginPage("/login").permitAll()
                    .and()
                    .logout().permitAll();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(applicationUserDetailsManager)
                    .passwordEncoder(passwordEncoder());
        }
    }

}
