package com.codezync.chat_sdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {

    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("timestamp")
    @Expose
    String timestamp;

    @SerializedName("message")
    @Expose
    List<Message> message;

    @SerializedName("senderId")
    @Expose
    String senderId;


    public User(String senderId,String status, String timestamp, List<Message> message) {
        this.senderId = senderId;
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
