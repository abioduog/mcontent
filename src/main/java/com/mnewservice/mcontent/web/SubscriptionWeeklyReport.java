package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.domain.SubscriptionPeriod;
import com.mnewservice.mcontent.domain.SubscriptionWeeklyReportDTO;
import com.mnewservice.mcontent.domain.SubscriptionWeeklyReportList;
import com.mnewservice.mcontent.manager.SubscriptionManager;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SubscriptionWeeklyReport {

    private static final Logger LOG
            = Logger.getLogger(SubscriptionWeeklyReport.class);

    private int LIST_PAGE_SIZE = 25; // How many rows in page
    private int PAGINATION_MENU_SIZE = 5; // How many numbers is visible in pagination menu

    @Autowired
    private SubscriptionManager subscriptionManager;


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
    @ModelAttribute("allSubscriptionWeeklyReport")
    public List<SubscriptionWeeklyReportDTO> populateSubscriptionWeeklyReport(Long serviceId) {
        SubscriptionWeeklyReportList report = new SubscriptionWeeklyReportList();
        Collection<Subscription> subscriptions = subscriptionManager.getAllSubscriptionsByService(serviceId);
        Calendar minCal = Calendar.getInstance();
        minCal.add(Calendar.MONTH, -12);
        Calendar maxCal = Calendar.getInstance();
        maxCal.add(Calendar.MONTH, 12);

        for (Subscription s : subscriptions) {
            Collection<SubscriptionPeriod> periods = s.getPeriods();
            if (periods != null) {
                for (SubscriptionPeriod p : periods) {
                    Calendar cal = Calendar.getInstance();
                    if (p.getStart() != null && p.getStart().after(minCal.getTime()) && p.getStart().before(maxCal.getTime())) {
                        cal.setTime(p.getStart());
                        report.add(cal.get(Calendar.YEAR), cal.get(Calendar.WEEK_OF_YEAR), 1L, 0L);
                    }
                    if (p.getEnd() != null && p.getEnd().after(minCal.getTime()) && p.getEnd().before(maxCal.getTime())) {
                        cal.setTime(p.getEnd());
                        report.add(cal.get(Calendar.YEAR), cal.get(Calendar.WEEK_OF_YEAR), 0L, 1L);
                    }
                }
            }
        }

        return report.getList();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping({"/service/{serviceId}/subscription/report/weekly"})
    public String listSubscriptionWeeklyReportPaged(HttpServletRequest request, @PathVariable("serviceId") Long serviceId) {
        request.getSession().setAttribute("allSubscriptionWeeklyReportPaged", null);
        return "redirect:/service/" + serviceId + "/subscription/report/weekly/page/1";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping(value = {"/service/{serviceId}/subscription/report/weekly/page/{pagenumber}"})
    public ModelAndView viewSubscriptionWeeklyReportPageNumberX(HttpServletRequest request, @PathVariable("serviceId") Long serviceId, @PathVariable("pagenumber") Integer pagenumber) {
        String baseUrl = "service/" + serviceId + "/subscription/report/weekly/page";
        ModelAndView mav = new ModelAndView("subscriptionWeeklyReportPaged");

        PagedListHolder<?> pagedListHolder = (PagedListHolder<?>) request.getSession().getAttribute("allSubscriptionWeeklyReportPaged");
        if (pagedListHolder == null) {
            pagedListHolder = new PagedListHolder(populateSubscriptionWeeklyReport(serviceId));

        } else {
            final int goToPage = pagenumber - 1;
            if (goToPage <= pagedListHolder.getPageCount() && goToPage >= 0) {
                pagedListHolder.setPage(goToPage);
            }
        }

        pagedListHolder.setPageSize(LIST_PAGE_SIZE);
        pagedListHolder.getPageList();
        request.getSession().setAttribute("allSubscriptionWeeklyReportPaged", pagedListHolder);
        int current = pagedListHolder.getPage() + 1;
        int begin = Math.max(1, current - (PAGINATION_MENU_SIZE / 2));
        int end = Math.min(begin + (PAGINATION_MENU_SIZE - 1), pagedListHolder.getPageCount());
        int totalPageCount = pagedListHolder.getPageCount();

        mav.addObject("allSubscriptionWeeklyReportPaged", pagedListHolder.getPageList());

        mav.addObject("beginIndex", begin);
        mav.addObject("endIndex", end);
        mav.addObject("currentIndex", current);
        mav.addObject("totalPageCount", totalPageCount);
        mav.addObject("baseUrl", baseUrl);
        mav.addObject("pagedListHolder", pagedListHolder);

        return mav;
    }


}
