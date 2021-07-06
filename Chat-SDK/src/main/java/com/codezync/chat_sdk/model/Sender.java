package com.codezync.chat_sdk.model;

public class Sender {
    private String senderId;
    private String name;
    private String imageUrl;

    public Sender(String senderId, String name, String imageUrl) {
        this.senderId = senderId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
