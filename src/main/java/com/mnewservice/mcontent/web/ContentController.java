package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.*;
import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import com.mnewservice.mcontent.manager.NotificationManager;
import com.mnewservice.mcontent.manager.ProviderManager;
import com.mnewservice.mcontent.manager.UserManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ContentController {

    private static final Logger LOG
            = Logger.getLogger(ContentController.class);

    @Autowired
    private DeliveryPipeManager deliveryPipeManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private ProviderManager providerManager;

    @Autowired
    private NotificationManager notificationManager;

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

    @RequestMapping({"/content/list"})
    public String listServices() {
        return "deliveryPipeList";
    }

//<editor-fold defaultstate="collapsed" desc="Delivery pipe">
    @RequestMapping({"/deliverypipe/create"})
    public ModelAndView viewDeliveryPipe() {
        ModelAndView mav = new ModelAndView("deliveryPipeDetail");
        mav.addObject("deliveryPipe", new DeliveryPipe());
        return mav;
    }

    @RequestMapping({"/deliverypipe/{id}"})
    public ModelAndView viewDeliveryPipe(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("deliveryPipeDetail");
        mav.addObject("deliveryPipe", deliveryPipeManager.getDeliveryPipe(id));
        return mav;
    }

    @RequestMapping(value = {"/deliverypipe/{id}"}, params = {"save"})
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
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Content list">
    @RequestMapping({"/deliverypipe/{id}/content/list"})
    public ModelAndView viewDeliveryPipeContent(@PathVariable("id") long id) {
        ModelAndView mav;
        DeliveryPipe pipe = deliveryPipeManager.getDeliveryPipe(id);
        switch (pipe.getDeliverableType()) {
            case SCHEDULED:
                mav = new ModelAndView("deliveryPipeScheduledContent");
                mav.addObject("contents", //Thyme leaf javascript support is well unknown, so we do some magic here
                        deliveryPipeManager.getDeliveryPipeScheduledContent(id).stream().map(
                                deliverable -> new HashMap<String, Object>() {
                                    {
                                        put("id", deliverable.getId());
                                        put("title", deliverable.getContent().getTitle());
                                        put("date", new SimpleDateFormat("yyyy-MM-dd").format(deliverable.getDeliveryDate()));
                                    }
                                }
                        ).collect(Collectors.toList()));
                break;

            case SERIES:
                mav = new ModelAndView("deliveryPipeSeriesContent");
                mav.addObject("contents", deliveryPipeManager.getDeliveryPipeSeriesContent(id));
                break;

            default:
                throw new UnsupportedOperationException("Support for viewing content for pipe of type " + pipe.getDeliverableType() + " not implemented.");
        }
        mav.addObject(
                "deliveryPipe", pipe);
        return mav;
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Series content">
    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/series/create"})
    public ModelAndView createSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId) {
        ModelAndView mav = new ModelAndView("content");
        SeriesDeliverable newDeliverable = new SeriesDeliverable();
        newDeliverable.setContent(new Content());

        mav.addObject("deliveryPipeId", deliveryPipeId);
        mav.addObject(
                "deliverable",
                deliveryPipeManager.saveSeriesContent(deliveryPipeId, newDeliverable));

        //  TODO: which subject?
        String notificationSubject = "notification";
        //  TODO: which message to send?
        String notificationMessage = "new content has been created: " + "series";

        notificationManager.notifyAdmin(notificationSubject, notificationMessage);
        return mav;
    }

    @RequestMapping({"/deliverypipe/{deliveryPipeId}/series/{contentId}"})
    public ModelAndView viewSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) {
        ModelAndView mav = new ModelAndView("content");
        mav.addObject("deliveryPipeId", deliveryPipeId);
        mav.addObject(
                "deliverable",
                deliveryPipeManager.getSeriesContent(contentId));
        return mav;
    }

    @RequestMapping({"/deliverypipe/{deliveryPipeId}/series/{contentId}/approve"})
    @ResponseStatus(value = HttpStatus.OK)
    public void approveSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) throws Exception {
        SeriesDeliverable content = getSeriesContent(contentId, DeliverableStatus.APPROVED);
        DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);

        content.setStatus(DeliverableStatus.APPROVED);
        deliveryPipeManager.saveSeriesContent(deliveryPipeId, content);

        String notificationSubject = "notification";
        String notificationMessage = "series deliverable was approved";
        notify(deliveryPipe.getProviders(), notificationSubject, notificationMessage);
    }

    private DeliveryPipe getDeliveryPipe(long deliveryPipeId) throws Exception {
        DeliveryPipe deliveryPipe = deliveryPipeManager.getDeliveryPipe(deliveryPipeId);
        if (deliveryPipe == null) {
            throw new IllegalArgumentException(
                    "delivery pipe was not found with id=" + deliveryPipeId);
        }
        return deliveryPipe;
    }

    private SeriesDeliverable getSeriesContent(long contentId,
            DeliverableStatus disallowedStatus) throws Exception {
        SeriesDeliverable content = deliveryPipeManager.getSeriesContent(contentId);
        if (content == null) {
            throw new IllegalArgumentException(
                    "series deliverable was not found with id=" + contentId);
        }
        if (disallowedStatus.equals(content.getStatus())) {
            throw new IllegalStateException(
                    "series deliverable is already on the desired state");
        }
        return content;
    }

    @RequestMapping({"/deliverypipe/{deliveryPipeId}/series/{contentId}/disapprove"})
    @ResponseStatus(value = HttpStatus.OK)
    public void disapproveSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) throws Exception {
        SeriesDeliverable content
                = getSeriesContent(contentId, DeliverableStatus.PENDING_APPROVAL);
        DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);

        content.setStatus(DeliverableStatus.PENDING_APPROVAL);
        deliveryPipeManager.saveSeriesContent(deliveryPipeId, content);

        String notificationSubject = "notification";
        String notificationMessage = "series deliverable was disapproved";
        notify(deliveryPipe.getProviders(), notificationSubject, notificationMessage);
    }

    private void notify(Collection<User> users, String notificationSubject,
            String notificationMessage) {
        Collection<Provider> providers = new ArrayList<>();
        for (User user : users) {
            Provider provider = providerManager.findByUserId(user.getId());
            if (provider != null) {
                providers.add(provider);
            }
        }

        notificationManager.notifyProviders(
                providers, notificationSubject, notificationMessage);
    }

    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/series/{contentId}"}, params = {"save"})
    public ModelAndView saveSeriesContent(@PathVariable("deliveryPipeId") long deliveryPipeId, @PathVariable("contentId") long contentId,
            final SeriesDeliverable deliverable,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("content");

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().forEach((error) -> {
                LOG.error(error.toString());
            });
            mav.addObject("deliverable", model.getOrDefault("deliverable", new SeriesDeliverable()));
            mav.addObject("error", true);
        } else {
            try {
                // persist the object "scheduledDeliverable"
                SeriesDeliverable savedContent = deliveryPipeManager.saveSeriesContent(deliveryPipeId, deliverable);
                mav.addObject("deliveryPipeId", deliveryPipeId);
                mav.addObject("deliverable", savedContent);
                mav.addObject("saved", true);
            } catch (Exception ex) {
                LOG.error(ex);
                mav.addObject("deliverable", deliverable);
                mav.addObject("error", true);
            }
        }

        return mav;
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Scheduled content">
    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/scheduled/create"}, params = {"date"})
    public ModelAndView createScheduledContent(@PathVariable("deliveryPipeId") long deliveryPipeId, @RequestParam(value = "date") String date) {
        ModelAndView mav = new ModelAndView("content");
        ScheduledDeliverable newDeliverable = new ScheduledDeliverable();
        newDeliverable.setContent(new Content());
        try {
            newDeliverable.setDeliveryDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException ex) {
            LOG.error("Failed to parse date " + date, ex);
        }
        mav.addObject("deliveryPipeId", deliveryPipeId);
        mav.addObject("deliverable", deliveryPipeManager.saveScheduledContent(deliveryPipeId, newDeliverable));

        //  TODO: which subject?
        String notificationSubject = "notification";
        //  TODO: which message to send?
        String notificationMessage = "new content has been created: " + "scheduled";

        notificationManager.notifyAdmin(notificationSubject, notificationMessage);
        return mav;
    }

    @RequestMapping({"/deliverypipe/{deliveryPipeId}/scheduled/{contentId}"})
    public ModelAndView viewScheduledContent(@PathVariable("deliveryPipeId") long deliveryPipeId, @PathVariable("contentId") long contentId) {
        ModelAndView mav = new ModelAndView("content");
        mav.addObject("deliveryPipeId", deliveryPipeId);
        mav.addObject("deliverable", deliveryPipeManager.getScheduledContent(contentId));
        return mav;
    }

    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/scheduled/{contentId}"}, params = {"save"})
    public ModelAndView saveScheduledContent(@PathVariable("deliveryPipeId") long deliveryPipeId, @PathVariable("contentId") long contentId,
            final ScheduledDeliverable deliverable,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("content");

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().forEach((error) -> {
                LOG.error(error.toString());
            });
            mav.addObject("deliverable", model.getOrDefault("deliverable", new ScheduledDeliverable()));
            mav.addObject("error", true);
        } else {
            try {
                // persist the object "scheduledDeliverable"
                ScheduledDeliverable savedContent = deliveryPipeManager.saveScheduledContent(deliveryPipeId, deliverable);
                mav.addObject("deliveryPipeId", deliveryPipeId);
                mav.addObject("deliverable", savedContent);
                mav.addObject("saved", true);
            } catch (Exception ex) {
                LOG.error(ex);
                mav.addObject("deliverable", deliverable);
                mav.addObject("error", true);
            }
        }

        return mav;
    }

    @RequestMapping({"/deliverypipe/{deliveryPipeId}/scheduled/{contentId}/approve"})
    @ResponseStatus(value = HttpStatus.OK)
    public void approveScheduledContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) throws Exception {
        ScheduledDeliverable content = getScheduledContent(contentId, DeliverableStatus.APPROVED);
        DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);

        content.setStatus(DeliverableStatus.APPROVED);
        deliveryPipeManager.saveScheduledContent(deliveryPipeId, content);

        String notificationSubject = "notification";
        String notificationMessage = "scheduled deliverable was approved";
        notify(deliveryPipe.getProviders(), notificationSubject, notificationMessage);
    }

    @RequestMapping({"/deliverypipe/{deliveryPipeId}/scheduled/{contentId}/disapprove"})
    @ResponseStatus(value = HttpStatus.OK)
    public void disapproveScheduledContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) throws Exception {
        ScheduledDeliverable content = getScheduledContent(contentId, DeliverableStatus.PENDING_APPROVAL);
        DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);

        content.setStatus(DeliverableStatus.PENDING_APPROVAL);
        deliveryPipeManager.saveScheduledContent(deliveryPipeId, content);

        String notificationSubject = "notification";
        String notificationMessage = "scheduled deliverable was disapproved";
        notify(deliveryPipe.getProviders(), notificationSubject, notificationMessage);
    }

    private ScheduledDeliverable getScheduledContent(long contentId,
            DeliverableStatus disallowedStatus) {
        ScheduledDeliverable content = deliveryPipeManager.getScheduledContent(contentId);
        if (content == null) {
            throw new IllegalArgumentException(
                    "scheduled deliverable was not found with id=" + contentId);
        }
        if (disallowedStatus.equals(content.getStatus())) {
            throw new IllegalStateException(
                    "scheduled deliverable is already on the desired state");
        }
        return content;
    }

//</editor-fold>
    @RequestMapping({"/show/a/{short_uuid}"})
    public ModelAndView showContent(@PathVariable("short_uuid") String shortUuid) {
        ModelAndView mav = new ModelAndView("show");
        mav.addObject("content", deliveryPipeManager.getContentByUuid(shortUuid));
        return mav;
    }
}
