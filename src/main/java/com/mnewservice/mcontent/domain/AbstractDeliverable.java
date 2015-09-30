package com.mnewservice.mcontent.domain;

public abstract class AbstractDeliverable {

    public final DeliverableType type;

    protected Long id;
    protected Content content;
    protected DeliverableStatus status;

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

    public DeliverableStatus getStatus() {
        return status;
    }

    public void setStatus(DeliverableStatus status) {
        this.status = status;
    }

}
