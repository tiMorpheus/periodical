package com.tolochko.periodicals.model.connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ConnectionProxy extends AutoCloseable {

    /**
     * Defines begin of transaction.
     */
    void beginTransaction();

    /**
     * Saves transaction.
     */
    void commitTransaction();

    /**
     * rolls back transaction.
     */
    void rollbackTransaction();

    /**
     * Closes connection.
     */
    @Override
    void close();

    PreparedStatement prepareStatement(String query) throws SQLException;

    PreparedStatement prepareStatement(String query, int i) throws SQLException;
}
