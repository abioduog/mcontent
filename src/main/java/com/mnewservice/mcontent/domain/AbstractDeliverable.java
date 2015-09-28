package com.mnewservice.mcontent.domain;

public abstract class AbstractDeliverable {

    public final DeliverableType type;

    protected Long id;
    protected Content content;

    public AbstractDeliverable(DeliverableType type) {
        this.type = type;
        content = new Content();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

}
