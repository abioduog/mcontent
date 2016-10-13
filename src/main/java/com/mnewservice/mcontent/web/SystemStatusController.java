package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.manager.ServiceManager;
import com.mnewservice.mcontent.manager.SystemStatusManager;
import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import static org.springframework.http.RequestEntity.method;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Antti Vikman on 7.3.2016.
 */
@Controller
public class SystemStatusController {

    private static final Logger LOG
            = Logger.getLogger(SystemStatusController.class);
    
    private int LIST_PAGE_SIZE = 25; // How many rows in page
    private int PAGINATION_MENU_SIZE = 5; // How many numbers is visible in pagination menu

    @Autowired
    private SystemStatusManager systemStatuseManager;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/status/smsmessages"})
    public String viewSmsMessageLogPagedFF(HttpServletRequest request) {
        request.getSession().setAttribute("smsmessagesList", null);
        return "redirect:/status/smsmessagespaged/page/1";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value={"/status/smsmessagespaged/page/{pagenumber}"})
    public ModelAndView viewSmsMessageLogPageNumberX(HttpServletRequest request, @PathVariable("pagenumber") Integer pagenumber, @RequestParam(value = "nameFilter", required = false) String fname) {
        String baseUrl = "/status/smsmessagespaged/page";

        ModelAndView mav = new ModelAndView("smsMessageLogPaged");
        PagedListHolder<?> pagedListHolder = (PagedListHolder<?>) request.getSession().getAttribute("smsmessagesList"); 
        if(pagedListHolder == null){
            pagedListHolder = new PagedListHolder(systemStatuseManager.getSmsMessages().stream().collect(Collectors.toList()));
        }else{
            final int goToPage = pagenumber - 1;
            if(goToPage <= pagedListHolder.getPageCount() && goToPage >= 0){
                pagedListHolder.setPage(goToPage);
            }
            if(fname != null){
                pagedListHolder = new PagedListHolder(systemStatuseManager.getFilteredSmsMessages(fname));                
            }
        }

        pagedListHolder.setPageSize(LIST_PAGE_SIZE);
        pagedListHolder.getPageList();
        request.getSession().setAttribute("smsmessagesList", pagedListHolder);
        int current = pagedListHolder.getPage() + 1;
        int begin = Math.max(1, current - (PAGINATION_MENU_SIZE / 2));
        int end = Math.min(begin + (PAGINATION_MENU_SIZE - 1), pagedListHolder.getPageCount());
        int totalPageCount = pagedListHolder.getPageCount();
                
        mav.addObject("smsmessagesList", pagedListHolder.getPageList());
        mav.addObject("beginIndex", begin);
        mav.addObject("endIndex", end);
        mav.addObject("currentIndex", current);
        mav.addObject("totalPageCount", totalPageCount);
        mav.addObject("baseUrl", baseUrl);
        mav.addObject("pagedListHolder", pagedListHolder);
        mav.addObject("nameFilter", fname);
        
        return mav;
    }
    
        @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/status/smsmessages/filtered"})
    public ModelAndView viewFilteredSmsMessageLog(@RequestParam(value = "nameFilter", required = false) String fname) {
        String filter = (fname == null || fname.length() == 0) ? "%" : "%" + fname + "%";
        System.out.println("Haetaan logeja nrolla = " + fname);
        ModelAndView mav = new ModelAndView("smsMessageLog");
        mav.addObject("messages", systemStatuseManager.getFilteredSmsMessages(fname));
        mav.addObject("nameFilter", fname);
        return mav;
    }
    
}
