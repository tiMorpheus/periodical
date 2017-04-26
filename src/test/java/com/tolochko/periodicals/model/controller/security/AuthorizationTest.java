package com.tolochko.periodicals.model.controller.security;

import com.tolochko.periodicals.controller.security.Authorization;
import com.tolochko.periodicals.model.domain.user.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationTest {
    private HttpSession session = mock(HttpSession.class);
    private HttpServletRequest request = mock(HttpServletRequest.class);
    private User admin;
    private User notAdmin;

    @Before
    public void setUp() throws Exception {
        User.Role adminRole = User.Role.ADMIN;

        User.Builder builder = new User.Builder();
        builder.setRole(adminRole);

        admin = builder.build();

        User.Role notAdminRole = User.Role.SUBSCRIBER;

        builder = new User.Builder();

        builder.setRole(notAdminRole);
        notAdmin = builder.build();

        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void checkPermissions_AccessToPersistPeriodical_ShouldReturn_TrueForAdmin() throws Exception {
        when(request.getMethod()).thenReturn("post");
        when(request.getRequestURI()).thenReturn("/app/periodicals");
        when(session.getAttribute("currentUser")).thenReturn(admin);

        Assert.assertTrue(Authorization.getInstance().checkPermissions(request));

    }

    @Test
    public void checkPermissions_AccessToPersistPeriodical_ShouldReturn_FalseForNotAdmin() throws Exception {
        when(request.getMethod()).thenReturn("post");
        when(request.getRequestURI()).thenReturn("/app/periodicals");
        when(session.getAttribute("currentUser")).thenReturn(notAdmin);

        Assert.assertFalse(Authorization.getInstance().checkPermissions(request));
    }

    @Test
    public void checkPermissions_AccessToDisplayPeriodicals_ShouldReturn_TrueForAdmin() throws Exception {
        when(request.getMethod()).thenReturn("get");
        when(request.getRequestURI()).thenReturn("/app/periodicals");
        when(session.getAttribute("currentUser")).thenReturn(admin);

        Assert.assertTrue(Authorization.getInstance().checkPermissions(request));

    }

    @Test
    public void checkPermissions_AccessToDisplayPeriodicals_ShouldReturn_TrueForNotAdmin() throws Exception {
        when(request.getMethod()).thenReturn("get");
        when(request.getRequestURI()).thenReturn("/app/periodicals");
        when(session.getAttribute("currentUser")).thenReturn(notAdmin);

        Assert.assertTrue(Authorization.getInstance().checkPermissions(request));

    }

    @Test
    public void checkPermissions_AccessToDisplayUsers_ShouldReturn_FalseForNotAdmin() throws Exception {
        when(request.getMethod()).thenReturn("get");
        when(request.getRequestURI()).thenReturn("/app/users");
        when(session.getAttribute("currentUser")).thenReturn(notAdmin);

        Assert.assertFalse(Authorization.getInstance().checkPermissions(request));

    }

    @Test
    public void checkPermissions_AccessToDisplayUsers_ShouldReturn_TrueForAdmin() throws Exception {
        when(request.getMethod()).thenReturn("get");
        when(request.getRequestURI()).thenReturn("/app/users");
        when(session.getAttribute("currentUser")).thenReturn(admin);

        Assert.assertTrue(Authorization.getInstance().checkPermissions(request));

    }

    @Test
    public void checkPermissions_AccessToAdminPanel_ShouldReturn_FalseForNotAdmin() throws Exception {
        when(request.getMethod()).thenReturn("get");
        when(request.getRequestURI()).thenReturn("/app/adminPanel");
        when(session.getAttribute("currentUser")).thenReturn(notAdmin);

        Assert.assertFalse(Authorization.getInstance().checkPermissions(request));

    }

    @Test
    public void checkPermissions_AccessToAdminPanel_ShouldReturn_TrueForAdmin() throws Exception {
        when(request.getMethod()).thenReturn("get");
        when(request.getRequestURI()).thenReturn("/app/adminPanel");
        when(session.getAttribute("currentUser")).thenReturn(admin);

        Assert.assertTrue(Authorization.getInstance().checkPermissions(request));

    }

    @Test
    public void checkPermissions_AccessToUpdatePeriodical_ShouldReturn_TrueForAdmin() throws Exception {
        when(request.getMethod()).thenReturn("get");
        when(request.getRequestURI()).thenReturn("/app/periodicals/10/update");
        when(session.getAttribute("currentUser")).thenReturn(admin);

        Assert.assertTrue(Authorization.getInstance().checkPermissions(request));
    }

    @Test
    public void checkPermissions_AccessToUpdatePeriodical_ShouldReturn_FalseForNotAdmin() throws Exception {
        when(request.getMethod()).thenReturn("get");
        when(request.getRequestURI()).thenReturn("/app/periodicals/10/update");
        when(session.getAttribute("currentUser")).thenReturn(notAdmin);

        Assert.assertFalse(Authorization.getInstance().checkPermissions(request));

    }
}