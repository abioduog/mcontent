package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.domain.SmsMessage;
import com.mnewservice.mcontent.manager.ServiceManager;
import com.mnewservice.mcontent.manager.SystemStatusManager;
import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import java.util.ArrayList;
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
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Antti Vikman on 7.3.2016.
 */
@Controller
public class SystemStatusController {

    private static final Logger LOG
            = Logger.getLogger(SystemStatusController.class);
    
    private int LIST_PAGE_SIZE = 25;

    @Autowired
    private SystemStatusManager systemStatuseManager;
/*
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/status/smsmessages_pasi"})
    public ModelAndView viewSmsMessageLog() {
        ModelAndView mav = new ModelAndView("smsMessageLog");
        mav.addObject("messages", systemStatuseManager.getSmsMessages());
        return mav;
    }
    */
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/status/smsmessages"})
    public String viewSmsMessageLogPagedFF(HttpServletRequest request) {
        //ModelAndView mav = new ModelAndView("smsMessageLogPaged");
        //mav.addObject("messages", systemStatuseManager.getSmsMessages());
        request.getSession().setAttribute("smsmessagesList", null);
        return "redirect:/status/smsmessagespaged/page/1";
    }
    /*
        @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/status/smsmessagespagedff/{pagenumber}"})
    public String viewSmsMessageLogPagedFFNext(HttpServletRequest request,  @PathVariable("pagenumber") Integer pagenumber, Model model) {
        //ModelAndView mav = new ModelAndView("smsMessageLogPaged");
        //mav.addObject("messages", systemStatuseManager.getSmsMessages());
        System.out.println("FF2 ->");
        //request.getSession().setAttribute("smsmessagesList", null);
        return "redirect:/status/smsmessagespaged/page/" + pagenumber;
    }
    
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/status/smsmessagespaged"})
    public ModelAndView viewSmsMessageLogPaged() {
        System.out.println("Paged");
        ModelAndView mav = new ModelAndView("smsMessageLogPaged");
        mav.addObject("messages", systemStatuseManager.getSmsMessages());
        return mav;
    }
    */
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value={"/status/smsmessagespaged/page/{pagenumber}"})
    public ModelAndView viewSmsMessageLogPageNumberX(HttpServletRequest request, @PathVariable("pagenumber") Integer pagenumber) {
        String baseUrl = "/status/smsmessagespaged/page";
        PagedListHolder<?> pagedListHolder = (PagedListHolder<?>) request.getSession().getAttribute("smsmessagesList"); 
        if(pagedListHolder == null){
            pagedListHolder = new PagedListHolder(systemStatuseManager.getSmsMessages());
            pagedListHolder.setPageSize(LIST_PAGE_SIZE);
        }else{
            final int goToPage = pagenumber - 1;
            if(goToPage <= pagedListHolder.getPageCount() && goToPage >= 0){
                pagedListHolder.setPage(goToPage);
            }
        }
        pagedListHolder.getPageList();
        request.getSession().setAttribute("smsmessagesList", pagedListHolder);
        int current = pagedListHolder.getPage() + 1;
        int begin = Math.max(1, current-LIST_PAGE_SIZE);
        int end = Math.min(begin+5, pagedListHolder.getPageCount());
        int totalPageCount = pagedListHolder.getPageCount();

        /*
        System.out.println("Begin, end, current : " + begin + ", " + end + ", " + current);
        ArrayList<SmsMessage> al = (ArrayList<SmsMessage>) systemStatuseManager.getSmsMessages();
        ArrayList<SmsMessage> al2 = new ArrayList<SmsMessage>(al.subList((begin - 1), end));
*/
        ModelAndView mav = new ModelAndView("smsMessageLogPaged");
        //mav.addObject("messages", systemStatuseManager.getSmsMessages());
        mav.addObject("messages", pagedListHolder.getPageList());
        mav.addObject("beginIndex", begin);
        mav.addObject("endIndex", end);
        mav.addObject("currentIndex", current);
        mav.addObject("totalPageCount", totalPageCount);
        mav.addObject("baseUrl", baseUrl);
        mav.addObject("pagedListHolder", pagedListHolder);
        
        return mav;
    }
    
}
