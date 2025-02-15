package com.mnewservice.mcontent.repository.entity;

import com.mnewservice.mcontent.util.DateUtils;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@NamedNativeQuery(
        name = "SubscriptionEntity.findByExpiry",
        query
        = "SELECT "
        + "    s.id, s.service_id, s.subscriber_id "
        + "FROM "
        + "    subscriptions s "
        + "    JOIN ( "
        + "	    SELECT "
        + "           s2.id s_id, max(p2.`end`) maxend "
        + "         FROM "
        + "           subscriptions s2 "
        + "           JOIN subscriptions_periods sp2 ON s2.id=sp2.subscriptions_id "
        + "           JOIN subscriptionperiods p2 ON p2.id=sp2.periods_id "
        + "         GROUP BY "
        + "           s2.id) smax ON smax.s_id=s.id "
        + "    JOIN subscriptions_periods sp ON s.id=sp.subscriptions_id "
        + "    JOIN subscriptionperiods p ON p.end=smax.maxend AND p.id=sp.periods_id "
        + "WHERE "
        + "    TIMESTAMPDIFF(DAY,p.`start`,p.`end`) >= :minDuration AND "
        + "    p.`end`= DATE(:expiryAt) AND "
        + "    s.service_id = :serviceId "
        + "ORDER BY "
        + "    s.id "
        //+ "    p.`end` desc "   // Pasi testasi, ei lajitellut
        + "LIMIT "
        + "    :limit "
        + "OFFSET "
        + "    :offset ",
        resultClass = SubscriptionEntity.class
)
@Table(
        name = "subscriptions",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"service_id", "subscriber_id"}
        )
)
public class SubscriptionEntity extends AbstractEntity {

    @ManyToOne
    private ServiceEntity service;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private SubscriberEntity subscriber;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("start DESC")
    private List<SubscriptionPeriodEntity> periods;

    public boolean isActive() {
        Date now = new Date();
        return periods.stream().filter(period -> /*period.getStart().before(now) && */ period.getEnd().after(now)).findFirst().isPresent();
    }

    public int getActiveDaysOverall() {
        int totalActiveDays = 0;
        Date currentDate = DateUtils.getCurrentDate();
        for (SubscriptionPeriodEntity period : this.getPeriods()) {
            if (period.getStart().after(currentDate)) {
                //This is not active yet
            } else if (period.getEnd().before(currentDate)) {
                //This is already totally ended
                totalActiveDays += DateUtils.calculateDifferenceInDays(
                        period.getEnd(),
                        period.getStart());
            } else {
                totalActiveDays += DateUtils.calculateDifferenceInDays(
                        currentDate,
                        period.getStart());
            }
        }
        return totalActiveDays;
    }

    // <editor-fold defaultstate="collapsed" desc="autogenerated getters / setters">
    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public SubscriberEntity getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(SubscriberEntity subscriber) {
        this.subscriber = subscriber;
    }

    public List<SubscriptionPeriodEntity> getPeriods() {
        return periods;
    }

    public void setPeriods(List<SubscriptionPeriodEntity> periods) {
        this.periods = periods;
    }
    // </editor-fold>
}
