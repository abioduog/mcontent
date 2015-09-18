package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Content;
import com.mnewservice.mcontent.domain.DeliverableType;
import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.domain.Role;
import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import com.mnewservice.mcontent.manager.UserManager;
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

@Controller
public class ContentController {

    private static final Logger LOG
            = Logger.getLogger(ContentController.class);

    @Autowired
    private DeliveryPipeManager deliveryPipeManager;

    @Autowired
    private UserManager userManager;

    @ModelAttribute("allDeliverableTypes")
    public List<DeliverableType> populateDeliverableTypes() {
        return Arrays.asList(DeliverableType.values());
    }

    @ModelAttribute("allDeliveryPipes")
    public List<DeliveryPipe> populateServices() {
        return deliveryPipeManager.getAllDeliveryPipes()
                .stream().collect(Collectors.toList());
    }

    @ModelAttribute("allProviders")
    public List<User> populateProviders() {
        return userManager.getAllUsersByRoleName(Role.PROVIDER_SHOULD_BE_ENUM)
                .stream().collect(Collectors.toList());
    }

    @ModelAttribute("content")
    public Content populateContent() {
        Content content = new Content();
        content.setTitle("Great content");
        content.setContent("With lots of information.");
        return content;
    }

    @RequestMapping({"/content/list"})
    public String listServices() {
        return "contentList";
    }

    @RequestMapping({"/content/{id}"})
    public ModelAndView viewSeriesContent(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("content");
        mav.addObject("content", deliveryPipeManager.getSeriesContent(id));
        return mav;
    }

    @RequestMapping({"/content/deliverypipe/{id}/content"})
    public ModelAndView viewDeliveryPipeContent(@PathVariable("id") long id) {
        ModelAndView mav;
        DeliveryPipe pipe = deliveryPipeManager.getDeliveryPipe(id);
        switch (pipe.getDeliverableType()) {
            case SCHEDULED:
                mav = new ModelAndView("deliveryPipeDetail");
                break;
            case SERIES:
                mav = new ModelAndView("deliveryPipeSeriesContent");
                mav.addObject("contents", deliveryPipeManager.getDeliveryPipeSeriesContent(id));
                break;
            default:
                throw new UnsupportedOperationException("Support for viewing content for pipe of type " + pipe.getDeliverableType() + " not implemented.");
        }
        mav.addObject("deliveryPipe", pipe);
        return mav;
    }

    @RequestMapping({"/content/deliverypipe/create"})
    public ModelAndView viewDeliveryPipe() {
        ModelAndView mav = new ModelAndView("deliveryPipeDetail");
        mav.addObject("deliveryPipe", new DeliveryPipe());
        return mav;
    }

    @RequestMapping({"/content/deliverypipe/{id}"})
    public ModelAndView viewDeliveryPipe(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("deliveryPipeDetail");
        mav.addObject("deliveryPipe", deliveryPipeManager.getDeliveryPipe(id));
        return mav;
    }

    @RequestMapping(value = {"/content/deliverypipe/{id}"}, params = {"save"})
    public ModelAndView saveDeliveryPipe(
            @PathVariable("id") String id,
            final DeliveryPipe deliveryPipe,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("deliveryPipeDetail");

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().forEach((error) -> {
                LOG.error(error.toString());
            });
            mav.addObject("deliveryPipe", model.getOrDefault("deliveryPipe", new DeliveryPipe()));
            mav.addObject("error", true);
        } else {
            try {
                // persist the object "service"
                DeliveryPipe savedDeliveryPipe = deliveryPipeManager.saveDeliveryPipe(deliveryPipe);
                mav.addObject("service", savedDeliveryPipe);
                mav.addObject("saved", true);
            } catch (Exception ex) {
                LOG.error(ex);
                mav.addObject("service", deliveryPipe);
                mav.addObject("error", true);
            }
        }

        return mav;
    }

    @RequestMapping({"/show"})
    public String showContent() {
        return "show";
    }

//    @RequestMapping({"/show/{short_uuid}"})
//    public String showContent(
//            @PathVariable("short_uuid") String shortUuid) {
//        return "show";
//    }
}
