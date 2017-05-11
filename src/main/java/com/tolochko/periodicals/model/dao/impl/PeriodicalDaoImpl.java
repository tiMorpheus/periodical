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

    private static final String SELECT_PERIODICAL_BY_NAME = "SELECT * FROM periodicals WHERE name = ?";

    private static final String SELECT_PERIODICAL_BY_STATUS = "SELECT * FROM periodicals WHERE status = ?";

    private static final String SELECT_PERIODICAL_BY_ID = "SELECT * FROM periodicals WHERE id = ?";

    private static final String SELECT_ALL_PERIODICAL = "SELECT * FROM periodicals";

    private static final String FIND_COUNT_OF_PERIODICAL = "SELECT COUNT(id) FROM periodicals " +
            "WHERE category = ? AND status = ?";

    private static final String INSERT_PERIODICAL = "INSERT INTO periodicals " +
            "(name, category, publisher, description, one_month_cost, status) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_PERIODICAL_BY_ID = "UPDATE periodicals " +
            "SET name=?, category=?, publisher=?, description=?, one_month_cost=?, status=? " +
            "WHERE id=?";

    private static final String UPDATE_PERIODICAL_AND_SET_DISCARDED = "UPDATE periodicals AS p " +
            "SET name=?, category=?, publisher=?, description=?, one_month_cost=?, status=? " +
            "WHERE id=? AND 0 = (SELECT count(*) FROM subscriptions AS s " +
            "WHERE s.periodical_id = p.id AND s.status = ?)";

    private static final String DELETE_PERIODICAL = "DELETE FROM periodicals " +
            "WHERE status = ?";

    private static final String INSERT_INTO_ARCHIVE = "INSERT INTO periodical_archive " +
            "(name, category, publisher, description, one_month_cost, delete_date) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    @Override
    public Periodical findOneByName(String name) {
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement ps = connection.prepareStatement(SELECT_PERIODICAL_BY_NAME)) {

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
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement ps = connection.prepareStatement(SELECT_PERIODICAL_BY_ID)) {

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
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(SELECT_ALL_PERIODICAL)) {

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
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(SELECT_PERIODICAL_BY_STATUS)) {

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
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(FIND_COUNT_OF_PERIODICAL)) {

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
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(INSERT_PERIODICAL)) {

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
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(UPDATE_PERIODICAL_BY_ID)) {

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
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(UPDATE_PERIODICAL_AND_SET_DISCARDED)) {

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
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(DELETE_PERIODICAL)) {

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
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(INSERT_INTO_ARCHIVE)) {

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
