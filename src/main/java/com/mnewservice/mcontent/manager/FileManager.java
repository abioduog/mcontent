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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    //@Value("${application.filestorage.folder}")
    private String fileFolder;
        
    @Value("${application.filestorage.address}")
    private String smbPath;
    @Value("${application.filestorage.userDomain}")
    private String smbDomain;
    @Value("${application.filestorage.user}")
    private String smbUser;
    @Value("${application.filestorage.pass}")
    private String smbPass;

    private static final Logger LOG = Logger.getLogger(FileManager.class);

    public String getFilestoragePath(){
        return smbPath.substring(5);
    }
    
    public boolean isSambafile(){
        return (smbPath.startsWith("smb:"));
    }
    
    private SmbFile getSmbFile(String path) {
        SmbFile retval = null;
        //System.out.println("getSmbFile(String path) = " + path);

        try {
            NtlmPasswordAuthentication auth;
            if(!smbUser.isEmpty()) {
                LOG.debug("Authenticating as " + smbUser);
                auth = new NtlmPasswordAuthentication(smbDomain, smbUser, smbPass);
            } else {
                auth = NtlmPasswordAuthentication.ANONYMOUS;
            }
            retval = new SmbFile(path, auth);
        } catch (Exception ex) {
            LOG.error(ex);
        }
     
        return retval;
    }

    public File getFileByPath(String path){
        //System.out.println("getFileByPath(String path) = " + getFilestoragePath() + path);
        return new File(getFilestoragePath() + path);
    }
    
    public SmbFile getSmbFileByPath(String filePath) {
            return getSmbFile(smbPath + filePath);
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
    public FileEntity getFileByUuid(String uuid) {
        LOG.info("Getting file with uuid=" + uuid);
        return repository.getFileByUuid(uuid);
    }

    private void cleanUpDBEntry(FileEntity entity) {
        LOG.info("Removing File DB entity with id=" + entity.getId());
        repository.delete(entity);
    }

    // Save File to DB and SMB server
    private void cleanUpSMBEntry(SmbFile smbFile) {
        try {
            if (smbFile != null && smbFile.exists()) { // File allready exists
                LOG.info("Removing SMB file: " + smbFile.getPath());
                smbFile.delete();
            }
        } catch (Exception ex) {
            LOG.error("Removing SMB Failed !");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ContentFile saveFile(ContentFile contentFile, byte[] bytes) {
        LOG.info("Saving contentFile (" + contentFile.getOriginalFilename() + ") " + bytes.length + " bytes");

        contentFile.setPath(contentFile.generateFilepath());
        //System.out.println("saveFile(ContentFile contentFile, byte[] bytes)");
        SmbFile smbFile = null;

        if(isSambafile()){
            //System.out.println("Tallennetaan sambaan");
            
        try {
            // Create final smb file name
            smbFile = getSmbFileByPath(contentFile.getPath());
            if (smbFile == null) {
                contentFile.setErrorMessage("Could not access SMB file! path=" + contentFile.getPath());
                LOG.error("Could not access SMB file! path=" + contentFile.getPath());
                contentFile.setAccepted(false);
                return contentFile;
            }

            // Remove existing file
            cleanUpSMBEntry(smbFile);

            LOG.info("Creating new SMB file: " + smbFile.getPath());
            smbFile.createNewFile();
            smbFile.setReadWrite();

            LOG.info("Writing " + bytes.length + " bytes to SMB file: " + smbFile.getPath());
            

            OutputStream out = smbFile.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();

        } catch (Exception ex) {
            // Virheenkäpistelyä tähän...
            LOG.error(ex);
            contentFile.setErrorMessage(ex.getLocalizedMessage());
            cleanUpSMBEntry(smbFile);
            contentFile.setAccepted(false);
            return contentFile;
        }

        LOG.info("Saved SMB file: " + smbFile.getPath());
            }else{
            //System.out.println("Tallennetaan kansioon");
            saveFileToFolder(contentFile, bytes);
            }
        // Save to DB
        FileEntity entity = mapper.toEntity(contentFile);
        entity = repository.save(entity);
        contentFile.setId(entity.getId());

        return contentFile;
    }

    @Transactional
    public void deleteFile(FileEntity file) {
        LOG.info("Deleting contentFile: " + file.getPath() + " (" + file.getOriginalFilename() + ")");
        //System.out.println("deleteFile(FileEntity file)" + getFilestoragePath() + file.getPath());
        SmbFile smbFile = getSmbFile(file.getPath());
        cleanUpSMBEntry(smbFile);
        cleanUpDBEntry(file);
        if(!isSambafile()){
            File newFile = new File(getFilestoragePath() + file.getPath());
            cleanUpFileFolderEntry(newFile);
        }
    }

    @Transactional
    public void deleteFile(Long fileId) {
        LOG.info("Searching contentFile for removal with id=" + fileId);
         //System.out.println("deleteFile(Long fileId)");
        FileEntity file = repository.findOne(fileId);
        deleteFile(file);
    }
    
        public ContentFile saveFileToFolder(ContentFile contentFile, byte[] bytes) {
            //System.out.println("saveFileToFolder(ContentFile contentFile, byte[] bytes)");
        LOG.info("Saving contentFile to folder(" + contentFile.getOriginalFilename() + ") " + bytes.length + " bytes");

        contentFile.setPath(contentFile.generateFilepath());
        //System.out.println("saveFileToFolder(ContentFile contentFile, byte[] bytes) = " + smbPath + contentFile.getPath());
        //fileFolder=smbPath.substring(5);
        String fileName = contentFile.getOriginalFilename();
        File newFile = new File(getFilestoragePath() + contentFile.getPath());
        //System.out.println("newFile = " + newFile.getPath());

            try {

                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
                OutputStream outputStream = new FileOutputStream(newFile);
                outputStream.write(bytes);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return contentFile;
    }
        
            private void cleanUpFileFolderEntry(File folderFile) {
                //System.out.println("Poistetaan... " + folderFile.getPath());
                
        try {
            if (folderFile != null && folderFile.exists()) { // File allready exists
                LOG.info("Removing file from folder: " + folderFile.getPath());
                
                folderFile.delete();
                //System.out.println("Poistetettu... " + folderFile.getPath());
            }
        } catch (Exception ex) {
            LOG.error("Removing file from folder Failed !");
        }
    }
    
}
