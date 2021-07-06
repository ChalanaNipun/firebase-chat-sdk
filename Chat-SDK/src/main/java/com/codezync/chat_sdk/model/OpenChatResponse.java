package com.codezync.chat_sdk.model;

public class OpenChatResponse {
    private String sessionId;
    private SessionResponse sessionResponse;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionResponse getSessionResponse() {
        return sessionResponse;
    }

    public void setSessionResponse(SessionResponse sessionResponse) {
        this.sessionResponse = sessionResponse;
    }

    public OpenChatResponse(String sessionId, SessionResponse sessionResponse) {
        this.sessionId = sessionId;
        this.sessionResponse = sessionResponse;
    }
}
