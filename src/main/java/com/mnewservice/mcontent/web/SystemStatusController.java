package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.manager.ServiceManager;
import com.mnewservice.mcontent.manager.SystemStatusManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Antti Vikman on 7.3.2016.
 */
@Controller
public class SystemStatusController {

    private static final Logger LOG
            = Logger.getLogger(SystemStatusController.class);

    @Autowired
    private SystemStatusManager systemStatuseManager;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/status/smsmessages"})
    public ModelAndView viewSmsMessageLog() {
        ModelAndView mav = new ModelAndView("smsMessageLog");
        mav.addObject("messages", systemStatuseManager.getSmsMessages());
        return mav;
    }
}
