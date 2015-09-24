package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.domain.Role;
import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.manager.ProviderManager;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Controller
public class ProviderController {

    private static final Logger LOG
            = Logger.getLogger(ProviderController.class);

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

}
