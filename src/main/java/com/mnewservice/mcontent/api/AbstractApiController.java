package com.mnewservice.mcontent.api;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class AbstractApiController {

    private static final Logger LOG
            = Logger.getLogger(AbstractApiController.class);

    protected static final String RETURN_VALUE_SUCCESSFUL = "SUCCESSFUL";
    protected static final String RETURN_VALUE_UNSUCCESSFUL = "UNSUCCESSFUL";

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException iae,
            HttpServletResponse response) throws IOException {
        LOG.error(iae.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), iae.getMessage());
    }

}
