package com.codezync.chat_sdk.util;

public class Constants {

    public final static String CHAT_COLLECTION_PATH = "chats";
    public final static String ADMIN_COLLECTION_PATH = "admin";
    public final static String CHAT_CONTENT_DOCUMENT = "content";
    public final static String TEXT_CONTENT_TYPE = "text";
    public final static String IMAGE_CONTENT_TYPE = "image";
    public final static String CHAT_MESSAGE_FIELD = "message";
    public static final String BUNDLE_SENDER = "bundleSender";
    public static final String BUNDLE_CHAT_REQUEST = "bundleChatRequest";
    public static final String DELIVERED_MESSAGE_STATUS = "delivered";
    public static final String DEFAULT_MESSAGE_STATUS = "default";
    public static final int IMAGE_CHOOSER_REQUEST_CODE = 500;
    public static final int ACTIVITY_RESULTS_READ_STORAGE = 400;
    public static final String RESIZE_IMAGE_OUTPUT_FOLDER = "CZC";
    public static final String STATUS_UPLOADING = "uploading";
    public static final String OPEN_STATUS = "open";
    public static final String DELIVERED_STATUS = "delivered";
    public static final String SEEN_STATUS = "seen";
    public static final String STATUS_HIDDEN = "hidden";
    public static final String ACTIVITY_ERROR_MESSAGE = "Activity object is null !";
    public static final String CHAT_REQUEST_ERROR_MESSAGE = "ChatRequest Cannot be null !";
    public static final String EMAIL_OR_PHONE_NO_ERROR_MESSAGE = "ChatRequest EmailOrPhoneNo field Cannot be empty !";
    public static final String MESSAGE_TONE_FILE_NAME = "notification_tone";
    public static Boolean IS_ENABLED_NEW_MESSAGE_SOUND = false;
    public static Boolean IS_ENABLED_MESSAGE_SEEN_STATUS = false;
    public static int SEND_ICON = 0;
//    public static final String ERROR_SENDING_INIT_MESSAGE = "Init message sending error ! contact developers !";
}
