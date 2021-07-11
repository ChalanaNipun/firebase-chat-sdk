package com.codezync.chat_sdk.util;

public interface OnMessageReceivedListener<X> {
    void onReceive(X response);
}
