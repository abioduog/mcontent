package com.mnewservice.mcontent.web;

import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Configuration
public class MvcConfig extends WebMvcAutoConfigurationAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        //registry.addViewController("/service").setViewName("service");
        //registry.addViewController("/service/list").setViewName("serviceList");
        registry.addViewController("/provider").setViewName("provider");
        registry.addViewController("/subscriber").setViewName("subscriber");
        registry.addViewController("/content").setViewName("content");
        registry.addViewController("/report").setViewName("report");
    }
}
