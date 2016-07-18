/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnewservice.mcontent.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



/**
 *
 * @author Pasi Saukkonen <pasi.saukkonen at nolwenture.com>
 */
@Controller
public class RegistrationController {

    private static final Logger LOG
            = Logger.getLogger(ResetpwController.class);

   /*
    @Value("${registration.providerName}")
    private String PROVIDERNAME;
     
    @Value("${registration.streetAddress}")
    private String ADDRESS;
    @Value("${registration.state}")
    private String STATE;
    @Value("${registration.selectCountry}")
    private String COUNTRY;
    @Value("${registration.companyName}")
    private String COMPANYNAME;
    @Value("${register.username}")
    private String USERNAME;
    @Value("${general.password}")
    private String PASSWORD;
    @Value("${registration.contactName}")
    private String NAMEOFCONTACTPERSON;
    @Value("${registration.position}")
    private String POSITIONOFCONTACTPERSON;
    @Value("${registration.mobileNumber}")
    private String MOBILENUMBER;
    @Value("${registration.faxNumber}")
    private String FAXNUMBER;
    @Value("${registration.contactEmail}")
    private String CONTACTEMAIL;
    @Value("${registration.contentDescription}")
    private String CONTENTDESCRIPTION;
     */
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registrationForm(Model model) {
        System.out.println("Registration/GET");
        model.addAttribute("providerName", "getthisfromfile");
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
            @RequestParam("faxnumber") String faxnumber,
            @RequestParam("contactemail") String contactemail,
            @RequestParam("contentdescription") String contentdescription,
            Model model) {
        System.out.println("Registration/POST");
        System.out.println("Arvot: " + providername
                + ", " + address
                + ", " + state
                + ", " + country
                + ", " + companyname
                + ", " + username
                + ", " + password
                + ", " + nameOfContactPerson
                + ", " + positionOfContactPerson
                + ", " + mobilenumber
                + ", " + faxnumber
                + ", " + contactemail
                + ", " + contentdescription
        );

        model.addAttribute("emailaddress", new String(""));
        return "registration";
    }
}
