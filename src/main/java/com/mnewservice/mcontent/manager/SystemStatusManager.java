package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.domain.mapper.SmsMessageMapper;
import com.mnewservice.mcontent.repository.SmsMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Antti Vikman on 7.3.2016.
 */
@Service
public class SystemStatusManager {

    @Autowired
    private SmsMessageMapper smsMessageMapper;

    @Autowired
    private SmsMessageRepository smsMessageRepository;

    public List<SmsMessage> getSmsMessages() {
        return smsMessageRepository.findAllByOrderByCreatedDesc().stream().map(smsMessageMapper::toDomain).collect(Collectors.toList());
    }
}
