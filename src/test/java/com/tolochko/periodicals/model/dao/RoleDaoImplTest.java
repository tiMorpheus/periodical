package com.tolochko.periodicals.model.dao;

import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.factory.DaoFactory;
import com.tolochko.periodicals.model.dao.factory.impl.MySqlDaoFactory;
import com.tolochko.periodicals.model.dao.interfaces.RoleDao;
import com.tolochko.periodicals.model.domain.user.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;

import static java.util.Objects.nonNull;
import static org.junit.Assert.assertEquals;

public class RoleDaoImplTest {

    private static final long ADMIN_ID = 1;
    private static RoleDao roleDao;
    private static DaoFactory factory;
    private static User.Role expectedAdmin;
    private static User.Role expectedSubscriber;

    @BeforeClass
    public static void setUp() throws Exception {

        factory = MySqlDaoFactory.getFactoryInstance();
        roleDao = factory.getRoleDao();

        expectedAdmin = User.Role.ADMIN;
        expectedSubscriber = User.Role.SUBSCRIBER;
    }

    @Ignore
    public void findRoleByUserName_Should_ReturnCorrectRole(){
        assertEquals(expectedAdmin, roleDao.findRoleByUserName("admin"));

        assertEquals(expectedSubscriber, roleDao.findRoleByUserName("user1"));
    }

}
