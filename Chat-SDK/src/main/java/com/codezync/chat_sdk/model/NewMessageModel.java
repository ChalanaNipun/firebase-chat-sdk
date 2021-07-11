package com.codezync.chat_sdk.model;

public class NewMessageModel {

    private String notificationId;
    private ContentData contentData;

    public NewMessageModel(String notificationId, ContentData contentData) {
        this.notificationId = notificationId;
        this.contentData = contentData;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public ContentData getContentData() {
        return contentData;
    }

    public void setContentData(ContentData contentData) {
        this.contentData = contentData;
    }
}
