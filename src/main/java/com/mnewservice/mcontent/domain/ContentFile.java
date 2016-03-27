/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.domain;

import com.mnewservice.mcontent.util.ContentUtils;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;

/**
 *
 */
public class ContentFile {

    private Long id;

    private String uuid;
    private byte[] thumbImage; // png format
//    private String filename;
    private String originalFilename;
    private String path;
    private String mimeType;

    // Only DTO level
    private String errorMessage;
    private boolean accepted;
    private String imageHtmlBlock;
    private static Integer maxSize = 100; // Max thumb picture size 100x100

    public ContentFile() {
        this.errorMessage = "";
        this.accepted = true;
        this.uuid = UUID.randomUUID().toString();
        this.imageHtmlBlock = "";
    }

//<editor-fold defaultstate="collapsed" desc="Std get/set">
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUuid() {
        return uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public byte[] getThumbImage() {
        return thumbImage;
    }
    
    public void setThumbImage(byte[] thumbImage) {
        this.thumbImage = thumbImage;
    }
    
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public boolean isAccepted() {
        return accepted;
    }
    
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
    public String getImageHtmlBlock() {
        return imageHtmlBlock;
    }
    
    public void setImageHtmlBlock(String imageHtmlBlock) {
        this.imageHtmlBlock = imageHtmlBlock;
    }
//</editor-fold>

    // get method for thymeleaf, can be assigned in thymeleaf as "variable.thumbBase64Png"
    public String getThumbBase64Png() {
        try {
            return new String(Base64.encodeBase64(thumbImage), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public String createAndSetImageHtmlBlock() {
        this.imageHtmlBlock = ContentUtils.getImageHtmlBlock(getImageUrl(), getUuid().toString(), getOriginalFilename());
        return this.imageHtmlBlock;
    }

    public String getImageUrl() {
        try {
            return Content.createContentImageUrl(URLEncoder.encode(path, "UTF-8"));
        } catch (Exception ex) {
            return Content.createContentImageUrl(path);
        }
    }

    public String generateFilepath() {
        String[] parts = this.getOriginalFilename().split("\\.");
        String name = uuid + "." + parts[parts.length - 1];
        try {
            return URLEncoder.encode(name, "UTF-8");
        } catch (Exception ex) {
            return name;
        }
    }

    public static byte[] generateThumbImage(byte[] data) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ImageIO.setUseCache(false);
        BufferedImage img = ImageIO.read(in);

        Integer newHeight = maxSize, height = img.getHeight();
        Integer newWidth = maxSize, width = img.getWidth();
        float ratio = width.floatValue() / height.floatValue();
        if (ratio > 1.0) {
            newHeight = new Double(maxSize / ratio).intValue();
        } else {
            newWidth = new Double(maxSize * ratio).intValue();
        }
        BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        newImg.createGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
        in.close();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(newImg, "png", out);
        byte[] retval = out.toByteArray();
        out.close();
        return retval;
    }
}
