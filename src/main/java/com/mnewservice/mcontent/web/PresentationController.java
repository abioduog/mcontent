package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PresentationController {

    @Autowired
    private DeliveryPipeManager deliveryPipeManager;

    @RequestMapping({"/show/a/{short_uuid}"})
    public ModelAndView showContent(@PathVariable("short_uuid") String shortUuid) {
        ModelAndView mav = new ModelAndView("show");
        mav.addObject("theme", deliveryPipeManager.getThemeForContentByUuid(shortUuid));
        mav.addObject("content", deliveryPipeManager.getContentByUuid(shortUuid));
        return mav;
    }
}
