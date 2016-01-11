/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.domain;

import org.springframework.beans.BeanUtils;

/**
 *
 */
public class ContentProvider extends Provider {

    private long contentCount;

    public ContentProvider init(Provider provider, long count) {
        BeanUtils.copyProperties(provider, this);
        this.contentCount = count;
        return this;
    }

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

}
