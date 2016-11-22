package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.ProviderInfo;
import com.mnewservice.mcontent.manager.ProviderManager;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Controller
public class ProviderController {

    private static final Logger LOG
            = Logger.getLogger(ProviderController.class);

    private int LIST_PAGE_SIZE = 25; // How many rows in page
    private int PAGINATION_MENU_SIZE = 5; // How many numbers is visible in pagination menu
    
    @Autowired
    private ProviderManager providerManager;

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
    @ModelAttribute("allProviders")
    public List<ProviderInfo> populateProviders() {
        List<ProviderInfo> contentProviders = providerManager.getAllProviders()
                .stream().map(p -> {
            return (new ProviderInfo()).init(p, providerManager.getPipeCount(p.getId()));
                }).collect(Collectors.toList());

        return contentProviders;
    }
/*
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/provider/list_alk"})
    public String listProviders() {
        return "providerList";
    }
*/
    
    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping({"/provider/list"})
    public String listProvidersPaged(HttpServletRequest request) {
                request.getSession().setAttribute("providerList", null);              
                request.getSession().setAttribute("allProviders", null); 
        return "redirect:/provider/list/page/1";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping(value={"/provider/list/page/{pagenumber}"})
    //@ResponseStatus(value = HttpStatus.OK)
    public ModelAndView viewProvidersPageNumberX(HttpServletRequest request, @PathVariable("pagenumber") Integer pagenumber, @RequestParam(value = "nameFilter", required = false) String fname) {
        String baseUrl = "/provider/list/page";
        PagedListHolder<?> pagedListHolder = (PagedListHolder<?>) request.getSession().getAttribute("allProviders"); 
        
        if(pagedListHolder == null){
            pagedListHolder = new PagedListHolder(populateProviders()); 

        }else{  
            final int goToPage = pagenumber - 1;
            if(goToPage <= pagedListHolder.getPageCount() && goToPage >= 0){
                pagedListHolder.setPage(goToPage);
            }
            if(fname != null){
                pagedListHolder = new PagedListHolder(populateFilteredProviders(fname));
            }
        }
        pagedListHolder.setPageSize(LIST_PAGE_SIZE);
        pagedListHolder.getPageList();
        request.getSession().setAttribute("allProviders", pagedListHolder);
        int current = pagedListHolder.getPage() + 1;
        int begin = Math.max(1, current - (PAGINATION_MENU_SIZE / 2));
        int end = Math.min(begin + (PAGINATION_MENU_SIZE - 1), pagedListHolder.getPageCount());
        int totalPageCount = pagedListHolder.getPageCount();

        /*
        System.out.println("Begin, end, current : " + begin + ", " + end + ", " + current);
        ArrayList<SmsMessage> al = (ArrayList<SmsMessage>) systemStatuseManager.getSmsMessages();
        ArrayList<SmsMessage> al2 = new ArrayList<SmsMessage>(al.subList((begin - 1), end));
*/
        ModelAndView mav = new ModelAndView("providerListPaged");
        //mav.addObject("messages", systemStatuseManager.getSmsMessages());
        mav.addObject("allProviders", pagedListHolder.getPageList());
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
    @RequestMapping({"/provider/{id}"})
    public ModelAndView viewService(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("providerDetail");
        mav.addObject("provider", (new ProviderInfo()).init(providerManager.getProvider(id), providerManager.getPipeCount(id)));
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping({"/provider/remove/{providerId}"})
    public ModelAndView viewRemovableProvider(@PathVariable("providerId") long id) {
        ModelAndView mav = new ModelAndView("providerRemove");
        mav.addObject("provider", (new ProviderInfo()).init(providerManager.getProvider(id), providerManager.getPipeCount(id)));
        if (providerManager.getPipeCount(id) > 0) {
            mav.addObject("hasPipes", "true");
        }
        return mav;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = {"/provider/remove/{providerId}"}, params = {"remove"})
    public ModelAndView removeProvider(
            @PathVariable("providerId") String id,
            final ProviderInfo provider,
            final BindingResult bindingResult,
            final ModelMap model) {
        ModelAndView mav = new ModelAndView("providerRemove");

        if (bindingResult.hasErrors()) {
            mav = mavAddNLogErrorText(mav, bindingResult.getAllErrors());
//            bindingResult.getAllErrors().stream().forEach((error) -> {
//                LOG.error(error.toString());
//            });
            mav.addObject("provider", model.getOrDefault("provider", new ProviderInfo()));
            mav.addObject("error", true);
        } else {
            try {
                providerManager.removeProvider(provider.getId());
                provider.setCorrespondences(null);
                mav.addObject("provider", provider);
                mav.addObject("removed", true);
            } catch (Exception ex) {
                LOG.error(ex);
//                mav.addObject("provider", provider);
//                mav.addObject("error", true);
//                mav.addObject("errortext", ex.getLocalizedMessage());
                throw new DataHandlingException(ex);
            }
        }

        return mav;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','PROVIDER')")
    @RequestMapping({"/provider/list/filtered/"})
    public ModelAndView listFilteredProviders(HttpServletRequest request, @RequestParam(value = "nameFilter", required=false) String fname) {
        System.out.println("listFilteredProviders: nameFilter = " + fname);
        ModelAndView mav = new ModelAndView("providerList");
        
        mav.addObject("allProviders", populateFilteredProviders(fname));

        mav.addObject("nameFilter", fname);
        
        return mav;
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @ModelAttribute("allProviders")
    public List<ProviderInfo> populateFilteredProviders(String nameFilter) {
        List<ProviderInfo> contentProviders = providerManager.getFilteredProviders(nameFilter)
                .stream().map(p -> {
            return (new ProviderInfo()).init(p, providerManager.getPipeCount(p.getId()));
                }).collect(Collectors.toList());
        return contentProviders;
    }

   
}
