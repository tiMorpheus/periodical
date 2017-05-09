package com.tolochko.periodicals.model.dao;

import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.factory.DaoFactory;
import com.tolochko.periodicals.model.dao.factory.impl.MySqlDaoFactory;
import com.tolochko.periodicals.model.dao.interfaces.PeriodicalDao;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.periodical.PeriodicalCategory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;

import static java.util.Objects.nonNull;
import static org.junit.Assert.assertEquals;

public class PeriodicalDaoImplTest {
    private static final long PERIODICAL_ID = 7;
    private static final String PERIODICAL_NAME = "Periodical1";
    private static final int ONE_MONTH_COST = 5;
    private static PeriodicalDao periodicalDao;
    private static ConnectionProxy conn;
    private static DaoFactory factory;
    private static Periodical expected;

    @BeforeClass
    public static void setUp() throws Exception {

        factory = MySqlDaoFactory.getFactoryInstance();
        periodicalDao = factory.getPeriodicalDao();

        Periodical.Builder periodicalBuilder = new Periodical.Builder();
        periodicalBuilder.setName(PERIODICAL_NAME)
                .setCategory(PeriodicalCategory.SPORTS)
                .setOneMonthCost(ONE_MONTH_COST)
                .setStatus(Periodical.Status.ACTIVE);

        expected = periodicalBuilder.build();
    }

    @Ignore
    public void findOneById_Should_ReturnOnePeriodical() throws Exception {
        assertPeriodicalData(expected, periodicalDao.findOneById(PERIODICAL_ID));
    }

    private void assertPeriodicalData(Periodical expected, Periodical actual) {
        assertEquals("Name", expected.getName(), actual.getName());
        assertEquals("Category", expected.getCategory(), actual.getCategory());
        assertEquals("OneMonthCost", expected.getOneMonthCost(), actual.getOneMonthCost());
        assertEquals("Status", expected.getStatus(), actual.getStatus());
    }

    @Ignore
    public void findOneByName_Should_ReturnOnePeriodical() throws Exception {
        assertPeriodicalData(expected, periodicalDao.findOneByName(PERIODICAL_NAME));
    }

    @Ignore
    public void findAll_Should_ReturnCorrectValues() throws Exception {
        int expectedNumber = 6;
        int actualNumber = periodicalDao.findAll().size();

        assertEquals(expectedNumber, actualNumber);
    }

    @Ignore
    public void findAllByStatus_Should_ReturnCorrectValues() throws Exception {
        int activeExpectedNumber = 8;
        int inActiveExpectedNumber = 2;
        int discardedExpectedNumber = 0;

        int activeActualNumber = periodicalDao.findAllByStatus(Periodical.Status.ACTIVE).size();
        int inActiveActualNumber = periodicalDao.findAllByStatus(Periodical.Status.INACTIVE).size();
        int discardedActualNumber = periodicalDao.findAllByStatus(Periodical.Status.DISCARDED).size();

        assertEquals("active periodicals", activeExpectedNumber, activeActualNumber);
        assertEquals("inActive periodicals", inActiveExpectedNumber, inActiveActualNumber);
        assertEquals("discarded periodicals", discardedExpectedNumber, discardedActualNumber);
    }

    @Ignore
    public void findNumberOfPeriodicalsWithCategoryAndStatus_Should_ReturnCorrectValues() throws Exception {
        int activeNewsExpectedNumber = 0;
        int inActiveNewsExpectedNumber = 1;
        int discardedNewsExpectedNumber = 0;

        int activeNewsActualNumber = periodicalDao.findNumberOfPeriodicalsWithCategoryAndStatus(
                PeriodicalCategory.NEWS, Periodical.Status.ACTIVE);
        int inActiveNewsActualNumber = periodicalDao.findNumberOfPeriodicalsWithCategoryAndStatus(
                PeriodicalCategory.NEWS, Periodical.Status.INACTIVE);
        int discardedNewsActualNumber = periodicalDao.findNumberOfPeriodicalsWithCategoryAndStatus(
                PeriodicalCategory.NEWS, Periodical.Status.DISCARDED);

        assertEquals("activeNews periodicals", activeNewsExpectedNumber, activeNewsActualNumber);
        assertEquals("inActiveNews periodicals", inActiveNewsExpectedNumber, inActiveNewsActualNumber);
        assertEquals("discardedNews periodicals", discardedNewsExpectedNumber, discardedNewsActualNumber);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        if (nonNull(conn)) {
            conn.close();
        }
    }

}
