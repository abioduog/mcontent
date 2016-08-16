/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.manager.FileManager;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import jcifs.smb.SmbFile;
import org.apache.commons.io.IOUtils;
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
            @PathVariable("fileUuid") String fileUuid
    ) {
        fileUuid = fileUuid;
        LOG.info("Showing image file by UUID=" + fileUuid);
        FileEntity fileToShow = fileManager.getFileByUuid(fileUuid);
            
        if (fileToShow != null) {
            String filename = fileToShow.getOriginalFilename();
            LOG.info("Found: " + fileToShow.getPath() + "(" + fileToShow.getMimeType() + ") - " + fileToShow.getOriginalFilename());
            System.out.println("Found: " + fileToShow.getPath() + "(" + fileToShow.getMimeType() + ") - " + fileToShow.getOriginalFilename());
            InputStream imageinputstream = null;
            try {

            if(fileManager.isSambafile()){
                SmbFile file = fileManager.getSmbFileByPath(fileToShow.getPath());
                imageinputstream = file.getInputStream();
            }else{
                File file = fileManager.getFileByPath(fileToShow.getPath());
                imageinputstream = new FileInputStream(file);
            }
            
                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(fileToShow.getMimeType()));
                headers.setContentDispositionFormData(filename, filename);
                //return new ResponseEntity<>(IOUtils.toByteArray(file.getInputStream()), headers, HttpStatus.OK);
                return new ResponseEntity<>(IOUtils.toByteArray(imageinputstream), headers, HttpStatus.OK);
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
