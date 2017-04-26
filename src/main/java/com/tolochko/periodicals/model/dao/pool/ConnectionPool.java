package com.tolochko.periodicals.model.dao.pool;

import com.tolochko.periodicals.model.connection.ConnectionProxy;

public interface ConnectionPool {

    ConnectionProxy getConnection();
}
