package com.tolochko.periodicals.model.controller.request;

import com.tolochko.periodicals.controller.request.sign.SignOut;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class SignOutTest {
    private HttpSession session = mock(HttpSession.class);
    private HttpServletRequest request = mock(HttpServletRequest.class);
    private HttpServletResponse response = mock(HttpServletResponse.class);

    @InjectMocks
    private SignOut signOut;

    @Before
    public void setUp() throws Exception {
        when(request.getSession()).thenReturn(session);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getViewName_Should_InvalidateCurrentSession() throws Exception {
        signOut.process(request, response);

        verify(session, times(1)).removeAttribute("currentUser");
        verify(session, times(1)).invalidate();
    }
}