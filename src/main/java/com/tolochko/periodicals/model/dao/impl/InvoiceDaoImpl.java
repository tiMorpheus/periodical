package com.tolochko.periodicals.model.dao.impl;

import com.tolochko.periodicals.model.TransactionHelper;
import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.dao.interfaces.InvoiceDao;
import com.tolochko.periodicals.model.dao.util.DaoUtil;
import com.tolochko.periodicals.model.domain.invoice.Invoice;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class InvoiceDaoImpl implements InvoiceDao {
    private static final Logger logger = Logger.getLogger(InvoiceDaoImpl.class);

    private static final String SELECT_ALL_BY_USER_ID = "SELECT * FROM invoices " +
            "JOIN users ON (invoices.user_id = users.id) " +
            "WHERE users.id = ?";

    private static final String SELECT_ALL_BY_PERIODICAL_ID = "SELECT * FROM invoices " +
            "JOIN periodicals ON (invoices.periodical_id = periodicals.id) " +
            "WHERE periodicals.id = ?";

    private static final String SELECT_SUM_BY_CREATION_DATE = "SELECT SUM(total_sum) FROM invoices " +
            "WHERE creation_date >= ? AND creation_date <= ?";

    private static final String SELECT_SUM_BY_PAYMENT_DATE = "SELECT SUM(total_sum) FROM invoices " +
            "WHERE payment_date >= ? AND payment_date <= ? AND status = ?";

    private static final String SELECT_INVOICE_BY_ID = "SELECT * FROM invoices WHERE id = ?";

    private static final String INSERT_INVOICE = "INSERT INTO invoices " +
            "(user_id, periodical_id, period, total_sum, creation_date, payment_date, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_BY_ID = "UPDATE invoices " +
            "SET user_id=?, periodical_id=?, period=?, total_sum=?, creation_date=?, " +
            "payment_date=?, status=? WHERE id=?";

    @Override
    public List<Invoice> findAllByUserId(long userId) {
        try {
            return executeAndGetInvoicesFromRs(SELECT_ALL_BY_USER_ID, userId);

        } catch (SQLException e) {
            String message = String.format("Exception during execution statement '%s' for userId = %d.",
                    SELECT_ALL_BY_USER_ID, userId);
            throw new DaoException(message, e);
        }
    }

    @Override
    public List<Invoice> findAllByPeriodicalId(long periodicalId) {
        try {
            return executeAndGetInvoicesFromRs(SELECT_ALL_BY_PERIODICAL_ID, periodicalId);

        } catch (SQLException e) {
            String message = String.format("Exception during execution statement '%s' for periodicalId = %d.",
                    SELECT_ALL_BY_PERIODICAL_ID, periodicalId);
            throw new DaoException(message, e);
        }
    }

    private List<Invoice> executeAndGetInvoicesFromRs(String query, long periodicalId)
            throws SQLException {

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {
            st.setLong(1, periodicalId);

            try (ResultSet rs = st.executeQuery()) {
                List<Invoice> invoices = new ArrayList<>();

                while (rs.next()) {
                    invoices.add(DaoUtil.getInvoiceFromResultSet(rs));
                }

                return invoices;
            }
        }
    }

    @Override
    public long getCreatedInvoiceSumByCreationDate(Instant since, Instant until) {
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(SELECT_SUM_BY_CREATION_DATE)) {

            st.setTimestamp(1, new Timestamp(since.toEpochMilli()));
            st.setTimestamp(2, new Timestamp(until.toEpochMilli()));


            try (ResultSet rs = st.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            String message = String.format("Exception during execution for since = %s, until = %s ", since, until);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public long getPaidInvoiceSumByPaymentDate(Instant since, Instant until) {
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(SELECT_SUM_BY_PAYMENT_DATE)) {

            st.setTimestamp(1, new Timestamp(since.toEpochMilli()));
            st.setTimestamp(2, new Timestamp(until.toEpochMilli()));
            st.setString(3, Invoice.Status.PAID.name().toLowerCase());

            try (ResultSet rs = st.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            String message = String.format("Exception during execution for since = %s , until = %s", since, until);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public Invoice findOneById(Long id) {
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(SELECT_INVOICE_BY_ID)) {

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? DaoUtil.getInvoiceFromResultSet(rs) : null;
            }

        } catch (SQLException e) {
            String message = String.format("Exception during execution statement '%s' for invoiceId = %d.",
                    SELECT_INVOICE_BY_ID, id);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }


    @Override
    public long add(Invoice invoice) {
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(INSERT_INVOICE)) {

            st.setLong(1, invoice.getUser().getId());
            st.setLong(2, invoice.getPeriodical().getId());
            st.setInt(3, invoice.getSubscriptionPeriod());
            st.setDouble(4, invoice.getTotalSum());
            st.setTimestamp(5, new Timestamp(invoice.getCreationDate().toEpochMilli()));
            st.setTimestamp(6, getPaymentDate(invoice));
            st.setString(7, invoice.getStatus().name().toLowerCase());

            return st.executeUpdate();

        } catch (SQLException e) {
            String message = String.format("Exception during execution statement '%s' for invoice = %s.",
                    INSERT_INVOICE, invoice);
            throw new DaoException(message, e);
        }
    }

    @Override
    public int updateById(Long id, Invoice invoice) {
        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(UPDATE_BY_ID)) {

            st.setLong(1, invoice.getUser().getId());
            st.setLong(2, invoice.getPeriodical().getId());
            st.setInt(3, invoice.getSubscriptionPeriod());
            st.setDouble(4, invoice.getTotalSum());
            st.setTimestamp(5, new Timestamp(invoice.getCreationDate().toEpochMilli()));
            st.setTimestamp(6, getPaymentDate(invoice));
            st.setString(7, invoice.getStatus().name().toLowerCase());
            st.setLong(8, invoice.getId());

            return st.executeUpdate();

        } catch (SQLException e) {
            String message = String.format("Exception during execution statement '%s' for invoice = %s.",
                    UPDATE_BY_ID, invoice);
            throw new DaoException(message, e);
        }
    }

    private Timestamp getPaymentDate(Invoice invoice) {
        Instant paymentDate = invoice.getPaymentDate();
        return nonNull(paymentDate) ? new Timestamp(paymentDate.toEpochMilli()) : null;
    }

    @Override
    public List<Invoice> findAll() {
        throw new UnsupportedOperationException();
    }

}
