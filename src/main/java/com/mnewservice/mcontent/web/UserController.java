package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Role;
import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.manager.UserManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private static final Logger LOG
            = Logger.getLogger(UserController.class);

    @Autowired
    private UserManager userManager;

    @RequestMapping({"/user/{id}/activate"})
    @ResponseStatus(value = HttpStatus.OK)
    public void activateProvider(@PathVariable("id") long id) {
        User user = userManager.getUser(id);
        if (!user.isActive()) {
            user.setActive(true);
            userManager.saveUser(user);
            return;
        } else {
            throw new IllegalStateException("Provider is already active");
        }
    }

    @RequestMapping({"/user/{id}/deactivate"})
    @ResponseStatus(value = HttpStatus.OK)
    public void deactivateProvider(@PathVariable("id") long id) {
        User user = userManager.getUser(id);
        if (user.isActive()) {
            user.setActive(false);
            userManager.saveUser(user);
            return;
        } else {
            throw new IllegalStateException("Provider is already inactive");
        }
    }

    @RequestMapping(value = {"/register/provider"}, method = RequestMethod.GET)
    public ModelAndView registerProvider() {
        ModelAndView mav = new ModelAndView("providerRegister");
        mav.addObject("provider", new User());
        return mav;
    }

    @RequestMapping(value = {"/register/provider"}, method = RequestMethod.POST)
    public ModelAndView registerProvider(
            final User user,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("providerRegister");

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().forEach((error) -> {
                LOG.error(error.toString());
            });
            mav.addObject("provider", model.getOrDefault("provider", new User()));
            mav.addObject("error", true);
        } else {
            try {
                // persist the object "service"
                user.setRoles(Arrays.asList(userManager.getRoleByName(Role.PROVIDER_SHOULD_BE_ENUM)));
                user.setActive(false);
                User savedUser = userManager.saveUser(user);
                return new ModelAndView("providerRegistered");
            } catch (Exception ex) {
                LOG.error(ex);
                mav.addObject("provider", user);
                mav.addObject("error", true);
            }
        }

        return mav;
    }
}
