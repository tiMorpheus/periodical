package com.tolochko.periodicals.model.dao;

import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.factory.DaoFactory;
import com.tolochko.periodicals.model.dao.factory.impl.MySqlDaoFactory;
import com.tolochko.periodicals.model.dao.interfaces.UserDao;
import com.tolochko.periodicals.model.domain.user.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;

import static java.util.Objects.nonNull;
import static org.junit.Assert.*;

public class UserDaoImplTest {
    private static final long ADMIN_ID = 1;
    private static UserDao userDao;
    private static ConnectionProxy conn;
    private static DaoFactory factory;
    private static User expected;

    @BeforeClass
    public static void setUp() throws Exception {

        factory = MySqlDaoFactory.getFactoryInstance();
        userDao = factory.getUserDao();

        User.Builder userBuilder = new User.Builder();
        userBuilder.setUsername("admin")
                .setFirstName("Tymur")
                .setLastName("Tolochko")
                .setAddress("address")
                .setEmail("admin@gmail.com")
                .setStatus(User.Status.ACTIVE);

        expected = userBuilder.build();
    }

    @Test
    public void findOneById_Should_ReturnCorrectUser() throws Exception {
        assertUserData(userDao.findOneById(ADMIN_ID));
    }

    private void assertUserData(User actual) {
        assertEquals("UserName", expected.getUsername(), actual.getUsername());
        assertEquals("FirstName", expected.getFirstName(), actual.getFirstName());
        assertEquals("LastName", expected.getLastName(), actual.getLastName());
        assertEquals("Email", expected.getEmail(), actual.getEmail());
        assertEquals("Status", expected.getStatus(), actual.getStatus());
    }

    @Test
    public void findUserByUserName_Should_ReturnCorrectUser() {
        assertUserData(userDao.findOneByUserName("admin"));
    }

    @Test
    public void findUserByUserName_Should_ReturnNull() {
        assertNull(userDao.findOneByUserName("admin2"));
    }

    @Test
    public void isEmailExistsInDb_Should_ReturnTrue(){

        assertTrue(userDao.emailExistsInDb(expected.getEmail()));
    }

    @Test
    public void isEmailExistsInDb_Should_ReturnFalse(){

        assertFalse(userDao.emailExistsInDb(null));
    }

    @Ignore
    public void updateUser_Should_setNewData_in_Db(){
        String password = "password" + Math.random();

        expected.setPassword(password);

        userDao.updateById(ADMIN_ID, expected);

        assertEquals(password, userDao.findOneById(ADMIN_ID).getPassword());

    }

    @AfterClass
    public static void tearDown() throws SQLException {
        if (nonNull(conn)) {
            conn.close();
        }
    }
}
