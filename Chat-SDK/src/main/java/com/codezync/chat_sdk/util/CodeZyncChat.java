package com.codezync.chat_sdk.util;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;

import com.codezync.chat_sdk.chat.ChatActivity;
import com.codezync.chat_sdk.model.ChatRequest;
import com.codezync.chat_sdk.model.NewMessageModel;
import com.codezync.chat_sdk.model.Sender;
import com.codezync.chat_sdk.repository.ChatService;


public class CodeZyncChat extends CZChat {

    private static String mUserId;
    private static String mImageUrl;
    private static String mName;
    private static ChatRequest mChatRequest;
    private static Activity mActivity;
    private static MutableLiveData<String> onMessageReceived = new MutableLiveData<>();
    private static String lastMessage;
    private static OnMessageReceivedListener messageResponseListener;
    private static CodeZyncChat mCodeZyncChat;
    public static ViewGroup mRoot;


    public static Sender getSender() {
        return new Sender(mUserId, mName, mImageUrl);
    }

    public static CodeZyncChat client() {
        if (mCodeZyncChat == null) {
            mCodeZyncChat = new CodeZyncChat();
        }
        return mCodeZyncChat;
    }


    @Override
    public CodeZyncChat registerUser(Activity activity, ChatRequest chatRequest, ViewGroup root) throws Exception {

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
            mRoot = root;
        }

