package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Content;
import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.domain.SubscriptionPeriod;
import com.mnewservice.mcontent.domain.SubscriptionHistory;
import com.mnewservice.mcontent.manager.SubscriberManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
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
        mav.addObject("subscriberhistory", request.getRemoteUser() + "/history");
        mav.addObject("short_uuid", shortUuid);
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
    
    @RequestMapping(value = {"/show/subscriber/{phone}/history"})
    public ModelAndView viewSubscriptionHistory(
            @RequestParam(value="uuid", required=false) String shortUuid,
            @PathVariable("phone") String phone,
            HttpServletRequest request
    ) {
        //String shortUuid1 ="6RLY"; // For test use
        //System.out.println("history and phone: " + phone + ", uuid = " + shortUuid);      

        Subscriber sub = subscriberManager.getSubscriberWithSubscriptions(phone, true);
        
        Set<Subscription> subsc=sub.getSubscriptions();
        ArrayList <SubscriptionHistory> activelist = new ArrayList <SubscriptionHistory>();
        ArrayList <SubscriptionHistory> expiredlist = new ArrayList <SubscriptionHistory>();
        

        for(Subscription s : subsc){

            Collection c=s.getPeriods();
            final Iterator itr = c.iterator();
            SubscriptionPeriod sp, latestsp = null;
            while(itr.hasNext()) {
                sp = (SubscriptionPeriod)itr.next();
                if (latestsp == null){
                latestsp = sp;
                }
                if(latestsp != null){
                    if(latestsp.getEnd().before(sp.getEnd())){
                        latestsp=sp;
                    }
                }    

            }


            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String datestring = dateFormat.format(latestsp.getEnd()); 

            SubscriptionHistory sh = new SubscriptionHistory();
            if(latestsp.getEnd().after(date)){
                sh.setKeyword(s.getService().getKeyword());
                sh.setCode(""+s.getService().getShortCode());
                sh.setExpire(datestring);

                activelist.add(sh);
            }else{
                sh.setKeyword(s.getService().getKeyword());
                sh.setCode(""+s.getService().getShortCode());
                sh.setExpire(datestring);

                expiredlist.add(sh);
            }

        }
        Collections.sort(activelist);
        Collections.sort(expiredlist);
        Content cnt=new Content();
        cnt.setTitle("My subscriptions");
        cnt.setMessage("Message");
        cnt.setContent(" ");
        
        ModelAndView mav = new ModelAndView("history");
        mav.addObject("theme", deliveryPipeManager.getThemeForContentByUuid(shortUuid));
        mav.addObject("content", cnt);

        mav.addObject("activelist", activelist);
        mav.addObject("expiredlist", expiredlist);
        mav.addObject("short_uuid", shortUuid);
        
        mav.addObject("subscriberhistory", request.getRemoteUser() + "/history");
   
        return mav;
    }
}
