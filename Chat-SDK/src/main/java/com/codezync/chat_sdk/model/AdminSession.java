package com.codezync.chat_sdk.model;

public class AdminSession {
    private String sessionId;
    private AdminContent adminContent;

    public AdminSession(String sessionId, AdminContent adminContent) {
        this.sessionId = sessionId;
        this.adminContent = adminContent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public AdminContent getAdminContent() {
        return adminContent;
    }

    public void setAdminContent(AdminContent adminContent) {
        this.adminContent = adminContent;
    }
}
