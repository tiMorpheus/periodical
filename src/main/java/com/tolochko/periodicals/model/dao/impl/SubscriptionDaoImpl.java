package com.tolochko.periodicals.model.dao.impl;

import com.tolochko.periodicals.model.TransactionHelper;
import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.dao.interfaces.SubscriptionDao;
import com.tolochko.periodicals.model.dao.util.DaoUtil;
import com.tolochko.periodicals.model.domain.subscription.Subscription;
import com.tolochko.periodicals.model.domain.user.User;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDaoImpl implements SubscriptionDao {
    private static final Logger logger = Logger.getLogger(UserDaoImpl.class);


    @Override
    public Subscription findOneByUserIdAndPeriodicalId(long userId, long periodicalId) {
        String query = "SELECT * FROM subscriptions " +
                "WHERE user_id = ? AND periodical_id = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {
            st.setLong(1, userId);
            st.setLong(2, periodicalId);

            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? DaoUtil.getSubscriptionFromRs(rs) : null;
            }

        } catch (SQLException e) {
            String message = String.format("Exception during finding all periodicals for userId = %d, periodicalId = %d",
                    userId, periodicalId);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public List<Subscription> findAllByPeriodicalIdAndStatus(long periodicalId, Subscription.Status status) {
        String query = "SELECT * FROM subscriptions " +
                "JOIN periodicals ON (subscriptions.periodical_id = periodicals.id) " +
                "WHERE periodicals.id = ? AND subscriptions.status = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {
            st.setLong(1, periodicalId);
            st.setString(2, status.name().toLowerCase());

            try (ResultSet rs = st.executeQuery()) {
                List<Subscription> subscriptions = new ArrayList<>();

                while (rs.next()) {
                    subscriptions.add(DaoUtil.getSubscriptionFromRs(rs));
                }

                return subscriptions;
            }

        } catch (SQLException e) {
            String message = String.format("Exception during finding all periodicals for status = %s, periodicalId = %d",
                    status, periodicalId);
            logger.error(message);
            throw new DaoException(message, e);
        }
    }

    @Override
    public List<Subscription> findAllByUser(User user) {
        String query = "SELECT * FROM users " +
                "JOIN subscriptions ON (users.id = subscriptions.user_id) " +
                "JOIN periodicals ON (subscriptions.periodical_id = periodicals.id) " +
                "WHERE users.id = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {
            st.setLong(1, user.getId());

            ResultSet rs = st.executeQuery();

            List<Subscription> subscriptions = new ArrayList<>();

            while (rs.next()) {
                Subscription.Builder subscriptionBuilder = new Subscription.Builder();

                subscriptionBuilder.setId(rs.getLong("subscriptions.id"))
                        .setUser(user)
                        .setPeriodical(DaoUtil.getPeriodicalFromResultSet(rs))
                        .setDeliveryAddress(rs.getString("subscriptions.delivery_address"))
                        .setEndDate(rs.getTimestamp("subscriptions.end_date").toInstant())
                        .setStatus(Subscription.Status.valueOf(
                                rs.getString("subscriptions.status").toUpperCase()));

                subscriptions.add(subscriptionBuilder.build());
            }

            return subscriptions;

        } catch (SQLException e) {
            String message = String.format("Exception during retrieving subscriptions for a user: %s.", user);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public long add(Subscription subscription) {
        String query = "INSERT INTO subscriptions " +
                "(user_id, periodical_id, delivery_address, end_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {

            st.setLong(1, subscription.getUser().getId());
            st.setLong(2, subscription.getPeriodical().getId());
            st.setString(3, subscription.getDeliveryAddress());
            st.setTimestamp(4, new Timestamp(subscription.getEndDate().toEpochMilli()));
            st.setString(5, subscription.getStatus().name().toLowerCase());

            return st.executeUpdate();

        } catch (SQLException e) {
            String message = String.format("Exception during creating a subscription %s.", subscription);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public int updateById(Long id, Subscription subscription) {
        String query = "UPDATE subscriptions " +
                "SET user_id=?, periodical_id=?, delivery_address=?, end_date=?, status=? " +
                "WHERE id=?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {

            st.setLong(1, subscription.getUser().getId());
            st.setLong(2, subscription.getPeriodical().getId());
            st.setString(3, subscription.getDeliveryAddress());
            st.setTimestamp(4, new Timestamp(subscription.getEndDate().toEpochMilli()));
            st.setString(5, subscription.getStatus().name().toLowerCase());
            st.setLong(6, id);

            return st.executeUpdate();

        } catch (SQLException e) {
            String message = String.format("Exception during updating %s.", subscription);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }


    @Override
    public Subscription findOneById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Subscription> findAll() {
        throw new UnsupportedOperationException();
    }

}
