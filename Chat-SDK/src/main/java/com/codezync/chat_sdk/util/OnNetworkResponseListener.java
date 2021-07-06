package com.codezync.chat_sdk.util;

public interface OnNetworkResponseListener<X, String> {

    void onSuccessResponse(X response);

    void onErrorResponse(String response);

    void onNetworkError();
}
