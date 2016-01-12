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
public class ProviderInfo extends Provider {

    private long pipeCount;

    public ProviderInfo init(Provider provider, long count) {
        BeanUtils.copyProperties(provider, this);
        this.pipeCount = count;
        return this;
    }

    public long getPipeCount() {
        return pipeCount;
    }

    public void setPipeCount(long pipeCount) {
        this.pipeCount = pipeCount;
    }

}
