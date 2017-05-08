package com.tolochko.periodicals.controller.security;

import com.tolochko.periodicals.controller.view.ViewResolver;
import com.tolochko.periodicals.controller.view.impl.JspViewResolver;
import com.tolochko.periodicals.model.domain.user.User;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Checks whether a current user has enough permissions to get a requested resource or perform
 * an operation.
 */
public class AuthorizationFilter implements Filter {
    private static final Logger logger = Logger.getLogger(AuthorizationFilter.class);
    private ViewResolver viewResolver = JspViewResolver.getInstance();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to init
    }

    /**
     * Proceeds to the next resource if a current user has enough permissions, and
     * redirects to 'access denied page' otherwise.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        boolean isRequestAuthorized = Authorization.getInstance().checkPermissions(request);

        if (isRequestAuthorized) {
            filterChain.doFilter(request, response);
        } else {

            String usernameFromSession = ((User) request.getSession().getAttribute("currentUser")).getUsername();
            String requestUri = request.getRequestURI();
            logger.error(String.format("Access denied for user '%s' to '%s'!!!%n", usernameFromSession, requestUri));

            response.sendRedirect(viewResolver.resolvePublicViewName("errors/accessDenied"));
        }
    }

    @Override
    public void destroy() {
        // nothing to init
    }
}
