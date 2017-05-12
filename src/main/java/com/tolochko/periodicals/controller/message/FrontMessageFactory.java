package com.tolochko.periodicals.controller.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains methods for generating different types of frontend messages: 'success', 'info', 'warning',
 * 'error'.
 */
public class FrontMessageFactory {
    private static Map<String, FrontMessage> messagesSuccess = new HashMap<>();
    private static Map<String, FrontMessage> messagesInfo = new HashMap<>();
    private static Map<String, FrontMessage> messagesWarning = new HashMap<>();
    private static Map<String, FrontMessage> messagesError = new HashMap<>();

    private static final FrontMessageFactory instance = new FrontMessageFactory();

    private FrontMessageFactory() {
    }

    public static FrontMessageFactory getInstance() {
        return instance;
    }

    public FrontMessage getSuccess(String messageKey) {
        return getMessageFromCache(messagesSuccess, FrontMessage.MessageType.SUCCESS, messageKey);
    }

    public FrontMessage getInfo(String messageKey) {
        return getMessageFromCache(messagesInfo, FrontMessage.MessageType.INFO, messageKey);
    }

    public FrontMessage getWarning(String messageKey) {
        return getMessageFromCache(messagesWarning, FrontMessage.MessageType.WARNING, messageKey);
    }

    public FrontMessage getError(String messageKey) {
        return getMessageFromCache(messagesError, FrontMessage.MessageType.ERROR, messageKey);
    }

    private FrontMessage getMessageFromCache(Map<String, FrontMessage> cache,
                                             FrontMessage.MessageType messageType,
                                             String messageKey) {

        if (!cache.containsKey(messageKey)) {
            cache.put(messageKey, new FrontMessage(messageKey, messageType));
        }

        return cache.get(messageKey);
    }
}
