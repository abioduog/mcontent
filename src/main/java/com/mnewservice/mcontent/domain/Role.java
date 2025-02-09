package com.mnewservice.mcontent.domain;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class Role {

    public static final String PROVIDER_SHOULD_BE_ENUM = "PROVIDER";

    private Long id;
    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }
// <editor-fold defaultstate="collapsed" desc="autogenerated getters / setters">

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
// </editor-fold>
}
