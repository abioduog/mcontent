/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnewservice.mcontent.domain;

import java.util.Date;

/**
 *
 * @author Pasi Saukkonen <pasi.saukkonen at nolwenture.com>
 */
public class Resetpw {
    
    private String checksum;
    private Date expires;
    private Integer userid;
    
    public Resetpw(){
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

}
