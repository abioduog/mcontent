package com.mnewservice.mcontent;

import com.mnewservice.mcontent.scheduler.SchedulerConfig;
import com.mnewservice.mcontent.scheduler.SchedulerDbInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
@Import({SchedulerConfig.class, SchedulerDbInitializer.class})
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
