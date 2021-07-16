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


    //customizations
    public static void setEnabledNewMessageSound(Boolean isEnabledNewMessageSound) {
        Customization.IS_ENABLED_NEW_MESSAGE_SOUND = isEnabledNewMessageSound;
    }

    public static void setEnabledMessageSeenStatus(Boolean isEnabledMessageSeenStatus) {
        Customization.IS_ENABLED_MESSAGE_SEEN_STATUS = isEnabledMessageSeenStatus;
    }

    public static void setEnabledSenderIcon(Boolean isEnabledSenderIcon) {
        Customization.IS_ENABLED_SENDER_ICON = isEnabledSenderIcon;
    }

    public static void setEnabledReceiverIcon(Boolean isEnabledReceiverIcon) {
        Customization.IS_ENABLED_RECEIVER_ICON = isEnabledReceiverIcon;
    }

    public static void setEnabledSentDate(Boolean isEnabledSentDate) {
        Customization.IS_ENABLED_SENT_DATE = isEnabledSentDate;
    }

    public static void setEnabledImageSending(Boolean isEnabledImageSending) {
        Customization.IS_ENABLED_IMAGE_SENDING = isEnabledImageSending;
    }

    public static void setSendIcon(int sendIcon) {
        Customization.SEND_ICON = sendIcon;
    }

    public static void setNewMessageSound(int newMessageSound) {
        Customization.NEW_MESSAGE_SOUND = newMessageSound;
    }

    public static void setSeenIcon(int seenIcon) {
        Customization.SEEN_ICON = seenIcon;
    }

    public static void setDeliveredIcon(int deliveredIcon) {
        Customization.DELIVERED_ICON = deliveredIcon;
    }

    public static void setBackgroundImage(int backgroundImage) {
        Customization.BACKGROUND_IMAGE = backgroundImage;
    }

    public static void setImagePickerIcon(int imagePickerIcon) {
        Customization.IMAGE_PICKER_ICON = imagePickerIcon;
    }

    public static void setSenderIcon(int senderIcon) {
        Customization.SENDER_ICON = senderIcon;
    }

    public static void setReceiverIcon(int receiverIcon) {
        Customization.RECEIVER_ICON = receiverIcon;
    }

    public static void setSenderBackgroundColor(int senderBackgroundColor) {
        Customization.SENDER_BACKGROUND_COLOR = senderBackgroundColor;
    }

    public static void setReceiverBackgroundColor(int receiverBackgroundColor) {
        Customization.RECEIVER_BACKGROUND_COLOR = receiverBackgroundColor;
    }

    public static void setSenderBubble(int senderBubble) {
        Customization.SENDER_BUBBLE = senderBubble;
    }

    public static void setReceiverBubble(int receiverBubble) {
        Customization.RECEIVER_BUBBLE = receiverBubble;
    }

    public static void setSentMessageTextColor(int sentMessageTextColor) {
        Customization.SENT_MESSAGE_TEXT_COLOR = sentMessageTextColor;
    }

    public static void setReceivedMessageTextColor(int receivedMessageTextColor) {
        Customization.RECEIVED_MESSAGE_TEXT_COLOR = receivedMessageTextColor;
    }

    public static void setSentMessageDeliveryTimeTextColor(int sentMessageDeliveryTimeTextColor) {
        Customization.SENT_MESSAGE_DELIVERY_TIME_TEXT_COLOR = sentMessageDeliveryTimeTextColor;
    }

    public static void setReceivedMessageDeliveryTimeTextColor(int receivedMessageDeliveryTimeTextColor) {
        Customization.RECEIVED_MESSAGE_DELIVERY_TIME_TEXT_COLOR = receivedMessageDeliveryTimeTextColor;
    }

    public static void setHeaderColor(int headerColor) {
        Customization.HEADER_COLOR = headerColor;
    }

    public static void setHeaderShape(int headerShape) {
        Customization.HEADER_SHAPE = headerShape;
    }

    public static void setTitleTextColor(int titleTextColor) {
        Customization.TITLE_TEXT_COLOR = titleTextColor;
    }

    public static void setSubTitleTextColor(int subTitleTextColor) {
        Customization.SUB_TITLE_TEXT_COLOR = subTitleTextColor;
    }

    public static void setMessageHint(String messageHint) {
        Customization.MESSAGE_HINT = messageHint;
    }
}
