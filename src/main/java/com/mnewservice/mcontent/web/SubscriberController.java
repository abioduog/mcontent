package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.manager.SubscriberManager;
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

@Controller
public class SubscriberController {

    private static final Logger LOG
            = Logger.getLogger(ServiceController.class);

    @Autowired
    private SubscriberManager subscriberManager;

    @PreAuthorize("hasAuthority('ADMIN')")
    @ModelAttribute("allSubscribers")
    public List<Subscriber> populateSubscribers() {
        return subscriberManager.getAllSubscribers()
                .stream().collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/subscriber/list"})
    public String listSubscribers() {
        return "subscriberList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/subscriber/remove/{id}"})
    public ModelAndView viewRemovableSubscriber(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("subscriberRemove");
        mav.addObject("subscriber", subscriberManager.getSubscriber(id));
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = {"/subscriber/remove/{id}"}, params = {"remove"})
    public ModelAndView removeSubscriber(
            @PathVariable("id") String id,
            final Subscriber subscriber,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("subscriberRemove");

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().forEach((error) -> {
                LOG.error(error.toString());
            });
            mav.addObject("subscriber", model.getOrDefault("subscriber", new Subscriber()));
            mav.addObject("error", true);
        } else {
            try {
                subscriberManager.removeSubscriber(subscriber.getId());
                mav.addObject("subscriber", subscriber);
                mav.addObject("removed", true);
            } catch (Exception ex) {
                LOG.error(ex);
                mav.addObject("subscriber", subscriber);
                mav.addObject("error", true);
                mav.addObject("errortext", ex.getLocalizedMessage());
            }
        }

        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/subscriber/{id}"})
    public ModelAndView viewSubscriber(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("subscriberDetail");
        mav.addObject("subscriber", subscriberManager.getSubscriber(id));
        return mav;
    }

}
