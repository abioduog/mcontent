package com.mnewservice.mcontent.repository.entity;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "deliverables")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "deliverableType")
public abstract class AbstractDeliverableEntity extends AbstractEntity {

    private String deliverableType;

    @ManyToOne
    private ServiceEntity service;

    @OneToOne
    private AbstractContentEntity content;

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public AbstractContentEntity getContent() {
        return content;
    }

    public void setContent(AbstractContentEntity content) {
        this.content = content;
    }

    public String getDeliverableType() {
        return deliverableType;
    }

    public void setDeliverableType(String deliverableType) {
        this.deliverableType = deliverableType;
    }

}
