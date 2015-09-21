package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.web.formatter.UserFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Configuration
public class MvcConfig extends WebMvcAutoConfigurationAdapter {

    @Autowired
    private UserFormatter userFormatter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(userFormatter);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/show/login").setViewName("showLogin");
    }
}
