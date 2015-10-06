package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.manager.SubscriberManager;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SubscriberController {

    @Autowired
    private SubscriberManager subscriberManager;

    @PreAuthorize("hasAuthority('ADMIN')")
    @ModelAttribute("allSubscribers")
    public List<Subscriber> populateSubscribers() {
        return subscriberManager.getAllSubscribers()
                .stream().collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/subscriber/list"})
    public String listSubscribers() {
        return "subscriberList";
    }
}
