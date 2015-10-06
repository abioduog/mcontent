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
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private ProviderManager providerManager;

    @PreAuthorize("hasAuthority('ADMIN')")
    @ModelAttribute("allProviders")
    public List<Provider> populateProviders() {
        return providerManager.getAllProviders()
                .stream().collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/provider/list"})
    public String listProviders() {
        return "providerList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/provider/{id}"})
    public ModelAndView viewService(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("providerDetail");
        mav.addObject("provider", providerManager.getProvider(id));
        return mav;
    }
}
