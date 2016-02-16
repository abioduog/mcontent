/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.manager.FileManager;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import jcifs.smb.SmbFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */
@Controller
public class ImageLoadController {
    private static final Logger LOG
            = Logger.getLogger(ImageLoadController.class);

    @Autowired
    private FileManager fileManager;

    @RequestMapping(value = {"/images/{fileUuid}"})
    public @ResponseBody
    ResponseEntity<byte[]> getImageFile(
            @PathVariable("fileUuid") String fileUuid,
            HttpServletResponse request
    ) {
        LOG.info("Showing image file by UUID=" + fileUuid);
        byte[] response = {};
        FileEntity fileToShow = fileManager.getFileByUuid(fileUuid);
        if (fileToShow != null) {
            LOG.info("Found: " + fileToShow.getPath() + "(" + fileToShow.getMimeType() + ") - " + fileToShow.getOriginalFilename());
            SmbFile file = fileManager.getSmbFileByPath(fileToShow.getPath());
            response = new byte[file.getContentLength()];
            try {
                InputStream is = file.getInputStream();
                int b, indx = 0;
                while ((b = is.read()) != -1) {
                    response[indx++] = (byte) b;
                }
                is.close();
            } catch (Exception ex) {
                LOG.error("Error on reading SMB file", ex);
                throw new RuntimeException("IOError on reading SMB file");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileToShow.getMimeType()));
        String filename = fileToShow.getOriginalFilename();
        headers.setContentDispositionFormData(filename, filename);
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

}
