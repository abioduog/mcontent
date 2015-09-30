package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.BinaryContent;
import com.mnewservice.mcontent.manager.BinaryContentManager;
import com.mnewservice.mcontent.manager.ProviderManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Controller
public class CorrespondenceController {

    private static final Logger LOG
            = Logger.getLogger(CorrespondenceController.class);
    private static final String ERROR_CORRESPONDENCE_WAS_NOT_FOUND
            = "Correspondence with id=%d was not found";
    private static final String ERROR_WRITING_CONTENT_TO_OUTPUT_STREAM
            = "Error writing content to output stream.";

    @Autowired
    private BinaryContentManager binaryContentManager;

    @Autowired
    private ProviderManager providerManager;

    @RequestMapping({"/correspondence/{id}/view"})
    @ResponseStatus(value = HttpStatus.OK)
    public void viewCorrespondenceContent(
            @PathVariable("id") long id,
            HttpServletResponse response) {
        BinaryContent correspondence = binaryContentManager.getBinaryContent(id);
        if (correspondence == null) {
            String msg = String.format(ERROR_CORRESPONDENCE_WAS_NOT_FOUND, id);
            LOG.error(msg);
            throw new NoSuchElementException(msg);
        }

        try {
            response.setContentType(correspondence.getContentType());
            InputStream is = new ByteArrayInputStream(correspondence.getContent());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            LOG.error(ERROR_WRITING_CONTENT_TO_OUTPUT_STREAM);
            throw new RuntimeException(ERROR_WRITING_CONTENT_TO_OUTPUT_STREAM, ex);
        }
    }

    @RequestMapping({"/correspondence/add"})
    @ResponseStatus(value = HttpStatus.OK)
    public void addCorrespondence(
            @RequestParam("correspondenceFile") MultipartFile correspondenceFile) {
        LOG.info("correspondenceFile=" + correspondenceFile);
    }

    @RequestMapping({"/correspondence/{id}/remove"})
    @ResponseStatus(value = HttpStatus.OK)
    public void removeCorrespondence(@PathVariable("id") long id) {
        boolean retVal = providerManager.removeCorrespondence(id);
        if (!retVal) {
            String msg = String.format(ERROR_CORRESPONDENCE_WAS_NOT_FOUND, id);
            LOG.error(msg);
            throw new NoSuchElementException(msg);
        }
    }

}
