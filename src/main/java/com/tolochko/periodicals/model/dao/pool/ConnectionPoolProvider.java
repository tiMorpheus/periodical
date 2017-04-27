package com.tolochko.periodicals.model.dao.pool;

import com.tolochko.periodicals.model.dao.exception.DaoException;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionPoolProvider {
    private static final Logger logger = Logger.getLogger(ConnectionPoolProvider.class);

    private static ConnectionPoolProvider poolProvider = new ConnectionPoolProvider();

    private  ConnectionPool instance;

    private ConnectionPoolProvider(){
        InputStream input;
        Properties properties = new Properties();
        try {

            input = ConnectionPoolProvider.class.getClassLoader()
                    .getResourceAsStream("config/database.properties");
            properties.load(input);

            String url = properties.getProperty("database.url");
            String userName = properties.getProperty("database.username");
            String userPassword = properties.getProperty("database.password");
            int maxConnNumber = Integer.parseInt(properties.getProperty("database.maxconnections"));

            instance = ConnectionPoolImpl.getBuilder(url)
                    .setUserName(userName)
                    .setPassword(userPassword)
                    .setMaxConnections(maxConnNumber)
                    .build();

        } catch (FileNotFoundException e) {
            logger.error("Exception during opening the db-config", e);
            throw new DaoException(e);
        } catch (IOException e) {
            logger.error("Exception during loading db-config properties", e);
            throw new DaoException(e);
        }
    }

    public ConnectionPool getPool() {
        return instance;
    }

    public static ConnectionPoolProvider getInstance() {
        return poolProvider;
    }
}
