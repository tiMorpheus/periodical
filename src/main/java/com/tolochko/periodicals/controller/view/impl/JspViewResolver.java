package com.tolochko.periodicals.controller.view.impl;


import com.tolochko.periodicals.controller.view.ViewResolver;

import java.io.Serializable;

//contains methods for converting logical names into paths
public final class JspViewResolver implements ViewResolver, Serializable{
    private static final long serialVersionUID = 128881231L;
    private static final JspViewResolver instance = new JspViewResolver();

    private JspViewResolver(){}

    public static JspViewResolver getInstance(){
        return instance;
    }


    @Override
    public String resolvePrivateViewName(String viewName) {
        return String.format("/WEB-INF/app/%s.jsp", viewName);
    }

    @Override
    public String resolvePublicViewName(String viewName) {
        return String.format("/%s.jsp", viewName);
    }
}
