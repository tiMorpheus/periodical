package com.tolochko.periodicals.model.controller.security;


import com.tolochko.periodicals.controller.security.AuthenticationFilter;
import com.tolochko.periodicals.model.domain.user.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class AuthenticationFilterTest {
    private static final int USER_ID = 77;
    private HttpSession session = mock(HttpSession.class);
    private HttpServletRequest request = mock(HttpServletRequest.class);
    private HttpServletResponse response = mock(HttpServletResponse.class);
    @Mock
    private FilterChain chain;
    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setId(USER_ID);

        when(session.getAttribute("currentUser")).thenReturn(user);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void doFilter_IfUserInSessionIsNull() throws Exception {
        String requestURI = "/app/periodicals/2/invoices/10/pay";
        when(request.getRequestURI()).thenReturn(requestURI);

        new AuthenticationFilter().doFilter(request, response, chain);

        verify(session, times(1)).setAttribute("originalUri", requestURI);
        verify(response, times(1)).sendRedirect("/login.jsp");
    }
}