package com.codezync.chat_sdk.util;


import com.codezync.chat_sdk.model.ChatRequest;
import com.codezync.chat_sdk.model.Message;
import com.codezync.chat_sdk.model.Sender;

public class Converter {

    public static Sender createSender(ChatRequest chatRequest) {
        if (chatRequest != null) {
            return new Sender(chatRequest.getEmailOrPhoneNo(), chatRequest.getName(), chatRequest.getImageUrl());
        } else {
            return null;
        }
    }


    public static String createIdentifier(Message message) {
        if (message != null) {
            return message.getTimestamp() + "_" + message.getMessage().replace(" ", "").trim() + "_" + message.getContentType();
        } else {
            return null;
        }
    }
}
