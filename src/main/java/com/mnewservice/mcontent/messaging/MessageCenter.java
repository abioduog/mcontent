package com.mnewservice.mcontent.messaging;

import com.mnewservice.mcontent.domain.AbstractMessage;
import com.mnewservice.mcontent.domain.PhoneNumber;
import com.mnewservice.mcontent.domain.SmsMessage;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class MessageCenter {

    private static final Logger LOG = Logger.getLogger(MessageCenter.class);

    public void sendMessage(AbstractMessage message) {
        if (!(message instanceof SmsMessage)) {
            // TODO
            throw new UnsupportedOperationException("not implemented");
        }

        // TODO: record sent messages somewhere?
        LOG.debug("message content: " + message.getMessage());
        for (PhoneNumber phoneNumber : ((SmsMessage) message).getReceivers()) {
            LOG.debug("recipient phone number: " + phoneNumber.getNumber());
        }

        // TODO use SMS gateway --> throw an exception in the case of failure
    }
}
