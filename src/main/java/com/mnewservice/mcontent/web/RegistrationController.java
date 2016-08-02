/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.repository.entity.CustomContentEntity;
import com.mnewservice.mcontent.repository.entity.FileEntity;

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

      /* 
    

    @Value("${registration.streetAddress}")
    private String ADDRESS;
    @Value("${registration.providerName}")
    private String PROVIDERNAME;

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
       // model.addAttribute("providerName", "getthisfromfile");
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
            @RequestParam("contentname") String contentname,
            @RequestParam("contentdescription") String contentdescription,
            Model model) {
        System.out.println("Registration/POST");
        System.out.println("Arvot: "
                + providername
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
                + ", " + contentname
                + ", " + contentdescription
        );

                Provider provider = new Provider();
                provider.setAddress(address);
                provider.setState(state);
                provider.setCountry(country);
                provider.setName(providername);
                provider.setNameOfContactPerson(nameOfContactPerson);
                provider.setPositionOfContactPerson(positionOfContactPerson);
                provider.setPhoneOfContactPerson(mobilenumber);
                provider.setEmailOfContactPerson(contactemail);
                provider.setContentName(contentname);
                provider.setContentDescription(contentdescription);
                provider.setFax(faxnumber);
                Provider testiprovider =  providerManager.saveProvider(provider);
                
                System.out.println(testiprovider.getId());
                User user= testiprovider.getUser();
                user.setPassword(PasswordEncrypter.getInstance().encrypt(password));
                user.setUsername(username);
                        user.setRoles(
                Arrays.asList(
                        userManager.getRoleByName(Role.PROVIDER_SHOULD_BE_ENUM)
                )
        );
                userManager.saveUser(user);
/*        
System.out.println("Tallennetaan testiprovider");
Provider provider =  testRegistration();
        System.out.println("Tallennetaan testiprovider tallennetty");

        model.addAttribute("emailaddress", new String(""));
*/
        return "registration";
    }
    
    public Provider testRegistration(){
                String providername = "Test Provider";
                String address= "Test address"; //???
                String state= "Test State";
                String country= "Test Country";
                String companyname= "Test companyname"; //???
                String username= "test_provider";
                String password= "test1234";
                String nameOfContactPerson= "Testcontact";
                String positionOfContactPerson= "Test Position";
                String mobilenumber= "555112233";
                String faxnumber= "Test faxnumber"; //???
                String contactemail= "Test contactemail";
                String contentdescription= "Test contentdesc";
                
                Provider provider = new Provider();
                
                //providers.content_description
                provider.setContentDescription(contentdescription);
                //providers.name
                provider.setName(providername);
                //providers.email_of_contact_person
                provider.setEmail(contactemail);
                //providers.state
                provider.setState(state);
                //providers.country
                provider.setCountry(country);
                
                //providers.name_of_contact_person
                provider.setNameOfContactPerson(nameOfContactPerson);
                //providers.phone_of_contact_person
                provider.setPhoneOfContactPerson(nameOfContactPerson);
                //providers.position_of_contact_person
                provider.setPositionOfContactPerson(positionOfContactPerson);
                
                Provider testiprovider =  providerManager.saveProvider(provider);
                
                System.out.println(testiprovider.getId());
                User user= testiprovider.getUser();
                user.setPassword(PasswordEncrypter.getInstance().encrypt(password));
                user.setUsername(username);
                        user.setRoles(
                Arrays.asList(
                        userManager.getRoleByName(Role.PROVIDER_SHOULD_BE_ENUM)
                )
        );
                userManager.saveUser(user);
                
                return testiprovider;
    }
}
