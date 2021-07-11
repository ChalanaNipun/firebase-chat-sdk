package com.codezync.chat_sdk.util;

import android.app.Activity;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.codezync.chat_sdk.chat.ChatActivity;
import com.codezync.chat_sdk.model.ChatRequest;
import com.codezync.chat_sdk.model.NewMessageModel;
import com.codezync.chat_sdk.model.Sender;


public class CodeZyncChat {

    private static String mUserId;
    private static String mImageUrl;
    private static String mName;
    private static ChatRequest mChatRequest;
    private static Activity mActivity;
    private static MutableLiveData<String> onMessageReceived = new MutableLiveData<>();
    private static String lastMessage;
    private static OnMessageReceivedListener messageResponseListener;


    public static Sender getSender() {
        return new Sender(mUserId, mName, mImageUrl);
    }

    public static CodeZyncChat init(Activity activity, ChatRequest chatRequest) throws Exception {

        if (activity == null) {
            throw new Exception(Constants.ACTIVITY_ERROR_MESSAGE);
        } else if (chatRequest == null) {
            throw new Exception(Constants.CHAT_REQUEST_ERROR_MESSAGE);
        } else if (!Utility.isNotNull(chatRequest.getEmailOrPhoneNo())) {
            throw new Exception(Constants.EMAIL_OR_PHONE_NO_ERROR_MESSAGE);
        } else {
            mActivity = activity;
            mChatRequest = chatRequest;
            mUserId = chatRequest.getEmailOrPhoneNo();
            mName = chatRequest.getName();
            mImageUrl = chatRequest.getImageUrl();


        }


        return new CodeZyncChat();
    }

    public void startChat() {
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra(Constants.BUNDLE_CHAT_REQUEST, Utility.objectToString(mChatRequest));
        mActivity.startActivity(intent);
    }

    public void setEnableNewMessageSound(boolean isEnableNewMessageSound) {
        Constants.IS_ENABLED_NEW_MESSAGE_SOUND = isEnableNewMessageSound;
    }

    public void setSendIcon(int drawableId) {
        Constants.SEND_ICON = drawableId;

    }

    public void OnMessageReceived(OnMessageReceivedListener listener) {
        messageResponseListener = listener;
    }

    public static void setOnMessageReceived(NewMessageModel newMessageModel) {
        //CodeZyncChat.onMessageReceived.postValue(messageId);
        //Toasty.error(mActivity, messageId);


        if (messageResponseListener != null) {

            if (Utility.isNotNull(lastMessage)) {
                if (lastMessage.equals(newMessageModel.getNotificationId())) {
                    return;
                }
            }
            lastMessage = newMessageModel.getNotificationId();
            messageResponseListener.onReceive(newMessageModel);
        }
    }


    public static Activity getActivity() {
        return mActivity;
    }
}
