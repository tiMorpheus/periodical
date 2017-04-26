package com.tolochko.periodicals.controller.encoding;

import org.apache.log4j.Logger;

import javax.servlet.*;
import java.io.IOException;

/**
 * Allows entering on the frontend and saving cyrillic symbols in the system.
 */
public class EncodingFilter implements Filter{
    private static final Logger logger = Logger.getLogger(EncodingFilter.class);


    @Override
    public void init(FilterConfig config) throws ServletException {
        // nothing to init
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        logger.debug("encoding: charset " + request.getCharacterEncoding());
        request.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        //nothing to destroy
    }
}
