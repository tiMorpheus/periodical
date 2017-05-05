package com.tolochko.periodicals.model.dao.impl;

import com.tolochko.periodicals.model.TransactionHelper;
import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.dao.interfaces.PeriodicalDao;
import com.tolochko.periodicals.model.dao.util.DaoUtil;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.periodical.PeriodicalCategory;
import com.tolochko.periodicals.model.domain.subscription.Subscription;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PeriodicalDaoImpl implements PeriodicalDao {
    private static final Logger logger = Logger.getLogger(UserDaoImpl.class);

    @Override
    public Periodical findOneByName(String name) {
        String query = "SELECT * FROM periodicals WHERE name = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? DaoUtil.getPeriodicalFromResultSet(rs) : null;
            }
        } catch (SQLException e) {
            String message =
                    String.format("Exception during retrieving a periodical with %s = %s. ", name, "periodicals.name");
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public Periodical findOneById(Long id) {
        String query = "SELECT * FROM periodicals WHERE id = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? DaoUtil.getPeriodicalFromResultSet(rs) : null;
            }

        } catch (SQLException e) {
            String message =
                    String.format("Exception during retrieving a periodical with %s = %s. ", id, "periodicals.id");
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public List<Periodical> findAll() {
        String query = "SELECT * FROM periodicals";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {
            ResultSet rs = st.executeQuery();

            List<Periodical> periodicals = new ArrayList<>();

            while (rs.next()) {
                Periodical periodical = DaoUtil.getPeriodicalFromResultSet(rs);

                periodicals.add(periodical);
            }

            return periodicals;

        } catch (SQLException e) {
            String message = "Exception during retrieving all periodicals.";
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public List<Periodical> findAllByStatus(Periodical.Status status) {
        String query = "SELECT * FROM periodicals WHERE status = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, status.name().toLowerCase());

            ResultSet rs = st.executeQuery();

            List<Periodical> periodicals = new ArrayList<>();
            while (rs.next()) {
                Periodical periodical = DaoUtil.getPeriodicalFromResultSet(rs);

                periodicals.add(periodical);
            }

            return periodicals;

        } catch (SQLException e) {
            String message = String.format("Exception during retrieving periodicals with status '%s'.", status);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public int findNumberOfPeriodicalsWithCategoryAndStatus(PeriodicalCategory category, Periodical.Status status) {
        String query = "SELECT COUNT(id) FROM periodicals " +
                "WHERE category = ? AND status = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, category.name().toLowerCase());
            st.setString(2, status.name().toLowerCase());

            try (ResultSet rs = st.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            String message =
                    String.format("Exception during getting number of periodicals with category = '%s' and status = '%s'.",
                            category, status);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public long add(Periodical periodical) {
        String query = "INSERT INTO periodicals " +
                "(name, category, publisher, description, one_month_cost, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {

            st.setString(1, periodical.getName());
            st.setString(2, periodical.getCategory().name().toLowerCase());
            st.setString(3, periodical.getPublisher());
            st.setString(4, periodical.getDescription());
            st.setLong(5, periodical.getOneMonthCost());
            st.setString(6, periodical.getStatus().name().toLowerCase());

            return st.executeUpdate();

        } catch (SQLException e) {
            String message = String.format("Exception during inserting %s into 'periodicals'.", periodical);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public int updateById(Long id, Periodical periodical) {
        String query = "UPDATE periodicals " +
                "SET name=?, category=?, publisher=?, description=?, one_month_cost=?, status=? " +
                "WHERE id=?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, periodical.getName());
            st.setString(2, periodical.getCategory().name().toLowerCase());
            st.setString(3, periodical.getPublisher());
            st.setString(4, periodical.getDescription());
            st.setLong(5, periodical.getOneMonthCost());
            st.setString(6, periodical.getStatus().name().toLowerCase());
            st.setLong(7, periodical.getId());

            return st.executeUpdate();

        } catch (SQLException e) {
            String message = String.format("Exception during updating %s.", periodical);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public int updateAndSetDiscarded(Periodical periodical) {
        String query = "UPDATE periodicals AS p " +
                "SET name=?, category=?, publisher=?, description=?, one_month_cost=?, status=? " +
                "WHERE id=? AND 0 = (SELECT count(*) FROM subscriptions AS s " +
                "WHERE s.periodical_id = p.id AND s.status = ?)";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {

            st.setString(1, periodical.getName());
            st.setString(2, periodical.getCategory().name().toLowerCase());
            st.setString(3, periodical.getPublisher());
            st.setString(4, periodical.getDescription());
            st.setLong(5, periodical.getOneMonthCost());
            st.setString(6, periodical.getStatus().name().toLowerCase());
            st.setLong(7, periodical.getId());
            st.setString(8, Subscription.Status.ACTIVE.name().toLowerCase());

            return st.executeUpdate();

        } catch (SQLException e) {
            String message = String.format("Exception during updating %s.", periodical);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public int deleteAllDiscarded() {
        String query = "DELETE FROM periodicals " +
                "WHERE status = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, Periodical.Status.DISCARDED.name());

            return st.executeUpdate();

        } catch (SQLException e) {
            String message = "Exception during deleting discarded periodicals.";
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public int addIntoArchive(Periodical periodical) {
        String query = "INSERT INTO periodical_archive " +
                "(name, category, publisher, description, one_month_cost, delete_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";


        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {

            logger.debug("adding into archive");
            st.setString(1, periodical.getName());
            st.setString(2, periodical.getCategory().name().toLowerCase());
            st.setString(3, periodical.getPublisher());
            st.setString(4, periodical.getDescription());
            st.setLong(5, periodical.getOneMonthCost());
            st.setTimestamp(6, new Timestamp(Instant.now().toEpochMilli()));

            return st.executeUpdate();

        } catch (SQLException e) {
            String message = String.format("Exception during inserting %s into archive.", periodical);
            logger.error(message, e);
            throw new DaoException(message, e);
        }

    }
}
