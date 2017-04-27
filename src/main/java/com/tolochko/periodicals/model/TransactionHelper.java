package com.tolochko.periodicals.model;

import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.dao.exception.TransactionException;
import com.tolochko.periodicals.model.dao.pool.ConnectionPoolProvider;
import org.apache.log4j.Logger;

public class TransactionHelper {
    private static final Logger logger = Logger.getLogger(TransactionHelper.class);
    private final static ThreadLocal<ConnectionProxy> connections = new ThreadLocal<>();

    public static ConnectionProxy getConnectionProxy() {
        ConnectionProxy connection = connections.get();

        if (connection == null) {
            connection = ConnectionPoolProvider
                    .getInstance()
                    .getPool()
                    .getConnection();
        }

        return connection;
    }

    public static void beginTransaction() {
        try {
            ConnectionProxy connection = ConnectionPoolProvider
                    .getInstance()
                    .getPool()
                    .getConnection();

            connections.set(connection);
            connection.beginTransaction();
        } catch (RuntimeException e){
            logger.error("Exception during beginning transaction");
            throw new TransactionException(e);
        }
    }

    public static void rollback() {
        if (connections.get() == null){
            throw new DaoException("can't rollback without beginTransaction");
        }
        logger.debug("transaction rollback");
        connections.get().rollbackTransaction();
        connections.remove();
    }

    public static void commit() {
        if (connections.get() == null){
            throw new DaoException("can't commit without beginTransaction");
        }
        logger.debug("commit");
        connections.get().commitTransaction();
        connections.remove();
    }

}
