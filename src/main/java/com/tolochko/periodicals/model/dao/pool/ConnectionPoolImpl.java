package com.tolochko.periodicals.model.dao.pool;

import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.connection.ConnectionProxyImpl;
import com.tolochko.periodicals.model.dao.exception.DaoException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolImpl implements ConnectionPool {
    private static final Logger logger = Logger.getLogger(ConnectionPoolImpl.class);
    private static final String NAME_DEFAULT = "root";
    private static final String PASS_DEFAULT = "root";
    private static final String DRIVER_NAME_DEFAULT = "com.mysql.cj.jdbc.Driver";
    private static final int MAX_TOTAL_CONNECTIONS = 10;

    private BasicDataSource dataSource;

    public ConnectionPoolImpl(Builder builder) {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(builder.driverClassName);
        dataSource.setUrl(builder.url);
        dataSource.setUsername(builder.userName);
        dataSource.setPassword(builder.password);
        dataSource.setMaxTotal(builder.maxConnections);
    }

    @Override
    public ConnectionProxy getConnection() {
        Connection connection;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Cannot create connection from data source", e);
            throw new DaoException("Cannot create connection from data source", e);
        }

        return new ConnectionProxyImpl(connection);
    }

    public static Builder getBuilder(String url) {
        return new Builder(url);
    }

    public static class Builder {
        private String driverClassName;
        private String url;
        private String userName;
        private String password;
        private int maxConnections;

        private Builder(String url) {
            checkNotNull(url, "url should not be null.");
            this.url = url;
            this.driverClassName = DRIVER_NAME_DEFAULT;
            this.userName = NAME_DEFAULT;
            this.password = PASS_DEFAULT;
            this.maxConnections = MAX_TOTAL_CONNECTIONS;
        }

        public Builder setDriverClassName(String driverClassName) {
            checkNotNull(driverClassName, "driverClassName should not be null.");
            this.driverClassName = driverClassName;
            return this;
        }

        public Builder setUserName(String userName) {
            checkNotNull(userName, "userName should not be null.");
            this.userName = userName;
            return this;
        }

        public Builder setPassword(String password) {
            checkNotNull(password, "password should not be null.");
            this.password = password;
            return this;
        }

        public Builder setMaxConnections(int maxConnections) {
            checkArgument(maxConnections > 0, "maxConnections should be a positive number.");
            this.maxConnections = maxConnections;
            return this;
        }


        public static void checkArgument(boolean expression, Object errorMessage) {
            if (!expression) {
                logger.error(errorMessage);
                throw new IllegalArgumentException(String.valueOf(errorMessage));
            }
        }

        public static <T> T checkNotNull(T reference, Object errorMessage) {
            if (reference == null) {
                logger.error(errorMessage);
                throw new NullPointerException(String.valueOf(errorMessage));
            } else {
                return reference;
            }
        }

        public ConnectionPool build() {
            return new ConnectionPoolImpl(this);
        }
    }
}
