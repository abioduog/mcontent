package com.mnewservice.mcontent.util;

import java.util.Locale;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class Messages {

    @Autowired
    private MessageSource messageSource;

    private MessageSourceAccessor accessor;

    @PostConstruct
    private void init() {
        // TODO: hardcoded locale, fix this when there is a need
        //       to support multiple languages
        accessor = new MessageSourceAccessor(messageSource, new Locale("en"));
    }

    public String get(String code) {
        return accessor.getMessage(code);
    }
}
