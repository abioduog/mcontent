package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.manager.NotificationManager;
import com.mnewservice.mcontent.manager.ProviderManager;
import com.mnewservice.mcontent.manager.UserManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class UserController {

    private static final Logger LOG
            = Logger.getLogger(UserController.class);

    @Value("${application.notification.provider.activated.message.subject}")
    private String providerActivatedMessageSubject;

    @Value("${application.notification.provider.activated.message.text}")
    private String providerActivatedMessageText;

    @Value("${application.notification.provider.deactivated.message.subject}")
    private String providerDeactivatedMessageSubject;

    @Value("${application.notification.provider.deactivated.message.text}")
    private String providerDeactivatedMessageText;

    @Autowired
    private UserManager userManager;

    @Autowired
    private ProviderManager providerManager;

    @Autowired
    private NotificationManager notificationManager;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/user/{id}/activate"})
    @ResponseStatus(value = HttpStatus.OK)
    public void activateProvider(@PathVariable("id") long id) {
        User user = userManager.getUser(id);
        if (user.isActive()) {
            throw new IllegalStateException("User is already active");
        }
        user.setActive(true);
        userManager.saveUser(user);

        activationNotification(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/user/{id}/deactivate"})
    @ResponseStatus(value = HttpStatus.OK)
    public void deactivateProvider(@PathVariable("id") long id) {
        User user = userManager.getUser(id);
        if (!user.isActive()) {
            throw new IllegalStateException("User is already inactive");
        }
        user.setActive(false);
        userManager.saveUser(user);

        deactivationNotification(user);
    }

    private void activationNotification(User user) {
        String notificationSubject = String.format(
                providerActivatedMessageSubject);
        String notificationMessage = String.format(
                providerActivatedMessageText,
                user.getUsername());

        notify(user, notificationSubject, notificationMessage);
    }

    private void deactivationNotification(User user) {
        String notificationSubject = String.format(
                providerDeactivatedMessageSubject);
        String notificationMessage = String.format(
                providerDeactivatedMessageText,
                user.getUsername());

        notify(user, notificationSubject, notificationMessage);
    }

    private void notify(User user, String notificationSubject,
            String notificationMessage) {
        Provider provider = providerManager.findByUserId(user.getId());
        if (provider != null) {
            notificationManager.notifyProvider(
                    provider, notificationSubject, notificationMessage);
        } else {
            LOG.info(user);
        }
    }

}
