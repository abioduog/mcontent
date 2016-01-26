package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.manager.SubscriberManager;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SubscriberController {

    private static final Logger LOG
            = Logger.getLogger(ServiceController.class);

    @Autowired
    private SubscriberManager subscriberManager;

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
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
//            bindingResult.getAllErrors().stream().forEach((error) -> {
//                LOG.error(error.toString());
//            });
            mav.addObject("subscriber", model.getOrDefault("subscriber", new Subscriber()));
            mav.addObject("error", true);
        } else {
            try {
                subscriberManager.removeSubscriber(subscriber.getId());
                mav.addObject("subscriber", subscriber);
                mav.addObject("removed", true);
            } catch (Exception ex) {
                LOG.error(ex);
//                mav.addObject("subscriber", subscriber);
//                mav.addObject("error", true);
//                mav.addObject("errortext", ex.getLocalizedMessage());
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

//<editor-fold defaultstate="collapsed" desc="Subscriptions">
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = {"/subscriber/{id}"})
    public ModelAndView viewAllSubscriptions(
            @PathVariable("id") long id,
            @RequestParam(value = "showAll", required = false) boolean showAll
    ) {
        LOG.error("showAll: [" + showAll + "]");
        ModelAndView mav = new ModelAndView("subscriberDetail");
        mav.addObject("subscriber", subscriberManager.getSubscriberWithSubscriptions(id, showAll));
        mav.addObject("showAll", showAll);
        return mav;
    }
    
//    @PreAuthorize("hasAuthority('ADMIN')")
//    @RequestMapping({"/subscriber/{id}"})
//    public ModelAndView viewSubscriptions(
//            @PathVariable("id") long id
//    ) {
//        ModelAndView mav = new ModelAndView("subscriberDetail");
//        mav.addObject("subscriber", subscriberManager.getSubscriberWithSubscriptions(id));
//        mav.addObject("showAll", false);
//        return mav;
//    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/subscriber/{id}/removesubscriptions"})
    public ModelAndView viewRemovableSubscription(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("subscriptionsRemove");
        mav.addObject("subscriber", subscriberManager.getSubscriberWithSubscriptions(id, true));
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = {"/subscriber/{id}/removesubscriptions"}, params = {"remove"})
    public ModelAndView removeDeliveryPipe(
            @PathVariable("subId") String id,
            final Subscriber subscriber,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("subscriptionsRemove");

        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
            mav.addObject("subscriber", model.getOrDefault("subscriber", new Subscriber()));
            mav.addObject("error", true);
        } else {
            try {
                subscriberManager.removeSubscriptions(subscriber.getId());
                subscriber.setSubscriptions(null);
                mav.addObject("subscriber", subscriber);
                mav.addObject("removed", true);
            } catch (Exception ex) {
                LOG.error(ex);
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

//</editor-fold>
}
