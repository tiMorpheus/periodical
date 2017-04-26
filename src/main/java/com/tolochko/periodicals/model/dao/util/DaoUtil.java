package com.tolochko.periodicals.model.dao.util;

import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.domain.invoice.Invoice;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.periodical.PeriodicalCategory;
import com.tolochko.periodicals.model.domain.subscription.Subscription;
import com.tolochko.periodicals.model.domain.user.User;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import static java.util.Objects.nonNull;

public final class DaoUtil {
    private static final Logger logger = Logger.getLogger(DaoUtil.class);
    private DaoUtil(){}

    /**
     * Creates a new user using the data from the result set.
     */
    public static User createUserFromResultSet(ResultSet rs){
        User.Builder userBuilder = null;

        try {
            userBuilder = new User.Builder()
                    .setId(rs.getLong("id"))
                    .setUsername(rs.getString("username"))
                    .setFirstName(rs.getString("first_name"))
                    .setLastName(rs.getString("last_name"))
                    .setEmail(rs.getString("email"))
                    .setAddress(rs.getString("address"))
                    .setPassword(rs.getString("password"))
                    .setStatus(User.Status.valueOf(rs.getString("status").toUpperCase()));

            return userBuilder.build();
        } catch (SQLException e){
            logger.error("caught exception during creating user", e);
        }

        return null;
    }

    /**
     * Creates a new periodical using the data from the result set.
     */
    public static Periodical getPeriodicalFromResultSet(ResultSet rs)  {
        Periodical.Builder periodicalBuilder;

        try {
            periodicalBuilder = new Periodical.Builder()
                    .setId(rs.getLong("periodicals.id"))
                    .setName(rs.getString("periodicals.name"))
                    .setCategory(PeriodicalCategory.valueOf(
                            rs.getString("periodicals.category").toUpperCase()))
                    .setPublisher(rs.getString("periodicals.publisher"))
                    .setDescription(rs.getString("periodicals.description"))
                    .setOneMonthCost(rs.getLong("periodicals.one_month_cost"))
                    .setStatus(Periodical.Status.valueOf(
                            rs.getString("periodicals.status").toUpperCase()));

            return periodicalBuilder.build();
        } catch (SQLException e) {
            logger.error("I've caught error during creating periodical for result set", e);
            throw new DaoException(e);
        }
    }

    /**
     * Creates a new Subscription using the data from the result set.
     */
    public static Subscription getSubscriptionFromRs(ResultSet rs){

        try {
            User.Builder userBuilder = new User.Builder();
            userBuilder.setId(rs.getLong("subscriptions.user_id"));

            Periodical.Builder periodicalBuilder = new Periodical.Builder();
            periodicalBuilder.setId(rs.getLong("subscriptions.periodical_id"));

            Subscription.Builder subscriptionBuilder = new Subscription.Builder();
            subscriptionBuilder.setId(rs.getLong("subscriptions.id"))
                    .setUser(userBuilder.build())
                    .setPeriodical(periodicalBuilder.build())
                    .setDeliveryAddress(rs.getString("subscriptions.delivery_address"))
                    .setEndDate(rs.getTimestamp("subscriptions.end_date").toInstant())
                    .setStatus(Subscription.Status.valueOf(
                            rs.getString("subscriptions.status").toUpperCase()));

            return subscriptionBuilder.build();
        } catch (SQLException e) {
            logger.error("I've caught error during creating subscription for result set", e);
            throw new DaoException(e);
        }
    }

    /**
     * Creates a new invoice using the data from the result set.
     */
    public static Invoice getInvoiceFromResultSet(ResultSet rs) {

        try {
            User.Builder userBuilder = new User.Builder();
            userBuilder.setId(rs.getLong("invoices.user_id"));

            Periodical periodical = new Periodical();
            periodical.setId(rs.getLong("invoices.periodical_id"));

            Invoice.Builder invoiceBuilder = new Invoice.Builder();
            invoiceBuilder.setId(rs.getLong("invoices.id"))
                    .setUser(userBuilder.build())
                    .setPeriodical(periodical)
                    .setSubscriptionPeriod(rs.getInt("invoices.period"))
                    .setTotalSum(rs.getLong("invoices.total_sum"))
                    .setCreationDate(getCreationDateFromResults(rs))
                    .setPaymentDate(getPaymentDateFromResults(rs))
                    .setStatus(Invoice.Status.valueOf(rs.getString("invoices.status").toUpperCase()));

            return invoiceBuilder.build();
        }catch (SQLException e){
            logger.error("I've caught error during creating invoice for result set", e);
            throw new DaoException(e);
        }
    }

    private static Instant getCreationDateFromResults(ResultSet rs) throws SQLException {
        return Instant.ofEpochMilli(rs.getTimestamp("invoices.creation_date").getTime());
    }

    private static Instant getPaymentDateFromResults(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("invoices.payment_date");
        return nonNull(timestamp) ? Instant.ofEpochMilli(timestamp.getTime()) : null;
    }

}
