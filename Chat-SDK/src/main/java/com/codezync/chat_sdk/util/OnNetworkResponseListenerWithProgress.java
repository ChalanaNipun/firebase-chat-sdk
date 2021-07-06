package com.codezync.chat_sdk.util;

public interface OnNetworkResponseListenerWithProgress<X, String> {

    void onSuccessResponse(X response);

    void onErrorResponse(String response);

    void onProgress(double response);

    void onNetworkError();
}
