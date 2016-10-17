package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.DashboardServiceInfo;
import com.mnewservice.mcontent.manager.ProviderManager;
import com.mnewservice.mcontent.manager.ServiceManager;
import com.mnewservice.mcontent.manager.SubscriberManager;
import com.mnewservice.mcontent.manager.SubscriptionManager;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

    private static final Logger LOG
            = Logger.getLogger(DashboardController.class);

    private int LIST_PAGE_SIZE = 20; // How many rows in page
    private int PAGINATION_MENU_SIZE = 5; // How many numbers is visible in pagination menu

    @Autowired
    private ProviderManager providerManager;

    @Autowired
    private ServiceManager serviceManager;

    @Autowired
    private SubscriptionManager subscriptionManager;

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
    @ModelAttribute("allDashboardServices")
    public List<DashboardServiceInfo> populateDashboardServices() {
        // return new ArrayList(serviceManager.getAllServices());
        return serviceManager.getAllServices()
                .stream().map(p -> {
                    return (new DashboardServiceInfo()).init(p, subscriptionManager.getAllSubscriptionsByService(p.getId()).size());
                }).collect(Collectors.toList());
    }

    public List<DashboardServiceInfo> populateFilteredDashboardServices(String nameFilter) {
        // return new ArrayList(serviceManager.getFilteredServices(nameFilter));
        return serviceManager.getFilteredServices(nameFilter)
                .stream().map(p -> {
                    return (new DashboardServiceInfo()).init(p, subscriptionManager.getAllSubscriptionsByService(p.getId()).size());
                }).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping({"/dashboard/service/list"})
    public String listDashboardServicesPaged(HttpServletRequest request) {
        request.getSession().setAttribute("allDashboardServicesPaged", null);
        return "redirect:/dashboard/service/list/page/1";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping(value = {"/dashboard/service/list/page/{pagenumber}"})
    public ModelAndView viewDashboardServicesPageNumberX(HttpServletRequest request, @PathVariable("pagenumber") Integer pagenumber, @RequestParam(value = "nameFilter", required = false) String fname) {
        String baseUrl = "dashboard/service/list/page";
        ModelAndView mav = new ModelAndView("adminDashboardPaged");

        PagedListHolder<?> pagedListHolder = (PagedListHolder<?>) request.getSession().getAttribute("allDashboardServicesPaged");
        if (pagedListHolder == null) {
            pagedListHolder = new PagedListHolder(populateDashboardServices());

        } else {
            final int goToPage = pagenumber - 1;
            if (goToPage <= pagedListHolder.getPageCount() && goToPage >= 0) {
                pagedListHolder.setPage(goToPage);
            }
            if (fname != null) {
                pagedListHolder = new PagedListHolder(populateFilteredDashboardServices(fname));
            }
        }

        pagedListHolder.setPageSize(LIST_PAGE_SIZE);
        pagedListHolder.getPageList();
        request.getSession().setAttribute("allDashboardServicesPaged", pagedListHolder);
        int current = pagedListHolder.getPage() + 1;
        int begin = Math.max(1, current - (PAGINATION_MENU_SIZE / 2));
        int end = Math.min(begin + (PAGINATION_MENU_SIZE - 1), pagedListHolder.getPageCount());
        int totalPageCount = pagedListHolder.getPageCount();

        mav.addObject("allDashboardServicesPaged", pagedListHolder.getPageList());

        // numOfSubscribers
        mav.addObject("numOfSubscribers", subscriberManager.getAllSubscribers().size());
        // runningServices
        mav.addObject("runningServices", serviceManager.getAllServices().size());
        // numOfProviders
        mav.addObject("numOfProviders", providerManager.getAllProviders().size());

        mav.addObject("beginIndex", begin);
        mav.addObject("endIndex", end);
        mav.addObject("currentIndex", current);
        mav.addObject("totalPageCount", totalPageCount);
        mav.addObject("baseUrl", baseUrl);
        mav.addObject("pagedListHolder", pagedListHolder);
        mav.addObject("nameFilter", fname);

        return mav;
    }

}
