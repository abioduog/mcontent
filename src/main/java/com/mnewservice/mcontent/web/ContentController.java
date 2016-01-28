package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.*;
import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import com.mnewservice.mcontent.manager.FileManager;
import com.mnewservice.mcontent.manager.NotificationManager;
import com.mnewservice.mcontent.manager.ProviderManager;
import com.mnewservice.mcontent.manager.ScheduledDeliverableManager;
import com.mnewservice.mcontent.manager.SeriesDeliverableManager;
import com.mnewservice.mcontent.manager.UserManager;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    private ScheduledDeliverableManager scheduledManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private FileManager fileManager;

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
                        scheduledManager.getDeliveryPipeScheduledContent(id).stream().map(
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
                mav.addObject("contents", seriesManager.getDeliveryPipeSeriesContent(id));
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
                seriesManager.getSeriesContent(contentId));
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
        seriesManager.saveSeriesContent(deliveryPipeId, content);

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
        SeriesDeliverable content = seriesManager.getSeriesContent(contentId);
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
        seriesManager.saveSeriesContent(deliveryPipeId, content);

        contentDisapproveNotification(content, deliveryPipe);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/series"}, params = {"save"})
    public ModelAndView saveSeriesContent(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            final SeriesDeliverable deliverable,
            final BindingResult bindingResult,
            final ModelMap model) {

        LOG.info("saveSeriesContent - STARTED");
        ModelAndView mav = new ModelAndView("content");

        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
            mav.addObject("deliverable", model.getOrDefault("deliverable", new SeriesDeliverable()));
            mav.addObject("error", true);
        } else {
            try {
                DeliveryPipe deliveryPipe = getDeliveryPipe(deliveryPipeId);
                SeriesDeliverable savedContent = seriesManager.saveSeriesContent(deliveryPipeId, deliverable);

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
        SeriesDeliverable deliverable = seriesManager.getSeriesContent(contentId);
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
                seriesManager.removeSeriesContent(deliverable.getId());
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
        mav.addObject("deliverable", scheduledManager.getScheduledContent(contentId));
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
                ScheduledDeliverable savedContent = scheduledManager.saveScheduledContent(deliveryPipeId, deliverable);
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
        scheduledManager.saveScheduledContent(deliveryPipeId, content);

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
        scheduledManager.saveScheduledContent(deliveryPipeId, content);

        contentDisapproveNotification(content, deliveryPipe);
    }

    private ScheduledDeliverable getScheduledContent(long contentId,
            DeliverableStatus disallowedStatus) {
        ScheduledDeliverable content = scheduledManager.getScheduledContent(contentId);
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
        ScheduledDeliverable deliverable = scheduledManager.getScheduledContent(contentId);
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
                scheduledManager.removeScheduledContent(deliverable.getId());
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

//<editor-fold defaultstate="collapsed" desc="MyFile/MyResponse">
    protected class MyFile {

        private String name;
        private String originalFilename;
        private String contentType;
        private long size;
        private boolean accepted;
        private String errorMessage;

        public MyFile(String name, String originalFilename, String contentType, long size, boolean accepted, String errorMessage) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.size = size;
            this.accepted = accepted;
            this.errorMessage = errorMessage;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }

        public void setOriginalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public boolean isAccepted() {
            return accepted;
        }

        public void setAccepted(boolean accepted) {
            this.accepted = accepted;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

    }

    protected class MyResponse {

        private String message;
        private String error;
        private List<MyFile> files;

        public MyResponse() {
            this.files = new ArrayList<>();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<MyFile> getFiles() {
            return files;
        }

        public void setFiles(List<MyFile> files) {
            this.files = files;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

    }
//</editor-fold>
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping(value = {"/deliverypipe/{deliveryPipeId}/series/{contentId}/fileupload"}/*, method = RequestMethod.POST*/)
    public @ResponseBody
    ResponseEntity<MyResponse> uploadSeriesFile(
            @PathVariable("deliveryPipeId") long deliveryPipeId,
            @PathVariable("contentId") long contentId,
            MultipartHttpServletRequest request
    ) {
        LOG.info("file upload - " + request.getContextPath() + "/deliverypipe/" + deliveryPipeId + "/series/" + contentId + "/fileupload");
        MyResponse response = new MyResponse();

        try {
            Iterator<String> itr = request.getFileNames();

            while (itr.hasNext()) {
                String uploadedFile = itr.next();
                MultipartFile file = request.getFile(uploadedFile);
                LOG.info("Saving downloaded file: " + file.getContentType() + "(" + file.getOriginalFilename() + ")");

                ContentFile contentFile = new ContentFile();
                contentFile.setMimeType(file.getContentType());
                contentFile.setOriginalFilename(file.getOriginalFilename());

                // SMB save
                contentFile = fileManager.saveFile(contentFile, file.getBytes());

                // Connect with Deliverable
                seriesManager.addFile(contentId, contentFile);

                MyFile resultFile = new MyFile(file.getName(), file.getOriginalFilename(), file.getContentType(), file.getSize(), contentFile.isAccepted(), contentFile.getErrorMessage());
                response.getFiles().add(resultFile);
            }
        } catch (Exception e) {
            response.setMessage("Error on handling request");
            response.setError(response.getMessage());
            return new ResponseEntity<MyResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response.getFiles().stream().anyMatch(x -> {
            if (x.isAccepted()) {
                return false;
            }
            response.setMessage("Request completed with errors");
            response.setError(x.getErrorMessage());
            return true;
        })) {
            return new ResponseEntity<MyResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.setMessage("Request completed");
        return new ResponseEntity<MyResponse>(response, HttpStatus.OK);
    }

}
