package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import com.mnewservice.mcontent.manager.SubscriberManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PresentationController {

    private static final Logger LOG
            = Logger.getLogger(PresentationController.class);

    @Autowired
    private SubscriberManager subscriberManager;

    @Autowired
    private DeliveryPipeManager deliveryPipeManager;

    @RequestMapping({"/show/a/{short_uuid}"})
    public ModelAndView showContent(
            @PathVariable("short_uuid") String shortUuid,
            HttpServletRequest request
    ) {
        ModelAndView mav = new ModelAndView("show");
        mav.addObject("theme", deliveryPipeManager.getThemeForContentByUuid(shortUuid));
        mav.addObject("content", deliveryPipeManager.getContentByUuid(shortUuid));
        mav.addObject("subscriber", request.getRemoteUser());
        return mav;
    }

    @RequestMapping(value = {"/show/subscriber/{phone}"})
    public ModelAndView viewAllSubscriptions(
            @PathVariable("phone") String phone,
            @RequestParam(value = "showAll", required = false) boolean showAll
    ) {
        LOG.info("/show/subscriber/" + phone + "  showAll: [" + showAll + "]");
        ModelAndView mav = new ModelAndView("subscriberDetail");
        mav.addObject("subscriber", subscriberManager.getSubscriberWithSubscriptions(phone, showAll));
        mav.addObject("showAll", showAll);
        mav.addObject("reloadRef", "/show/subscriber/" + phone);
        return mav;
    }
}
