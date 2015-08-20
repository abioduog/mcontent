package com.mnewservice.mcontent.scheduler;

import com.mnewservice.mcontent.job.DeliveryJob;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class SchedulerConfig {

    private static final Logger LOG = Logger.getLogger(SchedulerConfig.class);

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            DataSource dataSource,
            JobFactory jobFactory,
            @Qualifier("jobTrigger") Trigger jobTrigger)
            throws Exception {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // allow to update triggers in DB, when updating settings in config file
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);

        factory.setQuartzProperties(quartzProperties());
        factory.setTriggers(jobTrigger);

        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    public JobDetailFactoryBean jobDetail() {
        return createJobDetail(DeliveryJob.class);
    }

    @Bean(name = "jobTrigger")
    public CronTriggerFactoryBean jobTrigger(
            @Qualifier("jobDetail") JobDetail jobDetail,
            @Value("#{'${quartz.job.deliveryTimes}'.split(',')}") List<String> deliveryTimes) {
        for (String deliveryTime : deliveryTimes) {
            LOG.debug("deliveryTime: " + deliveryTime);
        }

        // TODO: create an own trigger for every delivery time
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression("0 * * * * ?");

        return factoryBean;
    }

    private static JobDetailFactoryBean createJobDetail(Class<?> jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // job has to be durable like the Finnish Bedrock
        factoryBean.setDurability(true);
        return factoryBean;
    }
    /*
     private SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequency) {
     SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
     factoryBean.setJobDetail(jobDetail);
     factoryBean.setStartDelay(10000);
     factoryBean.setRepeatInterval(pollFrequency);
     factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
     factoryBean.setMisfireInstruction(
     SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
     return factoryBean;
     }
     */
}
