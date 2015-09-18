package com.mnewservice.mcontent.messaging;

import com.mnewservice.mcontent.domain.AbstractMessage;
import com.mnewservice.mcontent.domain.PhoneNumber;
import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.util.StreamUtils;
import com.mnewservice.mcontent.util.exception.MessagingException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
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
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class MessageCenter {

    private static final Logger LOG = Logger.getLogger(MessageCenter.class);

    private static final String PROTOCOL = "http";
    private static final String PARAMETER_USERNAME = "username";
    private static final String PARAMETER_PASSWORD = "password";
    private static final String PARAMETER_TO = "to";
    private static final String PARAMETER_FROM = "from";
    private static final String PARAMETER_MESSAGE = "text";

    private static final String RECEIVER_SEPARATOR = " ";

    private static final String MESSAGE_START_SENDING
            = "START: Sending message '%s' from %s to %s";
    private static final String MESSAGE_END_SENDING = "END: Sending message";

    private static final String ERROR_NOT_IMPLEMENTED = "Not implemented";
    private static final String ERROR_URI_SYNTAX = "Error in URI syntax: %s";
    private static final String ERROR_ILLEGAL_NUMBER_OF_RECEIVERS
            = "Illegal number of receivers (valid range %d-%d)";
    private static final String ERROR_COULD_NOT_GET_RESPONSE
            = "Could not get response";
    private static final String ERROR_INVALID_STATUS
            = "Invalid status: %d (reason: %s)";
    private static final String ERROR_COMMUNICATION = "Communication error";

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

    public void sendMessage(AbstractMessage message, int fromNumber)
            throws MessagingException {
        if (!(message instanceof SmsMessage)) {
            // TODO
            throw new UnsupportedOperationException(ERROR_NOT_IMPLEMENTED);
        }

        SmsMessage smsMessage = (SmsMessage) message;
        String receiverNumbers = buildReceiverNumbers(smsMessage);

        try {
            // TODO: record sent messages somewhere?
            doSendMessage(
                    message.getMessage(),
                    receiverNumbers,
                    Integer.toString(fromNumber)
            );
        } catch (URISyntaxException ex) {
            String msg = String.format(ERROR_URI_SYNTAX, ex.getMessage());
            LOG.error(msg);
            throw new MessagingException(msg, ex);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            LOG.error(msg);
            throw new MessagingException(msg, ex);
        }
    }

    private String buildReceiverNumbers(SmsMessage smsMessage) {
        Collection<PhoneNumber> receivers = smsMessage.getReceivers();
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
                    .append(RECEIVER_SEPARATOR);
        }
        receiverNumbers.deleteCharAt(receiverNumbers.length() - 1);

        return receiverNumbers.toString();
    }

    private void doSendMessage(
            String message, String receiverNumbers, String fromNumber)
            throws MessagingException, URISyntaxException {
        LOG.debug(String.format(MESSAGE_START_SENDING,
                message,
                fromNumber,
                receiverNumbers)
        );

        URI uri = new URIBuilder()
                .setScheme(PROTOCOL)
                .setHost(gatewayIp).setPort(gatewayPort)
                .setPath(gatewayPath)
                .setParameter(PARAMETER_USERNAME, gatewayUsername)
                .setParameter(PARAMETER_PASSWORD, gatewayPassword)
                .setParameter(PARAMETER_TO, receiverNumbers)
                .setParameter(PARAMETER_FROM, fromNumber)
                .setParameter(PARAMETER_MESSAGE, message)
                .build();

        try (CloseableHttpResponse response = httpClient.execute(new HttpGet(uri))) {
            if (response == null || response.getStatusLine() == null) {
                LOG.error(ERROR_COULD_NOT_GET_RESPONSE);
                throw new MessagingException(ERROR_COULD_NOT_GET_RESPONSE);
            }

            HttpEntity entity = response.getEntity();
            logHeadersAndContent(response, entity);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String reason = response.getStatusLine().getReasonPhrase();
                String msg = String.format(
                        ERROR_INVALID_STATUS, statusCode, reason
                );
                LOG.error(msg);
                throw new MessagingException(msg);
            }
        } catch (IOException ioe) {
            LOG.error(ERROR_COMMUNICATION);
            throw new MessagingException(ERROR_COMMUNICATION, ioe);
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
