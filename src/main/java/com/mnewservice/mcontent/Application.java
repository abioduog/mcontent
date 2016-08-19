package com.mnewservice.mcontent;

import com.mnewservice.mcontent.scheduler.SchedulerConfig;
import com.mnewservice.mcontent.scheduler.SchedulerDbInitializer;
import java.io.File;
import java.util.Locale;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 * Edited by Pasi Saukkonen <pasi.saukkonen at nolwenture.com>
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

                return application
                .sources(Application.class)
                .properties(getProperties());
    }
    

    public static void main(String[] args) {
        //SpringApplication.run(Application.class, args);
                SpringApplicationBuilder springApplicationBuilder = (SpringApplicationBuilder) new SpringApplicationBuilder(Application.class)
                .sources(Application.class)
                .properties(getProperties())
                .run(args);
    }

       static Properties getProperties() {
           //props.put("spring.config.location", "file:/home/ubuntu1604/webservers/apache-tomcat-8.0.36/lib/test/application.properties"); // toimii
      
      Properties props = new Properties();
       
      props.put("spring.config.location", "file:"+System.getProperty( "catalina.base" ) + File.separator +"conf" + File.separator );
      
      //System.out.println(props.toString());
      
      return props;
   }
}
