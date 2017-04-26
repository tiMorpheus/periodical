package com.tolochko.periodicals.model;

import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.dao.pool.ConnectionPool;
import com.tolochko.periodicals.model.dao.pool.ConnectionPoolProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.tolochko.periodicals.model.dao.pool.ConnectionPoolProvider.createPoolFromProperties;
import static java.util.Objects.isNull;

public class InitDb {

    private static ConnectionPool TEST_INSTANCE;
    /**
     * @return a connection pool for using in tests.
     */
    public static ConnectionPool getTestPool() {

        if (isNull(TEST_INSTANCE)) {
            try {
                InputStream input = ConnectionPoolProvider.class.getClassLoader()
                        .getResourceAsStream("database.properties");
                Properties testProperties = new Properties();
                testProperties.load(input);

                TEST_INSTANCE = createPoolFromProperties(testProperties);

            } catch (FileNotFoundException e) {
                throw new DaoException(e);
            } catch (IOException e) {
                throw new DaoException(e);
            }
        }

        return TEST_INSTANCE;
    }
}
