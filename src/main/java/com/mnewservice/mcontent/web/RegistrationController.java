package com.mnewservice.mcontent.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mnewservice.mcontent.manager.UserManager;
import com.mnewservice.mcontent.domain.User;

import com.mnewservice.mcontent.manager.ProviderManager;
import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.domain.Role;
import com.mnewservice.mcontent.security.PasswordEncrypter;
import java.util.Arrays;

/**
 *
 * @author Pasi Saukkonen <pasi.saukkonen at nolwenture.com>
 */
@Controller
public class RegistrationController {

    private static final Logger LOG
            = Logger.getLogger(ResetpwController.class);

    @Autowired
    private ProviderManager providerManager;

    @Autowired
    private UserManager userManager;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registrationForm(Model model) {
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registrationFormHandler(
            @RequestParam("providername") String providername,
            @RequestParam("address") String address,
            @RequestParam("state") String state,
            @RequestParam("country") String country,
            @RequestParam("companyname") String companyname,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("nameOfContactPerson") String nameOfContactPerson,
            @RequestParam("positionOfContactPerson") String positionOfContactPerson,
            @RequestParam("mobilenumber") String mobilenumber,
            @RequestParam("contactemail") String contactemail,
            @RequestParam("contentname") String contentname,
            @RequestParam("contentdescription") String contentdescription,
            Model model) {
        // TODO: validate input data
        Provider provider = new Provider();
        provider.setAddress(address);
        provider.setState(state);
        provider.setCountry(country);
        provider.setName(providername);
        provider.setNameOfContactPerson(nameOfContactPerson);
        provider.setPositionOfContactPerson(positionOfContactPerson);
        provider.setPhoneOfContactPerson(mobilenumber);
        provider.setEmail(contactemail);
        provider.setEmailOfContactPerson(contactemail);
        provider.setContentName(contentname);
        provider.setContentDescription(contentdescription);
        Provider testiprovider = providerManager.saveProvider(provider);
        // TODO: what if save fails?

        User user = testiprovider.getUser();
        user.setPassword(PasswordEncrypter.getInstance().encrypt(password));
        user.setUsername(username);
        user.setRoles(
                Arrays.asList(
                        userManager.getRoleByName(Role.PROVIDER_SHOULD_BE_ENUM)
                )
        );
        userManager.saveUser(user);
        // TODO: what if save fails?

        return "registrationSuccess";
    }
}
