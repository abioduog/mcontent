package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.manager.ProviderManager;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
            = Logger.getLogger(ContentController.class);

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

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/provider/remove/{providerId}"})
    public ModelAndView viewRemovableProvider(@PathVariable("providerId") long id) {
        ModelAndView mav = new ModelAndView("providerRemove");
        mav.addObject("provider", providerManager.getProvider(id));
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = {"/provider/remove/{providerId}"}, params = {"remove"})
    public ModelAndView removeDeliveryPipe(
            @PathVariable("providerId") String id,
            final Provider provider,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("providerRemove");

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().forEach((error) -> {
                LOG.error(error.toString());
            });
            mav.addObject("provider", model.getOrDefault("provider", new Provider()));
            mav.addObject("error", true);
        } else {
            try {
                providerManager.removeProvider(provider.getId());
                provider.setCorrespondences(null);
                mav.addObject("provider", provider);
                mav.addObject("removed", true);
            } catch (Exception ex) {
                LOG.error(ex);
                mav.addObject("provider", provider);
                mav.addObject("error", true);
            }
        }

        return mav;
    }

}
