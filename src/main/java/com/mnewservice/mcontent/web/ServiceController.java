package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.domain.DeliveryTime;
import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import com.mnewservice.mcontent.manager.ServiceManager;
import com.mnewservice.mcontent.manager.SubscriptionManager;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Controller
public class ServiceController {

    private static final Logger LOG
            = Logger.getLogger(ServiceController.class);

    @Autowired
    private ServiceManager serviceManager;

    @Autowired
    private SubscriptionManager subscriptionManager;

    @Autowired
    private DeliveryPipeManager deliveryPipeManager;

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
    @ModelAttribute("allServices")
    public List<Service> populateServices() {
        return serviceManager.getAllServices()
                .stream().collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ModelAttribute("allDeliveryPipes")
    public List<DeliveryPipe> populateDeliveryPipes() {
        return deliveryPipeManager.getAllDeliveryPipes().stream().collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ModelAttribute("allDeliveryTimes")
    public List<DeliveryTime> populateDeliveryTimes() {
        return Arrays.asList(DeliveryTime.values());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/service/list"})
    public String listServices() {
        return "serviceList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/service/create"})
    public ModelAndView viewService() {
        ModelAndView mav = new ModelAndView("serviceDetail");
        mav.addObject("service", new Service());
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/service/{serviceid}"})
    public ModelAndView viewService(@PathVariable("serviceid") long id) {
        ModelAndView mav = new ModelAndView("serviceDetail");
        mav.addObject("service", serviceManager.getService(id));
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = {"/service/{serviceid}"}, params = {"save"})
    public ModelAndView saveService(
            @PathVariable("serviceid") String id,
            final Service service,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("serviceDetail");

        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
//            bindingResult.getAllErrors().stream().forEach((error) -> {
//                LOG.error(error.toString());
//            });
            mav.addObject("service", model.getOrDefault("service", new Service()));
            mav.addObject("error", true);
        } else {
            try {
                // persist the object "service"
                Service savedService = serviceManager.saveService(service);
                mav.addObject("service", savedService);
                mav.addObject("saved", true);
            } catch (Exception ex) {
                LOG.error(ex);
//                mav.addObject("service", service);
//                mav.addObject("error", true);
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/service/remove/{serviceid}"})
    public ModelAndView viewRemovableService(@PathVariable("serviceid") long id) {
        ModelAndView mav = new ModelAndView("serviceRemove");
        Service service = serviceManager.getService(id);
        mav.addObject("service", service);
        if (subscriptionManager.getAllSubscriptionsByService(id).size() > 0) {
            mav.addObject("hasSubscription", "true");
        }
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = {"/service/remove/{serviceid}"}, params = {"remove"})
    public ModelAndView removeService(
            @PathVariable("serviceid") long id,
            final Service service,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("serviceRemove");

        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
            mav.addObject("service", model.getOrDefault("service", new Service()));
            mav.addObject("error", true);
        } else {
            try {
                for (Subscription s : subscriptionManager.getAllSubscriptionsByService(id)) {
                    if (s.getSubscriber() != null) {
                        try {
                            subscriptionManager.unRegisterSubscription(s);
                        } catch (Exception ex) {
                            // Most propably send message failure
                            LOG.error("Unregisteration caused exception : ", ex);
                        }
                    }
                    subscriptionManager.removeSubscription(s);
                };
                serviceManager.removeService(service.getId());
                mav.addObject("service", service);
                mav.addObject("removed", true);
            } catch (Exception ex) {
                LOG.error(ex);
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

}
