package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Content;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ContentController {

    private static final Logger LOG
            = Logger.getLogger(ContentController.class);

    @ModelAttribute("content")
    public Content populateContent() {
        Content content = new Content();
        content.setTitle("Great content");
        content.setContent("With lots of information.");
        return content;
    }

    @RequestMapping({"/show"})
    public String showContent() {
        return "show";
    }
}
