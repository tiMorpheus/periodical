package com.tolochko.periodicals.model.domain.periodical;

import java.io.Serializable;

public class Periodical implements Serializable {
    private static final long serialVersionUID = 2223323888L;
    private long id;
    private String name;
    private PeriodicalCategory category;
    private String publisher;
    private String description;
    private long oneMonthCost;
    private Status status;

    public enum Status {
        ACTIVE, INACTIVE, DISCARDED
    }

    public enum OperationType {
        CREATE, UPDATE
    }

    public static class Builder {
        private Periodical periodical;

        public Builder() {
            periodical = new Periodical();
        }

        public Builder setId(long id) {
            periodical.setId(id);
            return this;
        }

        public Builder setName(String name) {
            periodical.setName(name);
            return this;
        }

        public Builder setCategory(PeriodicalCategory category) {
            periodical.setCategory(category);
            return this;
        }

        public Builder setPublisher(String publisher) {
            periodical.setPublisher(publisher);
            return this;
        }

        public Builder setDescription(String description) {
            periodical.setDescription(description);
            return this;
        }

        public Builder setOneMonthCost(long oneMonthCost) {
            periodical.setOneMonthCost(oneMonthCost);
            return this;
        }

        public Builder setStatus(Status status) {
            periodical.setStatus(status);
            return this;
        }

        public Periodical build() {
            return periodical;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PeriodicalCategory getCategory() {
        return category;
    }

    public void setCategory(PeriodicalCategory category) {
        this.category = category;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getOneMonthCost() {
        return oneMonthCost;
    }

    public void setOneMonthCost(long oneMonthCost) {

        this.oneMonthCost = oneMonthCost;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {

        return String.format("Periodical{id=%d, name='%s', category='%s', publisher='%s', " +
                        " oneMonthCost='%d', status='%s'}", id, name, category, publisher,
                oneMonthCost, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Periodical that = (Periodical) o;

        if (id != that.id) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return publisher != null ? publisher.equals(that.publisher) : that.publisher == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (publisher != null ? publisher.hashCode() : 0);
        return result;
    }
}
