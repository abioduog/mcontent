package com.mnewservice.mcontent.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author Pasi Saukkonen <pasi.saukkonen at nolwenture.com>
 */
@Entity
@Table(name = "resetpw")

public class ResetpwEntity extends AbstractEntity {
    
    private String checksum;
    private Date expires;
    private Integer userid;

    public ResetpwEntity(){
        
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
