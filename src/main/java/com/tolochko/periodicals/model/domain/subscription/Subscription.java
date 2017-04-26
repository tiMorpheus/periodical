package com.tolochko.periodicals.model.domain.subscription;

import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.user.User;

import java.time.Instant;


public class Subscription {
    private long id;
    /**
     * The user this subscription belongs to.
     */
    private User user;
    /**
     * The periodical this subscription is on.
     */
    private Periodical periodical;
    private String deliveryAddress;
    /**
     * The expiration date of this subscription. It can be prolonged by creating and paying a new invoice
     * for the same periodical.
     */
    private Instant endDate;
    /**
     * Is {@code active} when a subscription is created. Becomes {@code inactive} when
     * this subscription is expired.
     */
    private Status status;

    public enum Status {
        ACTIVE, INACTIVE
    }

    public static class Builder {
        private Subscription subscription;

        public Builder() {
            subscription = new Subscription();
        }

        public Builder setId(Long id) {
            subscription.setId(id);
            return this;
        }

        public Builder setUser(User user) {
            subscription.setUser(user);
            return this;
        }

        public Builder setPeriodical(Periodical periodical) {
            subscription.setPeriodical(periodical);
            return this;
        }

        public Builder setDeliveryAddress(String deliveryAddress) {
            subscription.setDeliveryAddress(deliveryAddress);
            return this;
        }

        public Builder setEndDate(Instant endDate) {
            subscription.setEndDate(endDate);
            return this;
        }

        public Builder setStatus(Status status) {
            subscription.setStatus(status);
            return this;
        }

        public Subscription build() {
            return subscription;
        }

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Periodical getPeriodical() {
        return periodical;
    }

    public void setPeriodical(Periodical periodical) {
        this.periodical = periodical;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Subscription{id=%d, user=%s, periodical=%s, deliveryAddress='%s', " +
                "endDate=%s, status=%s}", id, user, periodical, deliveryAddress, endDate, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Subscription that = (Subscription) o;

        if (id != that.id) {
            return false;
        }

        if (user != null ? !user.equals(that.user) : that.user != null) {
            return false;
        }
        return periodical != null ? periodical.equals(that.periodical) : that.periodical == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (periodical != null ? periodical.hashCode() : 0);
        return result;
    }
}

