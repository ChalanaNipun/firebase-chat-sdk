package com.codezync.chat_sdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SessionResponse {
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("message")
    @Expose
    private List<Message> message = null;
    @SerializedName("status")
    @Expose
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
