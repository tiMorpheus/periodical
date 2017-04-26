package com.tolochko.periodicals.controller.view;

import java.util.Locale;

public enum SystemLocale {
    EN_EN(Locale.ENGLISH),
    RU_RU(new Locale("ru","RU"));

    private Locale locale;

    SystemLocale(Locale locale){
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }
}
