package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.BinaryContent;
import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.domain.Role;
import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.manager.NotificationManager;
import com.mnewservice.mcontent.manager.ProviderManager;
import com.mnewservice.mcontent.manager.UserManager;
import com.mnewservice.mcontent.security.PasswordEncrypter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Controller
public class RegisterController {

    private static final Logger LOG
            = Logger.getLogger(RegisterController.class);

    private static final String ERROR_FAIL_TO_UPLOAD = "failed to upload %s (reason: %s)";
    private static final String REASON_EMPTY_FILE = "file was empty";
    private static final String REASON_READING_FAILED = "reading file failed";
    private static final String ERROR_USERNAME_TOO_SHORT = "Username too short. Min. 6 characters.";
    private static final String ERROR_PASSWORD_TOO_SHORT = "Password too short. Min. 8 characters.";

    @Value("${application.notification.provider.registered.message.subject}")
    private String providerRegisteredMessageSubject;

    @Value("${application.notification.provider.registered.message.text}")
    private String providerRegisteredMessageText;

    @Autowired
    private ProviderManager providerManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private NotificationManager notificationManager;

    @RequestMapping(value = {"/register/provider"}, method = RequestMethod.GET)
    public ModelAndView registerProvider() {
        ModelAndView mav = new ModelAndView("providerRegister");
        mav.addObject("provider", new Provider());
        return mav;
    }

    @RequestMapping(value = {"/register/provider"}, method = RequestMethod.POST)
    public ModelAndView registerProvider(
            final Provider provider,
            final BindingResult bindingResult,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("correspondenceFiles") MultipartFile[] correspondenceFiles,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("providerRegister");

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().forEach((error) -> {
                LOG.error(error.toString());
            });
            mav.addObject("provider", model.getOrDefault("provider", new Provider()));
            mav.addObject("error", true);
        } else {
            try {
                provider.setUser(createUser(username, password));
                provider.setCorrespondences(createCorrespondences(correspondenceFiles));

                // persist the object "provider"
                Provider savedProvider = providerManager.saveProvider(provider);

                mav = new ModelAndView("providerRegistered");
                mav.addObject("provider", savedProvider);

                registrationNotification(savedProvider);

            } catch (Exception ex) {
                LOG.error(ex);
                if (provider.getUser() != null) {
                    provider.getUser().setPassword(null);
                }
                mav.addObject("provider", provider);
                mav.addObject("error", true);
                mav.addObject("errortext", ex.getMessage());
            }
        }

        return mav;
    }

    private void registrationNotification(Provider savedProvider) {
        String notificationSubject = String.format(
                providerRegisteredMessageSubject,
                savedProvider.getName());
        String notificationMessage = String.format(
                providerRegisteredMessageText,
                savedProvider.getName(),
                savedProvider.getUser().getUsername());

        notificationManager.notifyAdmin(notificationSubject, notificationMessage);
    }

    private Collection<BinaryContent> createCorrespondences(
            MultipartFile[] correspondenceFiles) throws IllegalArgumentException {
        Collection<BinaryContent> correspondences = new ArrayList<>();
        for (MultipartFile correspondenceFile : correspondenceFiles) {
            try {
                checkCorrespondenceFile(correspondenceFile);
                BinaryContent correspondence = new BinaryContent();
                correspondence.setName(correspondenceFile.getOriginalFilename());
                correspondence.setContentType(correspondenceFile.getContentType());
                correspondence.setContent(correspondenceFile.getBytes());
                correspondences.add(correspondence);
            } catch (IOException ioe) {
                String message = String.format(
                        ERROR_FAIL_TO_UPLOAD,
                        correspondenceFile.getOriginalFilename(),
                        REASON_READING_FAILED);
                throw new RuntimeException(message, ioe);
            }
        }
        return correspondences;
    }

    private void checkCorrespondenceFile(MultipartFile correspondenceFile)
            throws IllegalArgumentException {
        if (correspondenceFile.isEmpty()) {
            String message = String.format(
                    ERROR_FAIL_TO_UPLOAD,
                    correspondenceFile.getOriginalFilename(),
                    REASON_EMPTY_FILE
            );
            throw new IllegalArgumentException(message);
        }
    }

    private User createUser(String username, String password) {
        User user = new User();
        if (username == null || username.length() < 6) {
            throw new IllegalArgumentException(ERROR_USERNAME_TOO_SHORT);
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(ERROR_PASSWORD_TOO_SHORT);
        }
        user.setUsername(username);
        user.setPassword(PasswordEncrypter.getInstance().encrypt(password));
        user.setRoles(
                Arrays.asList(
                        userManager.getRoleByName(Role.PROVIDER_SHOULD_BE_ENUM)
                )
        );
        user.setActive(false);
        return user;
    }

}
