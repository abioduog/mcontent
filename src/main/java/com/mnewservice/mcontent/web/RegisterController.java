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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

//<editor-fold defaultstate="collapsed" desc="error handling">
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data handling error")  // 409
    private class DataHandlingException extends RuntimeException {

        public DataHandlingException() {
            super();
        }

        public DataHandlingException(String s) {
            super(s);
        }

        public DataHandlingException(String s, Throwable throwable) {
            super(s, throwable);
        }

        public DataHandlingException(Throwable throwable) {
            super(throwable);
        }
    }

    private ModelAndView mavAddNLogErrorText(ModelAndView mav, List<ObjectError> errors) {
        String errorText = "";
        errors.stream().forEach((error) -> {
            errorText.concat(error.toString());
        });
        LOG.error(errorText);
        mav.addObject("errortext", errorText);
        return mav;
    }

//</editor-fold>

    
    @RequestMapping(value = {"/register/providerSignUp"}, method = RequestMethod.GET)
    public ModelAndView registerProviderSignUpGet() {
        ModelAndView mav = new ModelAndView("providerSignUp");
        mav.addObject("provider", new Provider());
        return mav;
    }
    
    @RequestMapping(value = {"/register/providerSignUp"}, method = RequestMethod.POST)
    public ModelAndView registerProviderSignUpPost(
             //@Valid final Provider provider, // poistettu Providerin validointi
            @Valid final Provider provider,
            final BindingResult bindingResult,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("passwordcnf") String passwordcnf,
            @RequestParam("correspondenceFiles") MultipartFile[] correspondenceFiles,
            final ModelMap model) {
        
            //System.out.println(password + ", " +passwordcnf);
            ModelAndView mav = new ModelAndView("providerSignUp");
            User user = userManager.getUserByUsername(username);

            // Handling errors from form
            // 1) User exists
            // 2) Passwords doesn't match
            // 2) Passwords doesn't match
            if(!password.equals(passwordcnf)){
                bindingResult.rejectValue("user.password", "password", "Password and confirmation didn't match.");
            }

            if (bindingResult.hasErrors() || user != null) {
                mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
    //            bindingResult.getAllErrors().stream().forEach((error) -> {
    //                LOG.error(error.toString());
    //            });
    
             // 1) User exists
            if(user != null){
                bindingResult.rejectValue("user.username", "username", "Username already in use.");
            }
            


           
            /*
            //TEST
            // Puts error to form field "name". It is in form provider.name(?)
            bindingResult.rejectValue("name", "name", "An account already exists for this username.");
            bindingResult.rejectValue("address", "address", "Address is too complicated.");
            */
            
           
            
            mav.addObject("provider", model.getOrDefault("provider", new Provider()));
            mav.addObject("error", true);
        } else {
            try {
                LOG.info("Liitetiedostoja: " + correspondenceFiles.length);
                provider.setUser(createUser(username, password));
                provider.setCorrespondences(createCorrespondences(correspondenceFiles));

                // persist the object "provider"
                Provider savedProvider = providerManager.saveProvider(provider);

                mav = new ModelAndView("providerRegistered");
                mav.addObject("provider", savedProvider);

                registrationNotification(savedProvider);

            } catch (Exception ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                
                LOG.error(sw.toString());
//                if (provider.getUser() != null) {
//                    provider.getUser().setPassword(null);
//                }
//                mav.addObject("provider", provider);
//                mav.addObject("error", true);
//                mav.addObject("errortext", ex.getMessage());
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

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
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
//            bindingResult.getAllErrors().stream().forEach((error) -> {
//                LOG.error(error.toString());
//            });
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
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                
                LOG.error(sw.toString());
//                if (provider.getUser() != null) {
//                    provider.getUser().setPassword(null);
//                }
//                mav.addObject("provider", provider);
//                mav.addObject("error", true);
//                mav.addObject("errortext", ex.getMessage());
                throw new DataHandlingException(ex);
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
            // array should not contain any files when no files are uploaded
            // during registration, but some combination of html multipart multiple-file upload
            // causes one empty array element in this list
            if (correspondenceFile.isEmpty()) continue;
            
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
        if (password == null || password.length() < 8) {
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
