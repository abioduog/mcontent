package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Content;
import com.mnewservice.mcontent.domain.Discover;
import com.mnewservice.mcontent.domain.Service;
import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import com.mnewservice.mcontent.domain.Subscriber;
import com.mnewservice.mcontent.domain.Subscription;
import com.mnewservice.mcontent.domain.SubscriptionPeriod;
import com.mnewservice.mcontent.domain.SubscriptionHistory;
import com.mnewservice.mcontent.manager.ServiceManager;
import com.mnewservice.mcontent.manager.SubscriberManager;
import com.mnewservice.mcontent.manager.SubscriptionManager;
import com.mnewservice.mcontent.repository.SubscriptionRepository;
import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    
    @Autowired
    private ServiceManager serviceManager;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Value("${application.notification.subscribeToService}")
    private String SUBSCRIBETOSERVICE;
    
    @RequestMapping({"/show/a/{short_uuid}"})
    public ModelAndView showContent(
            @PathVariable("short_uuid") String shortUuid,
            HttpServletRequest request
    ) {
        ModelAndView mav = new ModelAndView("show");
        mav.addObject("theme", deliveryPipeManager.getThemeForContentByUuid(shortUuid));
        mav.addObject("content", deliveryPipeManager.getContentByUuid(shortUuid));
        mav.addObject("subscriber", request.getRemoteUser());
        mav.addObject("subscriberhistory", request.getRemoteUser());
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
    @RequestMapping(value = {"/show/a/default"})   
    public ModelAndView showContentPasi(
            @RequestParam(value="uuid", required=false) String shortUuid1,
            HttpServletRequest request
    ) {
        /*
        System.out.println("Puhnro = " + request.getRemoteUser() + ", " + shortUuid1);
        String shortUuid ="6RLY";
        
        String urli = "/show/subscriber/" + request.getRemoteUser() +"/history";
        System.out.println(urli);
        */
        return viewSubscriptionHistory("", request.getRemoteUser(), request);        
/*
        ModelAndView mav = new ModelAndView("show");
        mav.addObject("theme", deliveryPipeManager.getThemeForContentByUuid(shortUuid));
        mav.addObject("content", deliveryPipeManager.getContentByUuid(shortUuid));
        mav.addObject("subscriber", request.getRemoteUser());
        mav.addObject("subscriberhistory", request.getRemoteUser());
        mav.addObject("short_uuid", shortUuid);
        return mav;
        */
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
        
        mav.addObject("subscriberhistory", request.getRemoteUser());
   
        return mav;
    }
    
    
    
    
    
    @RequestMapping(value="/show/discover", method=RequestMethod.GET)
    public ModelAndView showDiscover(
            @RequestParam(value="uuid", required=false) String shortUuid,
            @RequestParam(value="phone", required=false) String phone,
            HttpServletRequest request
    ) {
        /*
        // For test use
        shortUuid ="6RLY"; 
        phone="2345551000123";
        */
        ArrayList discoverlist = new ArrayList();
        Discover discover = new Discover();
       

        Collection <Service> services = serviceManager.getAllServices();
        Collection <Service> activelist = new ArrayList <Service>();

        //
        Subscriber sub = subscriberManager.getSubscriberWithSubscriptions(phone, true);
        Set<Subscription> subsc=sub.getSubscriptions();
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

            //SubscriptionHistory sh = new SubscriptionHistory();
            if(latestsp.getEnd().after(date)){
                /*
                sh.setKeyword(s.getService().getKeyword());
                sh.setCode(""+s.getService().getShortCode());
                sh.setExpire(datestring);

                System.out.println("Voimassa, poistetaan listalta: " + s.getService().getId());
*/                
                activelist.add(s.getService());
            }

        }
  

        //
        
        /*?MESSAGE=READ
        &SHORTCODE=33070
        &SENDER=2348097664330
        &OPERATOR=MTN
*/
        for (Service service : services) {
                discover = new Discover();
                //System.out.println("Service: " + service.getKeyword() + ", " + service.getSubscriptionPeriod());
                
                discover.setServicename(service.getKeyword());
                discover.setServicedescription(service.getServiceDescription());
                discover.setServiceduration(service.getSubscriptionPeriod());
                discover.setService(service);
                discoverlist.add(discover);

                for (Service serviceremove : activelist) {
                    //System.out.println("Voimassa: " + service.getKeyword() + ", " + service.getSubscriptionPeriod());
                    if (service.getId() == serviceremove.getId()) {
                        discoverlist.remove(discover);

                    }
                }
            }
        
        ModelAndView mav = new ModelAndView("discover");
        
        mav.addObject("theme", deliveryPipeManager.getThemeForContentByUuid(shortUuid));
        mav.addObject("content", deliveryPipeManager.getContentByUuid(shortUuid));
        mav.addObject("discoverlist", discoverlist);
        mav.addObject("subscriberhistory", request.getRemoteUser());
        mav.addObject("short_uuid", shortUuid);
        return mav;
    }
    
    
    
    
    @RequestMapping(value="/show/discover", method=RequestMethod.POST)
    public ModelAndView subscribeToService(
            @RequestParam(value="uuid", required=false) String shortUuid,
            @RequestParam(value="phone", required=false) String phone,
            @RequestParam(value="keyword", required=false) String keyword,
            @RequestParam(value="shortcode", required=false) String shortcode,
            @RequestParam(value="operator", required=false) String operator,

            HttpServletRequest request
    ) {
        //shortUuid ="6RLY"; // For test use
        
        //System.out.println("discover Post...");
        //System.out.println("discover Post..." + keyword + ", " + shortcode + ", " + operator + ", " + phone + ", " + shortUuid);
        
        ArrayList discoverlist = new ArrayList();
        Discover discover = new Discover();
        
        Collection <Service> services = serviceManager.getAllServices();
        for(Service service: services){
            //System.out.println("Service: " + service.getKeyword() + ", " + service.getSubscriptionPeriod());
        discover = new Discover();
        discover.setServicename(service.getKeyword());
        discover.setServicedescription(service.getServiceDescription());
        discover.setServiceduration(service.getSubscriptionPeriod());
        discover.setService(service);
        discoverlist.add(discover);
        }
        
        
        //System.out.println("Valmista");
        ModelAndView mav = new ModelAndView("subinstructions");
        
        mav.addObject("theme", deliveryPipeManager.getThemeForContentByUuid(shortUuid));
        mav.addObject("content", deliveryPipeManager.getContentByUuid(shortUuid));
        mav.addObject("discoverlist", discoverlist);
        mav.addObject("subscriberhistory", request.getRemoteUser());
        mav.addObject("short_uuid", shortUuid);
        return mav;
    }
    /*
    @RequestMapping(value = {"/show/discover/subinstructions1"})
    public String showSubscriptionInstructions(){
        //System.out.println("Toimii " + serviceSubscribeInstructionsDetails);
                    
        return "subinstructions";
    }
    */
    
    /*
    @Value("${service.subscribeInstructionsDetails}")
    String serviceSubscribeInstructionsDetails;
    */
    @RequestMapping(value = {"/show/discover/subinstructions"})
    
        public ModelAndView viewSubscriptionHistoryMav( @RequestParam(value="operator", required=false) String operator,
            @RequestParam(value="keyword", required=false) String keyword,
            @RequestParam(value="shortcode", required=false) String shortcode
            ){
             ModelAndView mav = new ModelAndView("subinstructions");
             
             //String serviceSubscribeInstructionsDetails = String.format("Subscribe to this service by sending sms message \"%s\" to number %s.", keyword, shortcode);
             String serviceSubscribeInstructionsDetails = String.format(SUBSCRIBETOSERVICE, keyword, shortcode);
             
             mav.addObject("subscribeInstructionsDetails", serviceSubscribeInstructionsDetails);
             return mav;
        }

    
}
