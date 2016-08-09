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
public class SubscriptionHistory implements Comparable<SubscriptionHistory>{
    private String keyword;
    private String code;
    private String expire;
    
    public int compareTo(SubscriptionHistory sh) {
        return keyword.compareTo(sh.keyword);
    }
    public SubscriptionHistory(){
        
    }
    
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    
}
