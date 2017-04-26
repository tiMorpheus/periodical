package com.tolochko.periodicals.model.controller.util;

import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.periodical.Periodical;
import com.tolochko.periodicals.model.domain.periodical.PeriodicalCategory;
import com.tolochko.periodicals.model.domain.user.User;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpUtilTest {

    private static final int USER_ID = 2;
    private HttpSession session;
    private HttpServletRequest request;

    @Before
    public void setup() {
        User user = new User();
        user.setId(USER_ID);

        session = mock(HttpSession.class);
        when(session.getAttribute("currentUser")).thenReturn(user);

        request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void getFirstIdFromUri_Should_ReturnCorrectId() throws Exception {
        String uri = "/app/periodicals/2/invoices/10/pay";
        int expected = 2;
        int actual = HttpUtil.getFirstIdFromUri(uri);

        assertEquals(expected, actual);
    }

    @Test
    public void getUserIdFromSession_Should_ReturnCorrectId() throws Exception {
        long expected = 2;
        long actual = HttpUtil.getUserIdFromSession(request);

        assertEquals(expected, actual);
    }

    @Test
    public void getPeriodicalFromRequest_Should_ReturnPeriodical() {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("entityId")).thenReturn("10");
        when(request.getParameter("periodicalName")).thenReturn("Test Name");
        when(request.getParameter("periodicalCategory")).thenReturn("news");
        when(request.getParameter("periodicalPublisher")).thenReturn("Test Publisher");
        when(request.getParameter("periodicalDescription")).thenReturn("Test description");
        when(request.getParameter("periodicalCost")).thenReturn("99");
        when(request.getParameter("periodicalStatus")).thenReturn("active");

        Periodical periodical = HttpUtil.getPeriodicalFromRequest(request);

        assertEquals(10, periodical.getId());
        assertEquals("Test Name", periodical.getName());
        assertEquals(PeriodicalCategory.NEWS, periodical.getCategory());
        assertEquals("Test Publisher", periodical.getPublisher());
        assertEquals("Test description", periodical.getDescription());
        assertEquals(99, periodical.getOneMonthCost());
        assertEquals(Periodical.Status.ACTIVE, periodical.getStatus());
    }
}
