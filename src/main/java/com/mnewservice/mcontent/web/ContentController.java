package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.*;
import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import com.mnewservice.mcontent.manager.NotificationManager;
import com.mnewservice.mcontent.manager.ProviderManager;
import com.mnewservice.mcontent.manager.SeriesDeliverableManager;
import com.mnewservice.mcontent.manager.UserManager;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ContentController {

    private static final Logger LOG
            = Logger.getLogger(ContentController.class);

    @Value("${application.notification.deliverable.created.message.subject}")
    private String deliverableCreatedMessageSubject;

    @Value("${application.notification.deliverable.created.message.text}")
    private String deliverableCreatedMessageText;

    @Value("${application.notification.deliverable.approved.message.subject}")
    private String deliverableApprovedMessageSubject;

    @Value("${application.notification.deliverable.approved.message.text}")
    private String deliverableApprovedMessageText;

    @Value("${application.notification.deliverable.disapproved.message.subject}")
    private String deliverableDisapprovedMessageSubject;

    @Value("${application.notification.deliverable.disapproved.message.text}")
    private String deliverableDisapprovedMessageText;

    @Value("${application.content.themes}")
    private String themes;

    @Autowired
    private DeliveryPipeManager deliveryPipeManager;

    @Autowired
    private SeriesDeliverableManager seriesManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private ProviderManager providerManager;

    @Autowired
    private NotificationManager notificationManager;

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @ModelAttribute("allDeliverableTypes")
    public List<DeliverableType> populateDeliverableTypes() {
        return Arrays.asList(DeliverableType.values());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @ModelAttribute("allThemes")
    public List<String> populateThemes() {
        return Arrays.asList(themes.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @ModelAttribute("allDeliveryPipes")
    public List<DeliveryPipe> populateServices() {
       return deliveryPipeManager.getAllDeliveryPipes()
               .stream().collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @ModelAttribute("allProviders")
    public List<User> populateProviders() {
        return userManager.getAllUsersByRoleName(Role.PROVIDER_SHOULD_BE_ENUM)
                .stream().collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping({"/content/list"})
    public String listServices() {
        return "deliveryPipeList";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping({"/deliverypipe/list/filtered/"})
    public ModelAndView listFilteredServices(@RequestParam(value = "nameFilter") String fname) {
        ModelAndView mav = new ModelAndView("deliveryPipeList");
        mav.addObject("filteredDeliveryPipes", deliveryPipeManager.getDeliveryPipes(fname)
                .stream().collect(Collectors.toList()));
        mav.addObject("nameFilter", fname);
        return mav;
    }

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
//<editor-fold defaultstate="collapsed" desc="Delivery pipe">
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/deliverypipe/create"})
    public ModelAndView viewDeliveryPipe() {
        ModelAndView mav = new ModelAndView("deliveryPipeDetail");
        mav.addObject("deliveryPipe", new DeliveryPipe());
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/deliverypipe/{pipeId}"})
    public ModelAndView viewDeliveryPipe(@PathVariable("pipeId") long id) {
        ModelAndView mav = new ModelAndView("deliveryPipeDetail");
        mav.addObject("deliveryPipe", deliveryPipeManager.getDeliveryPipe(id));
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = {"/deliverypipe/{pipeId}"}, params = {"save"})
    public ModelAndView saveDeliveryPipe(
            @PathVariable("pipeId") String id,
            final DeliveryPipe deliveryPipe,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("deliveryPipeDetail");

        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
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
                //mav.addObject("service", deliveryPipe);
                //mav.addObject("error", true);
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/deliverypipe/remove/{pipeId}"})
    public ModelAndView viewRemovableDeliveryPipe(@PathVariable("pipeId") long id) {
        ModelAndView mav = new ModelAndView("deliveryPipeRemove");
        mav.addObject("deliveryPipe", deliveryPipeManager.getDeliveryPipe(id));
        if (deliveryPipeManager.hasContent(id)) {
            mav.addObject("hasContent", "true");
        }
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = {"/deliverypipe/remove/{pipeId}"}, params = {"remove"})
    public ModelAndView removeDeliveryPipe(
            @PathVariable("pipeId") String id,
            final DeliveryPipe deliveryPipe,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("deliveryPipeRemove");

        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
            mav.addObject("deliveryPipe", model.getOrDefault("deliveryPipe", new DeliveryPipe()));
            mav.addObject("error", true);
        }  else {
            try {
                deliveryPipeManager.removeDeliveryPipe(deliveryPipe.getId());
                deliveryPipe.setProviders(null);
                mav.addObject("deliveryPipe", deliveryPipe);
                mav.addObject("removed", true);
            } catch (Exception ex) {
                LOG.error(ex);
                // mav.addObject("deliveryPipe", deliveryPipe);
                // mav.addObject("error", true);
                // mav.addObject("errortext", ex.getLocalizedMessage());
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Content list">
    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping({"/deliverypipe/{pipeId}/content/list"})
    public ModelAndView viewDeliveryPipeContent(@PathVariable("pipeId") long id) {
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
                                        put("status", deliverable.getStatus());
                                        put("date", new SimpleDateFormat("yyyy-MM-dd").format(deliverable.getDeliveryDate()));
                                        put("myStatus", deliverable.getStatus().toString().toLowerCase());
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
    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/series/create"})
    public ModelAndView createSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId) {
        ModelAndView mav = new ModelAndView("content");
        SeriesDeliverable newDeliverable = new SeriesDeliverable();
        newDeliverable.setContent(new Content());
        newDeliverable.setStatus(DeliverableStatus.PENDING_APPROVAL);
        newDeliverable.setDeliveryDaysAfterSubscription(
                seriesManager.getNextDeliveryDay(deliveryPipeId));

        mav.addObject("deliveryPipeId", deliveryPipeId);
        mav.addObject(
                "deliverable",
                newDeliverable
        );
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping({"/deliverypipe/{deliveryPipeId}/series/{contentId}"})
    public ModelAndView viewSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) {
        ModelAndView mav = new ModelAndView("content");
        mav.addObject("deliveryPipeId", deliveryPipeId);
        DeliveryPipe deliveryPipe = deliveryPipeManager.getDeliveryPipe(deliveryPipeId);
        mav.addObject("theme", deliveryPipe.getTheme());
        mav.addObject(
                "deliverable",
                deliveryPipeManager.getSeriesContent(contentId));
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/deliverypipe/{deliveryPipeId}/series/{contentId}/approve"})
    @ResponseStatus(value = HttpStatus.OK)
    public void approveSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) throws Exception {
        SeriesDeliverable content = getSeriesContent(contentId, DeliverableStatus.APPROVED);
        DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);

        content.setStatus(DeliverableStatus.APPROVED);
        deliveryPipeManager.saveSeriesContent(deliveryPipeId, content);

        contentApproveNotification(content, deliveryPipe);
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/deliverypipe/{deliveryPipeId}/series/{contentId}/disapprove"})
    @ResponseStatus(value = HttpStatus.OK)
    public void disapproveSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) throws Exception {
        SeriesDeliverable content
                = getSeriesContent(contentId, DeliverableStatus.PENDING_APPROVAL);
        DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);

        content.setStatus(DeliverableStatus.DISAPPROVED);
        deliveryPipeManager.saveSeriesContent(deliveryPipeId, content);

        contentDisapproveNotification(content, deliveryPipe);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/series"}, params = {"save"})
    public ModelAndView saveSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            final SeriesDeliverable deliverable,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("content");

        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
            mav.addObject("deliverable", model.getOrDefault("deliverable", new SeriesDeliverable()));
            mav.addObject("error", true);
        } else {
            try {
                DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);
                SeriesDeliverable savedContent = deliveryPipeManager.saveSeriesContent(deliveryPipeId, deliverable);

                mav.addObject("deliveryPipeId", deliveryPipeId);
                mav.addObject("deliverable", savedContent);
                mav.addObject("saved", true);
                if (deliverable.getId() == null) {
                    contentCreationNotification(savedContent, deliveryPipe);
                }

            } catch (Exception ex) {
                LOG.error(ex);
//                mav.addObject("deliverable", deliverable);
//                mav.addObject("error", true);
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping({"/deliverypipe/{deliveryPipeId}/series/remove/{contentId}"})
    public ModelAndView viewRemovableSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) {
        SeriesDeliverable deliverable = deliveryPipeManager.getSeriesContent(contentId);
        ModelAndView mav = new ModelAndView("contentSeriesRemove");
        mav.addObject("deliverable", deliverable);
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/series/remove/{contentId}"}, params = {"remove"})
    public ModelAndView removeSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId,
            final SeriesDeliverable deliverable,
            final BindingResult bindingResult,
            final ModelMap model) {
        //SeriesDeliverable deliverable = deliveryPipeManager.getSeriesContent(contentId);
        ModelAndView mav = new ModelAndView("contentSeriesRemove");
        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
            mav.addObject("deliverable", model.getOrDefault("deliverable", new SeriesDeliverable()));
            mav.addObject("error", true);
        } else {
            try {
                deliveryPipeManager.removeSeriesContent(deliverable.getId());
                mav.addObject("deliverable", deliverable);
                mav.addObject("removed", true);
            } catch (Exception ex) {
                LOG.error(ex);
//                mav.addObject("deliverable", deliverable);
//                mav.addObject("error", true);
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Scheduled content">
    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
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
        mav.addObject("deliverable", newDeliverable);
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping({"/deliverypipe/{deliveryPipeId}/scheduled/{contentId}"})
    public ModelAndView viewScheduledContent(@PathVariable("deliveryPipeId") long deliveryPipeId, @PathVariable("contentId") long contentId) {
        ModelAndView mav = new ModelAndView("content");
        mav.addObject("deliveryPipeId", deliveryPipeId);
        mav.addObject("deliverable", deliveryPipeManager.getScheduledContent(contentId));
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/scheduled"}, params = {"save"})
    public ModelAndView saveScheduledContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            final ScheduledDeliverable deliverable,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("content");

        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
            mav.addObject(
                    "deliverable",
                    model.getOrDefault("deliverable", new ScheduledDeliverable())
            );
            mav.addObject("error", true);
        } else {
            try {
                DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);
                ScheduledDeliverable savedContent = deliveryPipeManager.saveScheduledContent(deliveryPipeId, deliverable);
                mav.addObject("deliveryPipeId", deliveryPipeId);
                mav.addObject("deliverable", savedContent);
                mav.addObject("saved", true);

                if (deliverable.getId() == null) {
                    contentCreationNotification(savedContent, deliveryPipe);
                }

            } catch (Exception ex) {
                LOG.error(ex);
//                mav.addObject("deliverable", deliverable);
//                mav.addObject("error", true);
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/deliverypipe/{deliveryPipeId}/scheduled/{contentId}/approve"})
    @ResponseStatus(value = HttpStatus.OK)
    public void approveScheduledContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) throws Exception {
        ScheduledDeliverable content = getScheduledContent(contentId, DeliverableStatus.APPROVED);
        DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);

        content.setStatus(DeliverableStatus.APPROVED);
        deliveryPipeManager.saveScheduledContent(deliveryPipeId, content);

        contentApproveNotification(content, deliveryPipe);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/deliverypipe/{deliveryPipeId}/scheduled/{contentId}/disapprove"})
    @ResponseStatus(value = HttpStatus.OK)
    public void disapproveScheduledContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) throws Exception {
        ScheduledDeliverable content = getScheduledContent(contentId, DeliverableStatus.PENDING_APPROVAL);
        DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);

        content.setStatus(DeliverableStatus.DISAPPROVED);
        deliveryPipeManager.saveScheduledContent(deliveryPipeId, content);

        contentDisapproveNotification(content, deliveryPipe);
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

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping({"/deliverypipe/{deliveryPipeId}/scheduled/remove/{contentId}"})
    public ModelAndView viewRemovableScheduledContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId) {
        ScheduledDeliverable deliverable = deliveryPipeManager.getScheduledContent(contentId);
        ModelAndView mav = new ModelAndView("contentScheduledRemove");
        mav.addObject("deliverable", deliverable);
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/scheduled/remove/{contentId}"}, params = {"remove"})
    public ModelAndView removeScheduledContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId,
            final SeriesDeliverable deliverable,
            final BindingResult bindingResult,
            final ModelMap model) {
        //SeriesDeliverable deliverable = deliveryPipeManager.getSeriesContent(contentId);
        ModelAndView mav = new ModelAndView("contentScheduledRemove");
        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
            mav.addObject("deliverable", model.getOrDefault("deliverable", new ScheduledDeliverable()));
            mav.addObject("error", true);
        } else {
            try {
                deliveryPipeManager.removeScheduledContent(deliverable.getId());
                mav.addObject("deliverable", deliverable);
                mav.addObject("removed", true);
            } catch (Exception ex) {
                LOG.error(ex);
//                mav.addObject("deliverable", deliverable);
//                mav.addObject("error", true);
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

    //</editor-fold>
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping({"deliverypipe/{deliveryPipeId}/series/{contentId}/fileupload"})
    public ModelAndView uploadScheduledContentFiles(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId
    ) {
        ModelAndView mav = new ModelAndView("content");
        mav.addObject("deliveryPipeId", deliveryPipeId);
        mav.addObject("deliverable", deliveryPipeManager.getSeriesContent(contentId));
        return mav;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping({"deliverypipe/{deliveryPipeId}/scheduled/{contentId}/fileupload"})
    public ModelAndView uploadSeriesContentFiles(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId
    ) {
        ModelAndView mav = new ModelAndView("content");
        mav.addObject("deliveryPipeId", deliveryPipeId);
        mav.addObject("deliverable", deliveryPipeManager.getScheduledContent(contentId));
        return mav;
    }

    //<editor-fold defaultstate="collapsed" desc="notifications">
    private void contentCreationNotification(AbstractDeliverable deliverable,
            DeliveryPipe deliveryPipe) {
        String notificationSubject = String.format(
                deliverableCreatedMessageSubject,
                deliverable.getContent().getTitle());
        String notificationMessage = String.format(
                deliverableCreatedMessageText,
                deliverable.getContent().getTitle(),
                deliveryPipe.getName());

        notificationManager.notifyAdmin(notificationSubject, notificationMessage);
    }

    private void contentApproveNotification(
            AbstractDeliverable deliverable, DeliveryPipe deliveryPipe) {
        String notificationSubject = String.format(
                deliverableApprovedMessageSubject,
                deliverable.getContent().getTitle());
        String notificationMessage = String.format(
                deliverableApprovedMessageText,
                deliverable.getContent().getTitle(),
                deliveryPipe.getName());

        notify(deliveryPipe.getProviders(), notificationSubject, notificationMessage);
    }

    private void contentDisapproveNotification(
            AbstractDeliverable deliverable, DeliveryPipe deliveryPipe) {
        String notificationSubject = String.format(
                deliverableDisapprovedMessageSubject,
                deliverable.getContent().getTitle());
        String notificationMessage = String.format(
                deliverableDisapprovedMessageText,
                deliverable.getContent().getTitle(),
                deliveryPipe.getName());

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
    //</editor-fold>

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/series/{contentId}/fileupload"}, method = RequestMethod.POST)
    public ResponseEntity uploadFile(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId,
            MultipartHttpServletRequest request
    ) {
        try {
            Iterator<String> itr = request.getFileNames();

            while (itr.hasNext()) {
                String uploadedFile = itr.next();
                MultipartFile file = request.getFile(uploadedFile);
                String mimeType = file.getContentType();
                String filename = file.getOriginalFilename();
                byte[] bytes = file.getBytes();

                FileEntity newFile = new FileEntity(filename, bytes, mimeType);

                // fileManager.uploadFile(newFile);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

}
