package com.mnewservice.mcontent;

import com.mnewservice.mcontent.scheduler.SchedulerConfig;
import com.mnewservice.mcontent.scheduler.SchedulerDbInitializer;
import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
@Import({SchedulerConfig.class, SchedulerDbInitializer.class})
public class Application extends SpringBootServletInitializer {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US); // Set a default Locale that this resolver will return if no other locale found.
        return slr;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
