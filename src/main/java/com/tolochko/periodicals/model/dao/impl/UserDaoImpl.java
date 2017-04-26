package com.tolochko.periodicals.model.dao.impl;

import com.tolochko.periodicals.model.TransactionHelper;
import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.dao.interfaces.UserDao;
import com.tolochko.periodicals.model.dao.util.DaoUtil;
import com.tolochko.periodicals.model.domain.user.User;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    private static final Logger logger = Logger.getLogger(UserDaoImpl.class);


    @Override
    public User findOneByUserName(String userName) {
        String query = "SELECT * FROM users WHERE username = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? DaoUtil.createUserFromResultSet(rs) : null;
            }

        } catch (SQLException e) {
            String message = String.format("Exception during finding a user with username = %s", userName);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public boolean emailExistsInDb(String email) {
        String query = "SELECT COUNT(id) FROM users " +
                "WHERE users.email = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, email);

            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            logger.error(e);
            throw new DaoException(e);
        }
    }


    @Override
    public User findOneById(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? DaoUtil.createUserFromResultSet(rs) : null;
            }

        } catch (SQLException e) {
            String message = String.format("Exception during finding a user with id = %s", id);
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM users";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st = connection.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(DaoUtil.createUserFromResultSet(rs));
            }
            return users;
        } catch (SQLException e) {
            String message = "Exception during finding all users.";
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }


    @Override
    public long add(User user) {
        String errorMessage = String.format("Exception during creating a new user: %s", user);
        String errorMessageNoRows = String.format("Creating user (%s) failed, no rows affected.", user);

        String sqlStatement = "INSERT INTO users " +
                "(username, first_name, last_name, email, address, password, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        ConnectionProxy connection = TransactionHelper.getConnectionProxy();
        try (
             PreparedStatement st = connection
                     .prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, user.getUsername());
            st.setString(2, user.getFirstName());
            st.setString(3, user.getLastName());
            st.setString(4, user.getEmail());
            st.setString(5, user.getAddress());
            st.setString(6, user.getPassword());
            st.setString(7, user.getStatus().name().toLowerCase());

            tryExecuteUpdate(st, errorMessage);

            return tryRetrieveId(st, errorMessageNoRows);

        } catch (SQLException e) {
            logger.error(errorMessage, e);
        }

        return 0;
    }

    private void tryExecuteUpdate(PreparedStatement st, String errorMessage) throws SQLException {
        int affectedRows = st.executeUpdate();
        if (affectedRows == 0) {
            logger.error(errorMessage);
            throw new DaoException(errorMessage);
        }
    }

    private long tryRetrieveId(PreparedStatement st, String errorMessageNoRows) throws SQLException {
        try (ResultSet generatedKeys = st.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                logger.error(errorMessageNoRows);
                throw new DaoException(errorMessageNoRows);
            }
        }
    }

    @Override
    public int updateById(Long id, User user) {
        String query = "UPDATE users " +
                "SET " +
                "username = ?,first_name = ?, last_name = ?,email = ?, address = ?, password = ?, status = ? " +
                "WHERE id = ?";

        try (ConnectionProxy connection = TransactionHelper.getConnectionProxy();
             PreparedStatement st =
                     connection.prepareStatement(query)) {

            st.setString(1, user.getUsername());
            st.setString(2, user.getFirstName());
            st.setString(3, user.getLastName());
            st.setString(4, user.getEmail());
            st.setString(5, user.getAddress());
            st.setString(6, user.getPassword());
            st.setString(7, user.getStatus().name().toLowerCase());
            st.setLong(8, id);

            return st.executeUpdate();
        } catch (SQLException e) {
            String message = "Exception during update user id: " + id;
            logger.error(message, e);
            throw new DaoException(message, e);
        }
    }
}
