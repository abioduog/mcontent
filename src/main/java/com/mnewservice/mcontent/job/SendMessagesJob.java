package com.mnewservice.mcontent.job;

import com.mnewservice.mcontent.domain.Hello;
import com.mnewservice.mcontent.manager.HelloManager;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class SendMessagesJob implements Job {

    private static final Logger LOG = Logger.getLogger(SendMessagesJob.class);

    @Autowired
    private HelloManager helloManager;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        Collection<Hello> hellos = helloManager.getAll();
        for (Hello hello : hellos) {
            LOG.debug(hello.getId() + "; " + hello.getName());
        }
    }

}
