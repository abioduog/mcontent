package com.mnewservice.mcontent.messaging;

import com.mnewservice.mcontent.domain.EmailMessage;
import com.mnewservice.mcontent.domain.PhoneNumber;
import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.domain.mapper.SmsMessageMapper;
import com.mnewservice.mcontent.repository.ServiceRepository;
import com.mnewservice.mcontent.repository.SmsMessageRepository;
import com.mnewservice.mcontent.repository.entity.SmsMessageEntity;
import com.mnewservice.mcontent.util.StreamUtils;
import com.mnewservice.mcontent.util.exception.MessagingException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.Queue;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class MessageCenter {

    private static final Logger LOG = Logger.getLogger(MessageCenter.class);

    private static final int SMS_MAX_TRIES = 5;

    private static final String SMS_PROTOCOL = "http";
    private static final String SMS_PARAMETER_USERNAME = "username";
    private static final String SMS_PARAMETER_PASSWORD = "password";
    private static final String SMS_PARAMETER_TO = "to";
    private static final String SMS_PARAMETER_FROM = "from";
    private static final String SMS_PARAMETER_MESSAGE = "text";
    private static final String SMS_RECEIVER_SEPARATOR = " ";

    private static final String MESSAGE_START_SENDING
            = "START: Sending message '%s' from %s to %s";
    private static final String MESSAGE_END_SENDING = "END: Sending message";

    private static final String ERROR_URI_SYNTAX = "Error in URI syntax: %s";
    private static final String ERROR_ILLEGAL_NUMBER_OF_RECEIVERS
            = "Illegal number of receivers (valid range %d-%d)";
    private static final String ERROR_COULD_NOT_GET_RESPONSE
            = "Could not get response";
    private static final String ERROR_INVALID_STATUS
            = "Invalid status: %d (reason: %s)";
    private static final String ERROR_COMMUNICATION = "Communication error: %s";

    @Autowired
    private SmsMessageMapper smsMessageMapper;

    @Autowired
    private SmsMessageRepository smsMessageRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private CloseableHttpClient httpClient;

    @Value("${application.sms.gateway.ip}")
    private String gatewayIp;
    @Value("${application.sms.gateway.port}")
    private int gatewayPort;
    @Value("${application.sms.gateway.path}")
    private String gatewayPath;
    @Value("${application.sms.gateway.username}")
    private String gatewayUsername;
    @Value("${application.sms.gateway.password}")
    private String gatewayPassword;
    @Value("${application.sms.gateway.maxRecipients}")
    private int maxRecipients;

    public void queueMessage(EmailMessage message) throws MessagingException {
        try {
            // TODO: record sent messages somewhere?
            doSendEmailMessage(
                    message.getSender().getAddress(),
                    message.getReceivers().stream().map(r -> r.getAddress()).toArray(size -> new String[size]),
                    message.getSubject(),
                    message.getMessage()
            );
        } catch (MailException me) {
            String msg = me.getMessage();
            LOG.error(msg);
            throw new MessagingException(msg, me);
        }
    }

    private void doSendEmailMessage(String from, String[] to,
            String subject, String text) {
        LOG.debug(String.format(MESSAGE_START_SENDING,
                subject + ": " + text,
                from,
                String.join(" ", to))
        );
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(text);
            javaMailSender.send(mailMessage);
        } finally {
            LOG.debug(MESSAGE_END_SENDING);
        }
    }

    public void queueMessage(SmsMessage message) {
        smsMessageRepository.save(smsMessageMapper.toEntity(message));
    }

    private void sendSmsMessage(SmsMessageEntity message) {
        String receiverNumbers = buildReceiverNumbers(message.getReceivers());

        try {
            message.incrementTries();
            doSendSmsMessage(
                    message.getMessage(),
                    receiverNumbers,
                    message.getFromNumber()
            );
            message.setSent(new Date());
            message.log("Sent successfully");
        } catch (URISyntaxException ex) {
            String msg = String.format(ERROR_URI_SYNTAX, ex.getMessage());
            LOG.error(msg);
            message.log(msg);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            LOG.error(msg);
            message.log(msg);
        } catch (MessagingException ex) {
            String msg = ex.getMessage();
            LOG.error(msg);
            message.log(msg);
        }
        smsMessageRepository.save(message);
    }

    public void sendSmsMessages() {
        smsMessageRepository.findBySentIsNullAndTriesLessThan(SMS_MAX_TRIES).forEach(message -> sendSmsMessage(message));
    }

    private String buildReceiverNumbers(Collection<PhoneNumber> receivers) {
        if ((receivers == null || receivers.size() < 1)
                || receivers.size() > maxRecipients) {
            String message = String.format(
                    ERROR_ILLEGAL_NUMBER_OF_RECEIVERS,
                    1,
                    maxRecipients
            );
            throw new IllegalArgumentException(message);
        }

        StringBuilder receiverNumbers = new StringBuilder();
        for (PhoneNumber phoneNumber : receivers) {
            receiverNumbers
                    .append(phoneNumber.getNumber())
                    .append(SMS_RECEIVER_SEPARATOR);
        }
        receiverNumbers.deleteCharAt(receiverNumbers.length() - 1);

        return receiverNumbers.toString();
    }

    private void doSendSmsMessage(
            String message, String receiverNumbers, String fromNumber)
            throws MessagingException, URISyntaxException {
        LOG.debug(String.format(MESSAGE_START_SENDING,
                message,
                fromNumber,
                receiverNumbers)
        );

        URI uri = new URIBuilder()
                .setScheme(SMS_PROTOCOL)
                .setHost(gatewayIp).setPort(gatewayPort)
                .setPath(gatewayPath)
                .setParameter(SMS_PARAMETER_USERNAME, gatewayUsername)
                .setParameter(SMS_PARAMETER_PASSWORD, gatewayPassword)
                .setParameter(SMS_PARAMETER_TO, receiverNumbers)
                .setParameter(SMS_PARAMETER_FROM, fromNumber)
                .setParameter(SMS_PARAMETER_MESSAGE, message)
                .build();

        try (CloseableHttpResponse response = httpClient.execute(new HttpGet(uri))) {
            if (response == null || response.getStatusLine() == null) {
                LOG.error(ERROR_COULD_NOT_GET_RESPONSE);
                throw new MessagingException(ERROR_COULD_NOT_GET_RESPONSE);
            }

            HttpEntity entity = response.getEntity();
            logHeadersAndContent(response, entity);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_ACCEPTED) {
                String reason = response.getStatusLine().getReasonPhrase();
                String msg = String.format(
                        ERROR_INVALID_STATUS, statusCode, reason
                );
                LOG.error(msg);
                throw new MessagingException(msg);
            }
        } catch (IOException ioe) {
            String msg = String.format(
                    ERROR_COMMUNICATION, ioe.getMessage()
            );
            LOG.error(msg);
            throw new MessagingException(msg, ioe);
        } finally {
            LOG.debug(MESSAGE_END_SENDING);
        }
    }

    private void logHeadersAndContent(final CloseableHttpResponse response,
            HttpEntity entity) throws IOException {
        if (LOG.isDebugEnabled()) {
            if (response.getStatusLine() != null) {
                LOG.debug(
                        "Response status code: "
                        + response.getStatusLine().getStatusCode());
                LOG.debug(
                        "Response status reason: "
                        + response.getStatusLine().getReasonPhrase());
            }

            if (response.getAllHeaders() != null) {
                LOG.debug("Response headers:");
                for (Header header : response.getAllHeaders()) {
                    LOG.debug(header.getName() + "=" + header.getValue());
                }
            }

            if (entity != null) {
                LOG.debug("Response content:");
                LOG.debug(StreamUtils.convertStreamToString(entity.getContent()));
            }
        }
    }
}
