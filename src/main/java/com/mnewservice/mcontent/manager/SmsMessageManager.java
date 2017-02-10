package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.domain.mapper.SmsMessageMapper;
import com.mnewservice.mcontent.repository.SmsMessageRepository;
import com.mnewservice.mcontent.repository.entity.SmsMessageEntity;
import com.mnewservice.mcontent.util.SessionUtils;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@org.springframework.stereotype.Service
public class SmsMessageManager {

    @Autowired
    private SmsMessageRepository repository;

    @Autowired
    private SmsMessageMapper mapper;

    private static final Logger LOG = Logger.getLogger(SmsMessageManager.class);
    private static final String INFO_GETTING_LATEST_MESSAGES = "Getting latest messages (newer than %Tc) for user %s";

    @Transactional(readOnly = true)
    public Collection<SmsMessage> getLatestMessages(Date newerThan) {
        String phone = SessionUtils.getCurrentUserUsername();
        LOG.info(String.format(INFO_GETTING_LATEST_MESSAGES, newerThan, phone));
        List<SmsMessageEntity> entities = repository.findNewerThanAndReceiverPhone(newerThan, phone);
        return mapper.toDomain(entities);
    }

}
