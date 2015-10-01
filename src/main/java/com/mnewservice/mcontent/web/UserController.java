package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Email;
import com.mnewservice.mcontent.domain.EmailMessage;
import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.manager.ProviderManager;
import com.mnewservice.mcontent.manager.UserManager;
import com.mnewservice.mcontent.messaging.MessageCenter;
import com.mnewservice.mcontent.util.exception.MessagingException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class UserController {

    private static final Logger LOG
            = Logger.getLogger(UserController.class);

    @Value("${application.notification.from.address}")
    private String notificationFromAddress;

    @Autowired
    private UserManager userManager;

    @Autowired
    private ProviderManager providerManager;

    @Autowired
    private MessageCenter messageCenter;

    @RequestMapping({"/user/{id}/activate"})
    @ResponseStatus(value = HttpStatus.OK)
    public void activateProvider(@PathVariable("id") long id) {
        User user = userManager.getUser(id);
        if (user.isActive()) {
            throw new IllegalStateException("User is already active");
        }
        user.setActive(true);
        userManager.saveUser(user);

        //  TODO: which subject?
        String notificationSubject = "notification";
        //  TODO: which message to send?
        String notificationMessage = "your account has been activated";

        notify(user, notificationSubject, notificationMessage);
    }

    @RequestMapping({"/user/{id}/deactivate"})
    @ResponseStatus(value = HttpStatus.OK)
    public void deactivateProvider(@PathVariable("id") long id) {
        User user = userManager.getUser(id);
        if (!user.isActive()) {
            throw new IllegalStateException("User is already inactive");
        }
        user.setActive(false);
        userManager.saveUser(user);

        //  TODO: which subject?
        String notificationSubject = "notification";
        //  TODO: which message to send?
        String notificationMessage = "your account has been deactivated";

        notify(user, notificationSubject, notificationMessage);
    }

    private void notify(User user, String notificationSubject,
            String notificationMessage) {
        Provider provider = providerManager.findByUserId(user.getId());
        if (provider != null) {
            Email senderEmail = new Email();
            senderEmail.setAddress(notificationFromAddress);
            EmailMessage message = new EmailMessage();
            message.setSender(senderEmail);
            message.getReceivers().add(provider.getEmail());
            message.setSubject(notificationSubject);
            message.setMessage(notificationMessage);
            sendMessage(message);
        }
    }

    private void sendMessage(EmailMessage message) {
        LOG.info(String.format(
                "Sending message, start (%d receivers)",
                message.getReceivers().size())
        );
        try {
            messageCenter.sendMessage(message);
        } catch (MessagingException ex) {
            LOG.error("Sending message failed: " + ex.getMessage());
        } finally {
            LOG.info("Sending message, end");
        }
    }
}
