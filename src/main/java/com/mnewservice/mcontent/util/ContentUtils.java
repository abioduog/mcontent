/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.util;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 *
 */
public class ContentUtils {

//    private static final String contentUrl = "http://3wc4.com/";
//    private static final String contentDir = "";
//    private static final String imageDir = "images/";
//
    private static final String contentUrl = "http://127.0.0.1:8084/";
    private static final String contentDir = "";
    private static final String imageDir = "images/";

    public static String createContentImageUrl(String imagePath) {
        return contentUrl + imageDir + imagePath;
    }

    public static String getContentUrl() {
        return contentUrl + getContentDir();
    }

    public static String getContentDir() {
        return contentDir;
    }

    public static String getImageDir() {
        return imageDir;
    }

    public static String getImageHtmlBlock(String theme, String url, String filename) {
        theme = theme.toLowerCase().trim();
        theme.replaceAll("/([\\t\\n\\r\\s])+/g", "-");
        theme.replaceAll("/([^A-Za-z0-9\\-])+/g", "");
        String escFilename = escapeHtml4(filename);
        return "<div class=\"content-image " + (theme.length() > 0 ? "content-theme-" + theme : "") + "\">" // class = content-image [content-theme-{theme}]
                + "<img src=\"" + url + "\" alt=\"" + escFilename + "\" /></div>";
    }
}
