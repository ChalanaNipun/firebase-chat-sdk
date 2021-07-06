package com.codezync.chat_sdk.util;

public interface OnValueChangeListener<X, String> {

    void onSuccessResponse(X response);

    void onErrorResponse(String response);
}
