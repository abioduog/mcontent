package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.BinaryContent;
import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.domain.Role;
import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.manager.ProviderManager;
import com.mnewservice.mcontent.manager.UserManager;
import com.mnewservice.mcontent.security.PasswordEncrypter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
public class ProviderController {

    private static final String ERROR_FAIL_TO_UPLOAD = "failed to upload %s (reason: %s)";
    private static final String REASON_EMPTY_FILE = "file was empty";
    private static final String REASON_READING_FAILED = "reading file failed";
    private static final Logger LOG
            = Logger.getLogger(ProviderController.class);

    @Autowired
    private UserManager userManager;

    @Autowired
    private ProviderManager providerManager;

    @ModelAttribute("allProviders")
    public List<Provider> populateProviders() {
        return providerManager.getAllProviders()
                .stream().collect(Collectors.toList());
    }

    @RequestMapping({"/provider/list"})
    public String listProviders() {
        return "providerList";
    }

    @RequestMapping({"/provider/{id}"})
    public ModelAndView viewService(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("providerDetail");
        mav.addObject("provider", providerManager.getProvider(id));
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
            } catch (Exception ex) {
                LOG.error(ex);
                mav.addObject("provider", provider);
                mav.addObject("error", true);
            }
        }

        return mav;
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
