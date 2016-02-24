/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.manager.FileManager;
import com.mnewservice.mcontent.repository.entity.FileEntity;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import jcifs.smb.SmbFile;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
            HttpServletResponse response
    ) {
        LOG.info("Showing image file by UUID=" + fileUuid);
        //byte[] response = {};
        FileEntity fileToShow = fileManager.getFileByUuid(fileUuid);
        if (fileToShow != null) {
            LOG.info("Found: " + fileToShow.getPath() + "(" + fileToShow.getMimeType() + ") - " + fileToShow.getOriginalFilename());
            SmbFile file = fileManager.getSmbFileByPath(fileToShow.getPath());
            /*
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
            */
            String filename = fileToShow.getOriginalFilename();
            try {
                response.setContentType(fileToShow.getMimeType());
                response.setHeader(filename, filename);
                IOUtils.copy(file.getInputStream(), response.getOutputStream());
                response.flushBuffer();
            } catch (IOException ex) {
            LOG.info("Error writing file to output stream. Filename was " + filename, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
        } else {
            LOG.error("Error file not found from the DB");
            throw new RuntimeException("Now such image in the system.");
        }
    }

}
