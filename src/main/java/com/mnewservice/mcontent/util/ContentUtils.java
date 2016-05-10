/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.util;

import javax.servlet.ServletContext;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 *
 */

public class ContentUtils {

    private static final String contentUrl = "http://3wc4.com/";
    private static final String imageUrl = "/images/";
//    private static final String contentUrl = "http://127.0.0.1:8084/mContent/show/a/";
    
    private static String getRootUrl(ServletContext sc) {
        // getContextPath = /mContent
        // getRealPath = C:\Users\[username]\Documents\Projects\mcontent\target\mContent-1.0-SNAPSHOT\
        // getServletContextName = null
        // getVirtualServerName = localhost
        return sc.getContextPath() + "/";
    }
    
    public static String createImageUrl(ServletContext sc, String imagePath) {
        return ContentUtils.getRootUrl(sc) + imageUrl + imagePath;
    }

    public static String getContentUrl() {
        return contentUrl;
    }

    public static String getImageUrl() {
        return imageUrl;
    }

    public static String getImageHtmlBlock(String url, String id, String filename) {
        String escFilename = escapeHtml4(filename);
        return "<div class='content-image'><img id='" + id + "' src='" + url + "' alt='" + escFilename + "' /></div>";
    }
}
