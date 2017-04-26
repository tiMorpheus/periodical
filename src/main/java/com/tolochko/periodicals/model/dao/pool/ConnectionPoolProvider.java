package com.tolochko.periodicals.model.dao.pool;

import com.tolochko.periodicals.model.dao.exception.DaoException;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionPoolProvider {
    private static final Logger logger = Logger.getLogger(ConnectionPoolProvider.class);

    private static ConnectionPool instance;

    static {
        InputStream input;
        Properties properties = new Properties();
        try {

            input = ConnectionPoolProvider.class.getClassLoader()
                    .getResourceAsStream("config/database.properties");
            properties.load(input);
            instance = createPoolFromProperties(properties);

        } catch (FileNotFoundException e) {
            logger.error("Exception during opening the db-config", e);
            throw new DaoException(e);
        } catch (IOException e) {
            logger.error("Exception during loading db-config properties", e);
            throw new DaoException(e);
        }
    }

    private ConnectionPoolProvider(){}

    public static ConnectionPool createPoolFromProperties(Properties properties) {
        String url = properties.getProperty("database.url");
        String userName = properties.getProperty("database.username");
        String userPassword = properties.getProperty("database.password");
        int maxConnNumber = Integer.parseInt(properties.getProperty("database.maxconnections"));


        return ConnectionPoolImpl.getBuilder(url)
                .setUserName(userName)
                .setPassword(userPassword)
                .setMaxConnections(maxConnNumber)
                .build();
    }

    public static ConnectionPool getPool() {
        if (instance == null) {
            throw new IllegalStateException("Connection manager hasn't initialized yet!");
        }
        return instance;
    }

}
