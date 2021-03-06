package com.tolochko.periodicals.model.domain.invoice;

import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.user.User;

import java.io.Serializable;
import java.time.Instant;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 12412412488L;
    private long id;
    private User user;
    private Periodical periodical;
    private int subscriptionPeriod;
    private long totalSum;
    private transient Instant creationDate;
    private transient Instant paymentDate;
    private Status status;

    public enum Status {
        NEW, PAID
    }

    public static class Builder {
        private Invoice invoice;

        public Builder() {
            invoice = new Invoice();
        }

        public Builder setId(Long id) {
            invoice.setId(id);
            return this;
        }

        public Builder setUser(User user) {
            invoice.setUser(user);
            return this;
        }

        public Builder setPeriodical(Periodical periodical) {
            invoice.setPeriodical(periodical);
            return this;
        }

        public Builder setTotalSum(long totalSum) {
            invoice.setTotalSum(totalSum);
            return this;
        }

        public Builder setSubscriptionPeriod(int subscriptionPeriod) {
            invoice.setSubscriptionPeriod(subscriptionPeriod);
            return this;
        }

        public Builder setStatus(Status status) {
            invoice.setStatus(status);
            return this;
        }

        public Builder setCreationDate(Instant creationDate) {
            invoice.setCreationDate(creationDate);
            return this;
        }

        public Builder setPaymentDate(Instant paymentDate) {
            invoice.setPaymentDate(paymentDate);
            return this;
        }

        public Invoice build() {
            return invoice;
        }
    }

    public Long getId() {
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

    public long getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(long totalSum) {

        this.totalSum = totalSum;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Periodical getPeriodical() {
        return periodical;
    }

    public void setPeriodical(Periodical periodical) {
        this.periodical = periodical;
    }

    public int getSubscriptionPeriod() {
        return subscriptionPeriod;
    }

    public void setSubscriptionPeriod(int subscriptionPeriod) {
        this.subscriptionPeriod = subscriptionPeriod;
    }

    @Override
    public String toString() {
        return String.format("Invoice{id=%d, user=%s, periodical=%s, subscriptionPeriod=%d, "
                        + "totalSum=%s, creationDate=%s, paymentDate=%s, status=%s}",
                id, user, periodical, subscriptionPeriod, totalSum,
                creationDate, paymentDate, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Invoice invoice = (Invoice) o;

        if (id != invoice.id) {
            return false;
        }
        if (subscriptionPeriod != invoice.subscriptionPeriod) {
            return false;
        }
        if (user != null ? !user.equals(invoice.user) : invoice.user != null) {
            return false;
        }
        if (periodical != null ? !periodical.equals(invoice.periodical) : invoice.periodical != null) {
            return false;
        }
        return creationDate != null ? creationDate.equals(invoice.creationDate) : invoice.creationDate == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (periodical != null ? periodical.hashCode() : 0);
        result = 31 * result + subscriptionPeriod;
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        return result;
    }
}
