package com.mnewservice.mcontent.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final int REMEMBERME_TOKEN_VALIDITY = 7776000; // 90 days
    private static final String REMEMBERME_KEY = "9yNeDk9nxP6985wRnbQ8YkDbvrKg1yhy";

    @Bean
    public static PasswordEncoder applicationPasswordEncoder() {
        return PasswordEncrypter.getInstance().getPasswordEncoder();
    }

    @Bean
    public static PasswordEncoder contentPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
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
                    .defaultSuccessUrl("/show/a/default", false)
                    .and()
                    .logout()
                    .logoutUrl("/show/logout")
                    .deleteCookies("remember-me")
                    .logoutSuccessUrl("/show/login?logout").permitAll()
                    .and()
                    .rememberMe()
                    .key(REMEMBERME_KEY)
                    .tokenValiditySeconds(REMEMBERME_TOKEN_VALIDITY)
                    .and()
                    .sessionManagement().invalidSessionUrl("/show/login");
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(contentUserDetailsManager)
                    .passwordEncoder(contentPasswordEncoder());
        }
    }

    @Configuration
    @Order(2)
    public static class ApplicationWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsService applicationUserDetailsManager;

        @Value("${application.sms.gateway.ip}")
        private String smsGatewayIp;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests().antMatchers("/subscription").permitAll()//.hasIpAddress(smsGatewayIp)
                    .and()
                    .authorizeRequests().antMatchers("/register/provider").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/register/providerSignUp").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/lib/**").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/img/**").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/theme/**").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/login/resetPw").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/login/confirmPw").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/registration").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/discover").permitAll()
                    .and()
                    .authorizeRequests().anyRequest().hasAnyAuthority("ADMIN", "PROVIDER")
                    .and()
                    .formLogin()
                    .loginPage("/login").permitAll()
                    .and()
                    .logout().permitAll()
                    .and()
                    .sessionManagement().invalidSessionUrl("/login");
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(applicationUserDetailsManager)
                    .passwordEncoder(applicationPasswordEncoder());
        }
    }

}
