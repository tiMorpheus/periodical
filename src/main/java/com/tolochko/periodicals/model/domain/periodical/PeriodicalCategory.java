package com.tolochko.periodicals.model.domain.periodical;

public enum PeriodicalCategory {

    BUSINESS("category.business"),
    SPORTS("category.sports"),
    SCIENCE("category.science"),
    TRAVELLING("category.traveling"),
    NEWS("category.news"),
    NATURE("category.nature"),
    FITNESS("category.fitness");


    private String messageKey;

    PeriodicalCategory(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
