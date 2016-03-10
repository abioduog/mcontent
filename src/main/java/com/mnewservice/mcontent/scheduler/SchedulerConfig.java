package com.mnewservice.mcontent.scheduler;

import com.mnewservice.mcontent.domain.DeliveryTime;
import com.mnewservice.mcontent.job.DeliveryJob;
import static com.mnewservice.mcontent.job.DeliveryJob.DELIVERY_TIME_PARAM;

import com.mnewservice.mcontent.messaging.MessageCenter;
import com.mnewservice.mcontent.util.CronUtils;
import com.mnewservice.mcontent.util.DeliveryTimeUtils;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
@EnableScheduling
public class SchedulerConfig {

    private static final String GROUP_MESSAGE_DELIVERY = "messageDelivery";

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
            @Qualifier("jobT0800Trigger") Trigger jobT0800Trigger,
            @Qualifier("jobT1000Trigger") Trigger jobT1000Trigger,
            @Qualifier("jobT1200Trigger") Trigger jobT1200Trigger,
            @Qualifier("jobT1400Trigger") Trigger jobT1400Trigger,
            @Qualifier("jobT1600Trigger") Trigger jobT1600Trigger,
            @Qualifier("jobT1800Trigger") Trigger jobT1800Trigger,
            @Qualifier("jobT2000Trigger") Trigger jobT2000Trigger,
            @Qualifier("jobT2200Trigger") Trigger jobT2200Trigger) throws Exception {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // allow to update triggers in DB, when updating settings in config file
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);

        factory.setQuartzProperties(quartzProperties());
        factory.setTriggers(
                jobT0800Trigger,
                jobT1000Trigger,
                jobT1200Trigger,
                jobT1400Trigger,
                jobT1600Trigger,
                jobT1800Trigger,
                jobT2000Trigger,
                jobT2200Trigger
        );

        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    // <editor-fold defaultstate="collapsed" desc="jobTxxxxDetail beans">
    @Bean
    public JobDetailFactoryBean jobT0800Detail() {
        return createJobDetail(DeliveryJob.class, DeliveryTime.T0800);
    }

    @Bean
    public JobDetailFactoryBean jobT1000Detail() {
        return createJobDetail(DeliveryJob.class, DeliveryTime.T1000);
    }

    @Bean
    public JobDetailFactoryBean jobT1200Detail() {
        return createJobDetail(DeliveryJob.class, DeliveryTime.T1200);
    }

    @Bean
    public JobDetailFactoryBean jobT1400Detail() {
        return createJobDetail(DeliveryJob.class, DeliveryTime.T1400);
    }

    @Bean
    public JobDetailFactoryBean jobT1600Detail() {
        return createJobDetail(DeliveryJob.class, DeliveryTime.T1600);
    }

    @Bean
    public JobDetailFactoryBean jobT1800Detail() {
        return createJobDetail(DeliveryJob.class, DeliveryTime.T1800);
    }

    @Bean
    public JobDetailFactoryBean jobT2000Detail() {
        return createJobDetail(DeliveryJob.class, DeliveryTime.T2000);
    }

    @Bean
    public JobDetailFactoryBean jobT2200Detail() {
        return createJobDetail(DeliveryJob.class, DeliveryTime.T2200);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="jobTxxxxTrigger beans">
    @Bean(name = "jobT0800Trigger")
    public CronTriggerFactoryBean jobT0800Trigger(
            @Qualifier("jobT0800Detail") JobDetail jobDetail) {
        return createTrigger(jobDetail, DeliveryTime.T0800);
    }

    @Bean(name = "jobT1000Trigger")
    public CronTriggerFactoryBean jobT1000Trigger(
            @Qualifier("jobT1000Detail") JobDetail jobDetail) {
        return createTrigger(jobDetail, DeliveryTime.T1000);
    }

    @Bean(name = "jobT1200Trigger")
    public CronTriggerFactoryBean jobT1200Trigger(
            @Qualifier("jobT1200Detail") JobDetail jobDetail) {
        return createTrigger(jobDetail, DeliveryTime.T1200);
    }

    @Bean(name = "jobT1400Trigger")
    public CronTriggerFactoryBean jobT1400Trigger(
            @Qualifier("jobT1400Detail") JobDetail jobDetail) {
        return createTrigger(jobDetail, DeliveryTime.T1400);
    }

    @Bean(name = "jobT1600Trigger")
    public CronTriggerFactoryBean jobT1600Trigger(
            @Qualifier("jobT1600Detail") JobDetail jobDetail) {
        return createTrigger(jobDetail, DeliveryTime.T1600);
    }

    @Bean(name = "jobT1800Trigger")
    public CronTriggerFactoryBean jobT1800Trigger(
            @Qualifier("jobT1800Detail") JobDetail jobDetail) {
        return createTrigger(jobDetail, DeliveryTime.T1800);
    }

    @Bean(name = "jobT2000Trigger")
    public CronTriggerFactoryBean jobT2000Trigger(
            @Qualifier("jobT2000Detail") JobDetail jobDetail) {
        return createTrigger(jobDetail, DeliveryTime.T2000);
    }

    @Bean(name = "jobT2200Trigger")
    public CronTriggerFactoryBean jobT2200Trigger(
            @Qualifier("jobT2200Detail") JobDetail jobDetail) {
        return createTrigger(jobDetail, DeliveryTime.T2200);
    }
    // </editor-fold>

    private CronTriggerFactoryBean createTrigger(
            JobDetail jobDetail, DeliveryTime deliveryTime) {
        String[] parsedDeliveryTime
                = DeliveryTimeUtils.parseDeliveryTimeAsStringArray(deliveryTime);
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);

        String cronExpression = CronUtils.generateCronExpression(
                parsedDeliveryTime[2],
                parsedDeliveryTime[1],
                parsedDeliveryTime[0],
                CronUtils.EVERY,
                CronUtils.EVERY,
                CronUtils.OMIT,
                CronUtils.NOT_GIVEN
        );

        factoryBean.setCronExpression(cronExpression);
        factoryBean.setName(deliveryTime.name());
        factoryBean.setGroup(GROUP_MESSAGE_DELIVERY);
        factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        return factoryBean;
    }

    private static JobDetailFactoryBean createJobDetail(
            Class<?> jobClass, DeliveryTime deliveryTime) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // job has to be durable like the Finnish Bedrock
        factoryBean.setDurability(true);
        factoryBean.setName(deliveryTime.name());
        factoryBean.setGroup(GROUP_MESSAGE_DELIVERY);

        JobDataMap jobDataMap = factoryBean.getJobDataMap();
        jobDataMap.put(DELIVERY_TIME_PARAM, deliveryTime);

        return factoryBean;
    }

    @Autowired
    private MessageCenter messageCenter;

    @Scheduled(fixedDelay = 30000)
    public void sendSmsMessages() {
        try {
            messageCenter.sendSmsMessages();
        } catch (Exception ex) {
            LOG.info("Critical failure on sending SMS messages: " + ex.getMessage());
        }
    }
}
