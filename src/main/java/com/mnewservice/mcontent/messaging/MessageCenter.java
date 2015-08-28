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
        StringBuilder sb = new StringBuilder();
        for (PhoneNumber phoneNumber : ((SmsMessage) message).getReceivers()) {
            sb.append(phoneNumber.getNumber()).append(" ");
        }

        LOG.debug(String.format(
                "Sending message '%s' to %s",
                message.getMessage(),
                sb.toString()));

        // TODO use SMS gateway --> throw an exception in the case of failure
    }
}
