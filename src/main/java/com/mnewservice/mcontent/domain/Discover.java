/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnewservice.mcontent.domain;

/**
 *
 * @author ubuntu1604
 */
public class Discover {
    private String iconurl;
    private String servicename;
    private String servicedescription;
    private int serviceduration;
    private Service service;

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getServicedescription() {
        return servicedescription;
    }

    public void setServicedescription(String servicedescription) {
        this.servicedescription = servicedescription;
    }
    
    public int getServiceduration() {
        return serviceduration;
    }

    public void setServiceduration(int serviceduration) {
        this.serviceduration = serviceduration;
    }

    
    
}
