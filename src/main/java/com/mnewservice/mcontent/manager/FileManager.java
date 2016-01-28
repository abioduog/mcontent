/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.ContentFile;
import com.mnewservice.mcontent.domain.mapper.FileMapper;
import com.mnewservice.mcontent.repository.FileRepository;
import com.mnewservice.mcontent.repository.entity.FileEntity;
import java.io.OutputStream;
import java.net.URLEncoder;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
public class FileManager {

    @Autowired
    private FileRepository repository;

    @Autowired
    private FileMapper mapper;

    public enum FilePrefix {

        CONTENT_FILE("CF_"),
        OTHER_FILE("OF_");

        private String enumString;

        private FilePrefix(String enumString) {
            this.enumString = enumString;
        }

        @Override
        public String toString() {
            return this.enumString;
        }
    }

    private static String smbPath = "smb://127.0.0.1/mContent/";

    private static final Logger LOG = Logger.getLogger(FileManager.class);

    private SmbFile getSmbFile(String path) {
        SmbFile retval = null;
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("NOLWLAP005", "mContent", "juttu1234homma");
            //NtlmPasswordAuthentication auth = NtlmPasswordAuthentication.ANONYMOUS;
            retval = new SmbFile(path, auth);
        } catch (Exception ex) {
            LOG.error(ex);
        }
        return retval;
    }

    @Transactional(readOnly = true)
    public ContentFile getFile(long id) {
        LOG.info("Getting file with id=" + id);
        FileEntity entity = repository.findOne(id);
        return mapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public FileEntity getFileEntity(long id) {
        LOG.info("Getting file with id=" + id);
        return repository.findOne(id);
    }

    @Transactional(readOnly = true)
    public ContentFile findByFilename(String filename) {
        LOG.info("Finding provider with filename=" + filename);
        return mapper.toDomain(repository.findByFilename(filename));
    }

    private void cleanUpDBEntry(FileEntity entity) {
        LOG.info("Removing File DB entity with id=" + entity.getId());
        repository.delete(entity);
    }

    // Save File to DB and SMB server
    private void cleanUpSMBEntry(SmbFile smbFile, String path) {
        try {
            if (smbFile != null && smbFile.exists()) { // File allready exists
                LOG.info("Removing SMB file: " + path);
                smbFile.delete();
            }
        } catch (Exception ex) {
            LOG.error("Removing SMB Failed ! file: " + path);
        }
    }

    @Transactional
    public ContentFile saveFile(ContentFile contentFile, byte[] bytes){
        LOG.info("Saving contentFile (" + contentFile.getOriginalFilename() + ") " + bytes.length + " bytes");

        contentFile.setPath(smbPath);
        FileEntity entity = mapper.toEntity(contentFile);
        // Save 1st time to get id for entity
        contentFile = mapper.toDomain(repository.save(entity));

        SmbFile smbFile = null;
        String path = null;
        try {
            // Create final smb file name
            contentFile.setFilename(FilePrefix.CONTENT_FILE + String.format("%010d", contentFile.getId()) + "_" + URLEncoder.encode(contentFile.getOriginalFilename(), "UTF-8"));
            path = contentFile.getPath() + contentFile.getFilename();

            smbFile = getSmbFile(path);
            if (smbFile == null) {
                contentFile.setErrorMessage("Could not access SMB file! path=" + path);
                LOG.error("Could not access SMB file! path=" + path);
                cleanUpDBEntry(entity);
                contentFile.setAccepted(false);
                return contentFile;
            }

            cleanUpSMBEntry(smbFile, path);

            LOG.info("Creating new SMB file: " + path);
            smbFile.createNewFile();
            smbFile.setReadWrite();

            LOG.info("Writing " + bytes.length + " bytes to SMB file: " + path);
            OutputStream out = smbFile.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception ex) {
            // Virheenkäpistelyä tähän...
            LOG.error(ex);
            contentFile.setErrorMessage(ex.getLocalizedMessage());
            cleanUpDBEntry(entity);
            cleanUpSMBEntry(smbFile, path);
            contentFile.setAccepted(false);
            return contentFile;
        }

        LOG.info("Saved SMB file: " + path);

        entity = mapper.toEntity(contentFile);
        // Final save withe SMB filename
        return mapper.toDomain(repository.save(entity));
    }

}
