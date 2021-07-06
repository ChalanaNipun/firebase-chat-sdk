package com.codezync.chat_sdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContentData {


    @SerializedName("customer")
    @Expose
    private ChatRequest customer;

    @SerializedName("lastMessage")
    @Expose
    private Message lastMessage;

    @SerializedName("customerOnline")
    @Expose
    private boolean isCustomerOnline;

    @SerializedName("adminOnline")
    @Expose
    private boolean isAdminOnline;

    @SerializedName("adminTyping")
    @Expose
    private boolean isAdminTyping;

    @SerializedName("customerTyping")
    @Expose
    private boolean isCustomerTyping;

    @SerializedName("chat_status")
    @Expose
    private String chatStatus;


    public ContentData(ChatRequest chatRequest, Message lastMessage, boolean isCustomerOnline, boolean isAdminTyping, boolean isCustomerTyping, String chatStatus) {
        this.customer = chatRequest;
        this.lastMessage = lastMessage;
        this.isCustomerOnline = isCustomerOnline;
        this.isAdminTyping = isAdminTyping;
        this.isCustomerTyping = isCustomerTyping;
        this.chatStatus = chatStatus;
    }

    public ContentData() {
    }


    public ChatRequest getCustomer() {
        return customer;
    }

    public void setCustomer(ChatRequest customer) {
        this.customer = customer;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isCustomerOnline() {
        return isCustomerOnline;
    }

    public void setCustomerOnline(boolean isOnline) {
        this.isCustomerOnline = isOnline;
    }

    public boolean isAdminTyping() {
        return isAdminTyping;
    }

    public void setAdminTyping(boolean adminTyping) {
        isAdminTyping = adminTyping;
    }

    public boolean isCustomerTyping() {
        return isCustomerTyping;
    }

    public void setCustomerTyping(boolean isCustomerTyping) {
        this.isCustomerTyping = isCustomerTyping;
    }

    public boolean isAdminOnline() {
        return isAdminOnline;
    }

    public void setAdminOnline(boolean adminOnline) {
        isAdminOnline = adminOnline;
    }

    public String getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(String chatStatus) {
        this.chatStatus = chatStatus;
    }
}
