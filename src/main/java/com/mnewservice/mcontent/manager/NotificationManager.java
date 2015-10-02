package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.Email;
import com.mnewservice.mcontent.domain.EmailMessage;
import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.messaging.MessageCenter;
import com.mnewservice.mcontent.util.exception.MessagingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class NotificationManager {

    private static final Logger LOG
            = Logger.getLogger(NotificationManager.class);

    @Value("${application.notification.admin.addresses}")
    private String[] notificationAdminAddresses;

    @Value("${application.notification.from.address}")
    private String notificationFromAddress;

    @Autowired
    private MessageCenter messageCenter;

    private Email notificationFromEmailAddress;
    private Collection<Email> notificationAdminEmailAddresses;

    public boolean notifyAdmin(String notificationSubject,
            String notificationMessage) {
        EmailMessage message = new EmailMessage();
        message.setSender(getNotificationFromEmailAddress());
        message.getReceivers().addAll(getAdminEmailAddresses());
        message.setSubject(notificationSubject);
        message.setMessage(notificationMessage);

        return sendMessage(message);
    }

    public boolean notifyProviders(Collection<Provider> providers,
            String notificationSubject, String notificationMessage) {
        EmailMessage message
                = createEmailMessage(notificationSubject, notificationMessage);
        Collection<Email> emails
                = providers.stream().map(p -> p.getEmail()).collect(Collectors.toList());
        message.getReceivers().addAll(emails);

        return sendMessage(message);
    }

    public boolean notifyProvider(Provider provider, String notificationSubject,
            String notificationMessage) {
        EmailMessage message
                = createEmailMessage(notificationSubject, notificationMessage);
        message.getReceivers().add(provider.getEmail());

        return sendMessage(message);
    }

    private EmailMessage createEmailMessage(String notificationSubject,
            String notificationMessage) {
        EmailMessage message = new EmailMessage();
        message.setSender(getNotificationFromEmailAddress());
        message.setSubject(notificationSubject);
        message.setMessage(notificationMessage);
        return message;
    }

    private Email getNotificationFromEmailAddress() {
        if (notificationFromEmailAddress == null) {
            notificationFromEmailAddress = createEmail(notificationFromAddress);
        }
        return notificationFromEmailAddress;
    }

    private Collection<Email> getAdminEmailAddresses() {
        if (notificationAdminEmailAddresses == null) {
            notificationAdminEmailAddresses
                    = createEmails(notificationAdminAddresses);
        }
        return notificationAdminEmailAddresses;
    }

    private Collection<Email> createEmails(String[] addresses) {
        if (notificationAdminAddresses == null) {
            return null;
        }
        Collection<Email> emails = new ArrayList<>();
        for (String address : addresses) {
            Email email = createEmail(address);
            emails.add(email);
        }
        return emails;
    }

    private Email createEmail(String address) {
        Email email = new Email();
        email.setAddress(address);
        return email;
    }

    private boolean sendMessage(EmailMessage message) {
        LOG.info(String.format(
                "Sending notification, start (%d receivers)",
                message.getReceivers().size())
        );
        try {
            messageCenter.sendMessage(message);
        } catch (MessagingException ex) {
            LOG.error("Sending notification failed: " + ex.getMessage());
            return false;
        } finally {
            LOG.info("Sending notification, end");
        }

        return true;
    }

}
