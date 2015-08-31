package com.mnewservice.mcontent;

import com.mnewservice.mcontent.scheduler.SchedulerConfig;
import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
@Import({SchedulerConfig.class})
public class Application {

    public static void main(String[] args) {
        /*ApplicationContext ctx =*/ SpringApplication.run(Application.class, args);
        /*
         System.out.println("Let's inspect the beans provided by Spring Boot:");

         String[] beanNames = ctx.getBeanDefinitionNames();
         Arrays.sort(beanNames);
         for (String beanName : beanNames) {
         System.out.println(beanName);
         }
         */
    }
}