        mCodeZyncChat = new CodeZyncChat();
        return mCodeZyncChat;
    }

    @Override
    public void startChat() {
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra(Constants.BUNDLE_CHAT_REQUEST, Utility.objectToString(mChatRequest));
        mActivity.startActivity(intent);
    }


    public static void reOpen() {
        if (mChatRequest != null && mActivity != null && mCodeZyncChat != null) {
            mCodeZyncChat.startChat();
        }
    }

    @Override
    public void hideFloatingIcon() {
        ChatService.hideFloatingIcon();
    }

    @Override
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
    @Override
    public void setEnabledNewMessageSound(Boolean isEnabledNewMessageSound) {
        Customization.IS_ENABLED_NEW_MESSAGE_SOUND = isEnabledNewMessageSound;
    }

    @Override
    public void setEnabledMessageSeenStatus(Boolean isEnabledMessageSeenStatus) {
        Customization.IS_ENABLED_MESSAGE_SEEN_STATUS = isEnabledMessageSeenStatus;
    }

    @Override
    public void setEnabledSenderIcon(Boolean isEnabledSenderIcon) {
        Customization.IS_ENABLED_SENDER_ICON = isEnabledSenderIcon;
    }

    @Override
    public void setEnabledReceiverIcon(Boolean isEnabledReceiverIcon) {
        Customization.IS_ENABLED_RECEIVER_ICON = isEnabledReceiverIcon;
    }

    @Override
    public void setEnabledSentDate(Boolean isEnabledSentDate) {
        Customization.IS_ENABLED_SENT_DATE = isEnabledSentDate;
    }

    @Override
    public void setEnabledImageSending(Boolean isEnabledImageSending) {
        Customization.IS_ENABLED_IMAGE_SENDING = isEnabledImageSending;
    }

    @Override
    public void setEnabledAdminsOnlineStatus(Boolean isEnabledOnlineStatus) {
        Customization.IS_ENABLED_ADMINS_ONLINE_STATUS = isEnabledOnlineStatus;
    }

    @Override
    public void setSendIcon(int sendIcon) {
        Customization.SEND_ICON = sendIcon;
    }

    @Override
    public void setNewMessageSound(int newMessageSound) {
        Customization.NEW_MESSAGE_SOUND = newMessageSound;
    }

    @Override
    public void setSeenIcon(int seenIcon) {
        Customization.SEEN_ICON = seenIcon;
    }

    @Override
    public void setDeliveredIcon(int deliveredIcon) {
        Customization.DELIVERED_ICON = deliveredIcon;
    }

    @Override
    public void setBackgroundImage(int backgroundImage) {
        Customization.BACKGROUND_IMAGE = backgroundImage;
    }

    @Override
    public void setImagePickerIcon(int imagePickerIcon) {
        Customization.IMAGE_PICKER_ICON = imagePickerIcon;
    }

    @Override
    public void setSenderIcon(int senderIcon) {
        Customization.SENDER_ICON = senderIcon;
    }

    @Override
    public void setReceiverIcon(int receiverIcon) {
        Customization.RECEIVER_ICON = receiverIcon;
    }

    @Override
    public void setSenderBackgroundColor(int senderBackgroundColor) {
        Customization.SENDER_BACKGROUND_COLOR = senderBackgroundColor;
    }

    @Override
    public void setReceiverBackgroundColor(int receiverBackgroundColor) {
        Customization.RECEIVER_BACKGROUND_COLOR = receiverBackgroundColor;
    }

    @Override
    public void setChatBubbles(int senderBubble, int receiverBubble) {
        if (senderBubble <= 0 || receiverBubble <= 0) {
            try {

                throw new Exception(Constants.EMPTY_BUBBLE_ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            Customization.SENDER_BUBBLE = senderBubble;
            Customization.RECEIVER_BUBBLE = receiverBubble;


        }

    }

    @Override
    public void setSentMessageTextColor(int sentMessageTextColor) {
        Customization.SENT_MESSAGE_TEXT_COLOR = sentMessageTextColor;
    }

    @Override
    public void setHeaderHeight(int headerHeight) {
        Customization.HEADER_HEIGHT = headerHeight;
    }

    @Override
    public void setReceivedMessageTextColor(int receivedMessageTextColor) {
        Customization.RECEIVED_MESSAGE_TEXT_COLOR = receivedMessageTextColor;
    }

    @Override
    public void setSentMessageDeliveryTimeTextColor(int sentMessageDeliveryTimeTextColor) {
        Customization.SENT_MESSAGE_DELIVERY_TIME_TEXT_COLOR = sentMessageDeliveryTimeTextColor;
    }

    @Override
    public void setReceivedMessageDeliveryTimeTextColor(int receivedMessageDeliveryTimeTextColor) {
        Customization.RECEIVED_MESSAGE_DELIVERY_TIME_TEXT_COLOR = receivedMessageDeliveryTimeTextColor;
    }

    @Override
    public void setHeaderColor(int headerColor) {
        Customization.HEADER_COLOR = headerColor;
    }

    @Override
    public void setHeaderShape(int headerShape) {
        Customization.HEADER_SHAPE = headerShape;
    }

    @Override
    public void setTitleTextColor(int titleTextColor) {
        Customization.TITLE_TEXT_COLOR = titleTextColor;
    }

    @Override
    public void setSubTitleTextColor(int subTitleTextColor) {
        Customization.SUB_TITLE_TEXT_COLOR = subTitleTextColor;
    }

    @Override
    public void setMessageHint(String messageHint) {
        Customization.MESSAGE_HINT = messageHint;
    }

    @Override
    public void setEnabledLoadingAnimation(boolean isEnable) {
        Customization.IS_ENABLED_LOADING_ANIMATION = isEnable;
    }

    @Override
    public void setEnabledEmptyChatAnimation(Boolean isEnabledEmptyChatAnimation) {
        Customization.IS_ENABLED_EMPTY_CHAT_ANIMATION = isEnabledEmptyChatAnimation;
    }

    @Override
    public void setEnabledChatEndAnimation(Boolean isEnabledChatEndAnimation) {
        Customization.IS_ENABLED_CHAT_END_ANIMATION = isEnabledChatEndAnimation;
    }

    @Override
    public void setEmptyChatAnimation(String emptyChatAnimationFileNameWithExtension) {
        Customization.EMPTY_CHAT_ANIMATION = emptyChatAnimationFileNameWithExtension;
    }

    @Override
    public void setChatEndAnimation(String chatEndAnimationFileNameWithExtension) {
        Customization.CHAT_END_ANIMATION = chatEndAnimationFileNameWithExtension;
    }

    @Override
    public void setIsArabicLanguage(boolean isArabic) {
        Customization.IS_ARABIC_LANGUAGE = isArabic;
    }

}
