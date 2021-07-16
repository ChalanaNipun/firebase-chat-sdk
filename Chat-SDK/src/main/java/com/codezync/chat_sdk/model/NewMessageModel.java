package com.codezync.chat_sdk.model;

public class NewMessageModel {

    private String notificationId;
    private Message message;

    public NewMessageModel(String notificationId, Message message) {
        this.notificationId = notificationId;
        this.message = message;
    }


    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
