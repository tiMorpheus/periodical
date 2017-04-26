package com.tolochko.periodicals.model.dao.impl;

import com.tolochko.periodicals.model.TransactionHelper;
import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.dao.interfaces.InvoiceDao;
import com.tolochko.periodicals.model.dao.util.DaoUtil;
import com.tolochko.periodicals.model.domain.invoice.Invoice;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class InvoiceDaoImpl implements InvoiceDao {
    private static final Logger logger = Logger.getLogger(InvoiceDaoImpl.class);

    @Override
    public List<Invoice> findAllByUserId(long userId) {
        String query = "SELECT * FROM invoices " +
                "JOIN users ON (invoices.user_id = users.id) " +
                "WHERE users.id = ?";

        try {
            return executeAndGetInvoicesFromRs(query, userId);

        } catch (SQLException e) {
            String message = String.format("Exception during execution statement '%s' for userId = %d.",
                    query, userId);
            throw new DaoException(message, e);
        }
    }

    @Override
    public List<Invoice> findAllByPeriodicalId(long periodicalId) {
        String query = "SELECT * FROM invoices " +
                "JOIN periodicals ON (invoices.periodical_id = periodicals.id) " +
                "WHERE periodicals.id = ?";

        try {
            return executeAndGetInvoicesFromRs(query, periodicalId);

        } catch (SQLException e) {
            String message = String.format("Exception during execution statement '%s' for periodicalId = %d.",
                    query, periodicalId);
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
        String query = "SELECT SUM(total_sum) FROM invoices " +
                "WHERE creation_date >= ? AND creation_date <= ?";

        try (ConnectionProxy connection =TransactionHelper.getConnectionProxy();
                PreparedStatement st = connection.prepareStatement(query)) {
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
        String query = "SELECT SUM(total_sum) FROM invoices " +
                "WHERE payment_date >= ? AND payment_date <= ? AND status = ?";

        try (ConnectionProxy connection =TransactionHelper.getConnectionProxy();
                PreparedStatement st = connection.prepareStatement(query)) {

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
        String query = "SELECT * FROM invoices WHERE id = ?";

        try (ConnectionProxy connection =TransactionHelper.getConnectionProxy();
                PreparedStatement st = connection.prepareStatement(query)) {
            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? DaoUtil.getInvoiceFromResultSet(rs) : null;
            }

        } catch (SQLException e) {
            String message = String.format("Exception during execution statement '%s' for invoiceId = %d.",
                    query, id);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }


    @Override
    public long add(Invoice invoice) {
        String query = "INSERT INTO invoices " +
                "(user_id, periodical_id, period, total_sum, creation_date, payment_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (ConnectionProxy connection =TransactionHelper.getConnectionProxy();
                PreparedStatement st = connection.prepareStatement(query)) {

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
                    query, invoice);
            throw new DaoException(message, e);
        }
    }

    @Override
    public int updateById(Long id, Invoice invoice) {
        String query = "UPDATE invoices " +
                "SET user_id=?, periodical_id=?, period=?, total_sum=?, creation_date=?, " +
                "payment_date=?, status=? WHERE id=?";

        try (ConnectionProxy connection =TransactionHelper.getConnectionProxy();
                PreparedStatement st = connection.prepareStatement(query)) {

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
                    query, invoice);
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
