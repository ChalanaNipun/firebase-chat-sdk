package com.codezync.chat_sdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("sender")
    @Expose
    private Sender sender;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("status")
    @Expose
    private String status;

    public Message() {
    }

    public Message(String timestamp, Sender sender, String message, String contentType, String status) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.message = message;
        this.contentType = contentType;
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSentMessage(String currentUserId) {
        if (currentUserId.equals(sender.getSenderId())) {
            return true;
        } else {
            return false;
        }
    }
}
