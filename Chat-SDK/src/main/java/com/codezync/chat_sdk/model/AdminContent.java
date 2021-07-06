package com.codezync.chat_sdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminContent {


    @SerializedName("sender")
    @Expose
    private Sender sender;


    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
