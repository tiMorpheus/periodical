package com.tolochko.periodicals.controller.message;

import java.io.Serializable;

public final class FrontMessage implements Serializable {
    private static final long serialVersionUID = 12341241777L;

    private String messageKey;
    private MessageType type;

    enum MessageType implements Serializable {
        SUCCESS, INFO, WARNING, ERROR
    }

    public FrontMessage(String messageKey, MessageType type) {
        this.messageKey = messageKey;
        this.type = type;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public MessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("FrontendMessage{messageKey='%s', type=%s}", messageKey, type);
    }
}
