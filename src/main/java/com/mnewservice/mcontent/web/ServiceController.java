package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.domain.DeliveryTime;
import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import com.mnewservice.mcontent.manager.ServiceManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ServiceController {

    private static final Logger LOG
            = Logger.getLogger(ServiceController.class);

    @Autowired
    private ServiceManager serviceManager;

    @Autowired
    private DeliveryPipeManager deliveryPipeManager;

    @ModelAttribute("allServices")
    public List<Service> populateServices() {
        return serviceManager.getAllServices()
                .stream().collect(Collectors.toList());
    }

    @ModelAttribute("allDeliveryPipes")
    public List<DeliveryPipe> populateDeliveryPipes() {
        return deliveryPipeManager.getAllDeliveryPipes().stream().collect(Collectors.toList());
    }

    @ModelAttribute("allDeliveryTimes")
    public List<DeliveryTime> populateDeliveryTimes() {
        return Arrays.asList(DeliveryTime.values());
    }

    @RequestMapping({"/service/list"})
    public String listServices() {
        return "serviceList";
    }

    @RequestMapping({"/service/create"})
    public ModelAndView viewService() {
        ModelAndView mav = new ModelAndView("serviceDetail");
        mav.addObject("service", new Service());
        return mav;
    }

    @RequestMapping({"/service/{id}"})
    public ModelAndView viewService(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("serviceDetail");
        mav.addObject("service", serviceManager.getService(id));
        return mav;
    }

    @RequestMapping(value = {"/service/{id}"}, params = {"save"})
    public ModelAndView saveService(
            @PathVariable("id") String id,
            final Service service,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("serviceDetail");

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().forEach((error) -> {
                LOG.error(error.toString());
            });
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
                mav.addObject("service", service);
                mav.addObject("error", true);
            }
        }

        return mav;
    }

}
