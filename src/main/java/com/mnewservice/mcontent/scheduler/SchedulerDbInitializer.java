package com.mnewservice.mcontent.scheduler;

import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Configuration
@ConditionalOnProperty(name = "quartz.datasource.initialize")
public class SchedulerDbInitializer {

    @Value("classpath:create-quartz-schema.sql")
    private Resource createScript;

    @Value("classpath:drop-quartz-schema.sql")
    private Resource dropScript;

    private static final Logger LOG = Logger.getLogger(SchedulerDbInitializer.class);

    @Bean
    public DataSourceInitializer schedulerDataSourceInitializer(final DataSource dataSource) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(schedulerDatabasePopulator());
        return initializer;
    }

    private DatabasePopulator schedulerDatabasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(dropScript);
        LOG.debug("dropScript: " + dropScript);
        populator.addScript(createScript);
        LOG.debug("createScript: " + createScript);
        return populator;
    }
}
