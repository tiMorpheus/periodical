package com.tolochko.periodicals.model.dao;

import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.factory.DaoFactory;
import com.tolochko.periodicals.model.dao.factory.impl.MySqlDaoFactory;
import com.tolochko.periodicals.model.dao.interfaces.InvoiceDao;
import com.tolochko.periodicals.model.domain.invoice.Invoice;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.junit.Assert.assertEquals;

public class InvoiceDaoImplTest {
    private static InvoiceDao invoiceDao;
    private static ConnectionProxy conn;
    private static DaoFactory factory;

    @BeforeClass
    public static void setUp() throws Exception {

        factory = MySqlDaoFactory.getFactoryInstance();
        invoiceDao = factory.getInvoiceDao();
    }

    @Ignore
    public void findAllBYUserId_Should_ReturnAllInvoicesByUserId(){
        List<Invoice> result = invoiceDao.findAllByUserId(1l);

        assertEquals(5, result.size());
    }

    @Ignore
    public void findAllByPeriodicalId_Should_ReturnAllInvoicesByUserId(){
        List<Invoice> result = invoiceDao.findAllByPeriodicalId(1l);

        assertEquals(3, result.size());
    }

    @Ignore
    public void getCreatedInvoiceSumByCreationDate_Should_ReturnCorrectSumByTimePeriod(){

        Instant until = Instant.now();
        Instant since = until.minus(720, ChronoUnit.DAYS);

        assertEquals(1095, invoiceDao.getCreatedInvoiceSumByCreationDate(since, until) );
    }


    @AfterClass
    public static void tearDown() throws SQLException {
        if (nonNull(conn)) {
            conn.close();
        }
    }
}
