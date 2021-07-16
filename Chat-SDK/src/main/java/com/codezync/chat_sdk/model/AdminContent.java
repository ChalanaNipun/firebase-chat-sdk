package com.codezync.chat_sdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminContent {


    @SerializedName("sender")
    @Expose
    private Sender sender;

    @SerializedName("defaultMessage")
    @Expose
    private String defaultMessage;

    @SerializedName("chatIcon")
    @Expose
    private String chatIcon;

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getChatIcon() {
        return chatIcon;
    }

    public void setChatIcon(String chatIcon) {
        this.chatIcon = chatIcon;
    }
}
