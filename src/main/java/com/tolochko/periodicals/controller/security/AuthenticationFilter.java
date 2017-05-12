package com.tolochko.periodicals.controller.security;

import com.tolochko.periodicals.controller.util.HttpUtil;
import com.tolochko.periodicals.model.domain.user.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Makes sure that this request comes from a signed in user and the session has not expired.
 */
public class AuthenticationFilter implements Filter {

    private final List<String> unProtectedUris = Arrays.asList("/app/signIn", "/app/signUp",
            "/app/validation");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to init
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (requestNotRequiresAuthentication(request)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        String requestUri = request.getRequestURI();
        User currentUser = HttpUtil.getCurrentUserFromFromDb(request);

        if (isNull(currentUser)) {
            request.getSession().setAttribute("originalUri", requestUri);
            response.sendRedirect("/login.jsp");

        } else if (isUserActive(currentUser)) {
            response.sendRedirect("/app/signOut");

        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean requestNotRequiresAuthentication(HttpServletRequest request) {
        return unProtectedUris.contains(request.getRequestURI());
    }

    private boolean isUserActive(User currentUser) {
        return !User.Status.ACTIVE.equals(currentUser.getStatus());
    }

    @Override
    public void destroy() {
        // nothing to destroy
    }
}
