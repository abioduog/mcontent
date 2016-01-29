package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDeliverable {

    public final DeliverableType type;

    protected Long id;
    protected Content content;
    protected DeliverableStatus status;
    protected List<ContentFile> files;

    public AbstractDeliverable(DeliverableType type) {
        this.type = type;
        this.content = new Content();
        this.files = new ArrayList<>();
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

    public List<ContentFile> getFiles() {
        return files;
    }

    public void setFiles(List<ContentFile> files) {
        this.files = files;
    }

}
