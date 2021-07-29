package com.codezync.chat_sdk.util;

import android.app.Activity;
import android.view.ViewGroup;

import com.codezync.chat_sdk.model.ChatRequest;

public abstract class CZChat {

    public static CZChat client() {
        return CodeZyncChat.client();
    }

    public abstract CodeZyncChat registerUser(Activity activity, ChatRequest chatRequest, ViewGroup root) throws Exception;
    public abstract void startChat();

    public abstract void hideFloatingIcon();

    public abstract void OnMessageReceived(OnMessageReceivedListener listener);

    public abstract void setEnabledNewMessageSound(Boolean isEnabledNewMessageSound);

    public abstract void setEnabledMessageSeenStatus(Boolean isEnabledMessageSeenStatus);

    public abstract void setEnabledSenderIcon(Boolean isEnabledSenderIcon);

    public abstract void setEnabledReceiverIcon(Boolean isEnabledReceiverIcon);

    public abstract void setEnabledSentDate(Boolean isEnabledSentDate);

    public abstract void setEnabledImageSending(Boolean isEnabledImageSending);

    public abstract void setEnabledAdminsOnlineStatus(Boolean isEnabledOnlineStatus);

    public abstract void setSendIcon(int sendIcon);

    public abstract void setNewMessageSound(int newMessageSound);

    public abstract void setSeenIcon(int seenIcon);

    public abstract void setDeliveredIcon(int deliveredIcon);

    public abstract void setBackgroundImage(int backgroundImage);

    public abstract void setImagePickerIcon(int imagePickerIcon);

    public abstract void setSenderIcon(int senderIcon);

    public abstract void setReceiverIcon(int receiverIcon);

    public abstract void setSenderBackgroundColor(int senderBackgroundColor);

    public abstract void setReceiverBackgroundColor(int receiverBackgroundColor);

    public abstract void setChatBubbles(int senderBubble, int receiverBubble);

    public abstract void setSentMessageTextColor(int sentMessageTextColor);

    public abstract void setHeaderHeight(int headerHeight);

    public abstract void setReceivedMessageTextColor(int receivedMessageTextColor);

    public abstract void setSentMessageDeliveryTimeTextColor(int sentMessageDeliveryTimeTextColor);

    public abstract void setReceivedMessageDeliveryTimeTextColor(int receivedMessageDeliveryTimeTextColor);

    public abstract void setHeaderColor(int headerColor);

    public abstract void setHeaderShape(int headerShape);

    public abstract void setTitleTextColor(int titleTextColor);

    public abstract void setSubTitleTextColor(int subTitleTextColor);

    public abstract void setMessageHint(String messageHint);

    public abstract void setEnabledLoadingAnimation(boolean isEnable);

    public abstract void setEnabledEmptyChatAnimation(Boolean isEnabledEmptyChatAnimation);

    public abstract void setEnabledChatEndAnimation(Boolean isEnabledChatEndAnimation);

    public abstract void setEmptyChatAnimation(String emptyChatAnimationFileNameWithExtension);

    public abstract void setChatEndAnimation(String chatEndAnimationFileNameWithExtension);

    public abstract void setIsArabicLanguage(boolean isArabic);
}
