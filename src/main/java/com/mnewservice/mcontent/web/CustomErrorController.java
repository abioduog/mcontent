package com.mnewservice.mcontent.web;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Controller
public class CustomErrorController
        implements ErrorController {

    private static final Logger LOG
            = Logger.getLogger(CustomErrorController.class);

    private static final String PATH = "/error";

    private static final String ATTRIBUTE_EXCEPTION
            = "javax.servlet.error.exception";
    private static final String ATTRIBUTE_STATUS_CODE
            = "javax.servlet.error.status_code";
    private static final String ATTRIBUTE_MESSAGE
            = "javax.servlet.error.message";

    @RequestMapping(value = PATH)
    public String error(HttpServletRequest request, Model model) {
        Throwable throwable
                = (Throwable) request.getAttribute(ATTRIBUTE_EXCEPTION);
        LOG.debug(throwable);

        Integer errorCode = (Integer) request.getAttribute(ATTRIBUTE_STATUS_CODE);
        String errorMessage = (String) request.getAttribute(ATTRIBUTE_MESSAGE);
        model.addAttribute("errorCode", errorCode);
        model.addAttribute(
                "errorMessage",
                getErrorMessage(throwable, errorCode, errorMessage)
        );

        return "error";
    }

    private static String getErrorMessage(Throwable throwable,
            Integer errorCode, String errorMessage) {
        String returnMessage;

        if (throwable == null && errorMessage != null && !errorMessage.isEmpty()) {
            returnMessage = errorMessage;
        } else if (throwable == null) {
            HttpStatus status = HttpStatus.valueOf(errorCode);
            if (status != null) {
                returnMessage = status.getReasonPhrase();
            } else {
                returnMessage = null;
            }
        } else if (throwable.getCause() == null || throwable.getCause().getMessage() == null) {
            returnMessage = throwable.getMessage();
        } else {
            returnMessage = throwable.getCause().getMessage();
        }

        return returnMessage;
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
