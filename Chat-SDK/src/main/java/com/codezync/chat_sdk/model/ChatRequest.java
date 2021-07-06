package com.codezync.chat_sdk.model;

public class ChatRequest {

    private String customerId;
    private String name;
    private String imageUrl;
    private String deviceName;
    private String platform;
    private String phoneNo;
    private String email;
    private String appVersion;


    public ChatRequest(String emailOrPhoneNo, String name,
                       String imageUrl, String deviceName, String platform, String phoneNo,
                       String email,String appVersion) {
        this.customerId = emailOrPhoneNo;
        this.name = name;
        this.imageUrl = imageUrl;
        this.deviceName = deviceName;
        this.platform = platform;
        this.phoneNo = phoneNo;
        this.email = email;
        this.appVersion = appVersion;
    }

    public ChatRequest() {
    }

    public String getEmailOrPhoneNo() {
        return customerId;
    }

    public void setEmailOrPhoneNo(String emailOrPhoneNo) {
        this.customerId = emailOrPhoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
