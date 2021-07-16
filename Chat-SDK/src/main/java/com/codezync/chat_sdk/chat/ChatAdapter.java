package com.codezync.chat_sdk.chat;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.codezync.chat_sdk.R;
import com.codezync.chat_sdk.databinding.LayoutChatItemV2Binding;
import com.codezync.chat_sdk.model.Message;
import com.codezync.chat_sdk.util.Constants;
import com.codezync.chat_sdk.util.Customization;
import com.codezync.chat_sdk.util.LogUtil;
import com.codezync.chat_sdk.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final String TAG = "ChatAdapter";

    public List<Message> messageList;
    public static Activity mActivity;
    private String currentUserId;
    private com.dinuscxj.progressbar.CircleProgressBar progressBar;
    private boolean isLastMessageSeen;


    public void setLastMessageSeen(boolean lastMessageSeen) {
        isLastMessageSeen = lastMessageSeen;
        notifyDataSetChanged();
    }

    public ChatAdapter(Activity mActivity, String currentUserId) {
        LogUtil.debug(TAG, "ChatAdapter");
        this.messageList = new ArrayList<>();
        this.mActivity = mActivity;
        this.currentUserId = currentUserId;
    }


    public void setData(List<Message> messageList) {
        this.messageList.clear();
        this.messageList.addAll(messageList);
        notifyDataSetChanged();
    }

    public void addData(List<Message> messageList) {
        LogUtil.debug(TAG, "addData");
        this.messageList.addAll(messageList);
        notifyDataSetChanged();
    }

    public void addData(Message message) {
        LogUtil.debug(TAG, "addData");
        this.messageList.add(message);
        notifyDataSetChanged();
    }


    public boolean haveChat() {
        boolean isFound = false;
        if (messageList != null || messageList.size() > 0) {
            for (Message message : messageList) {
                if (message.getStatus().equals(Constants.SEEN_STATUS) || message.getStatus().equals(Constants.DELIVERED_STATUS)) {
                    isFound = true;
                    break;
                }
            }
        }
        return isFound;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LayoutChatItemV2Binding binding =
                LayoutChatItemV2Binding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // LogUtil.debug(TAG, "onBindViewHolder " + position);

        Message message = messageList.get(position);

        if (message.getStatus().equals(Constants.STATUS_HIDDEN)) {
            holder.binding.llReceived.setVisibility(View.GONE);
            holder.binding.llSent.setVisibility(View.GONE);
            return;
        }


        if (Customization.SENT_MESSAGE_TEXT_COLOR != 0) {
            holder.binding.lblSentMessage.setTextColor(mActivity.getColor(Customization.SENT_MESSAGE_TEXT_COLOR));
        }

        if (Customization.SENT_MESSAGE_DELIVERY_TIME_TEXT_COLOR != 0) {
            holder.binding.lblSentDate.setTextColor(mActivity.getColor(Customization.SENT_MESSAGE_DELIVERY_TIME_TEXT_COLOR));
        }

        if (Customization.RECEIVED_MESSAGE_TEXT_COLOR != 0) {
            holder.binding.lblReceivedMessage.setTextColor(mActivity.getColor(Customization.RECEIVED_MESSAGE_TEXT_COLOR));
        }

        if (Customization.RECEIVED_MESSAGE_DELIVERY_TIME_TEXT_COLOR != 0) {
            holder.binding.lblReceivedDate.setTextColor(mActivity.getColor(Customization.RECEIVED_MESSAGE_DELIVERY_TIME_TEXT_COLOR));
        }

        if (Customization.SENDER_BACKGROUND_COLOR != 0 && Customization.SENDER_BUBBLE == 0) {
            holder.binding.cardSentMessage.setCardBackgroundColor(mActivity.getColor(Customization.SENDER_BACKGROUND_COLOR));
        }

        if (Customization.RECEIVER_BACKGROUND_COLOR != 0 && Customization.RECEIVER_BUBBLE == 0) {
            holder.binding.cardReceivedMessage.setCardBackgroundColor(mActivity.getColor(Customization.RECEIVER_BACKGROUND_COLOR));
        }


        if (message.isSentMessage(currentUserId)) {

            if (Customization.IS_ENABLED_SENDER_ICON) {

                if (Customization.SENDER_ICON != 0) {
                    Utility.loadImage(holder.binding.imgMy, mActivity, Customization.SENDER_ICON, R.drawable.img_user_placeholder);
                } else {
                    Utility.loadImage(holder.binding.imgMy, mActivity, message.getSender().getImageUrl(), R.drawable.img_user_placeholder);
                }

                holder.binding.imgMy.setVisibility(View.VISIBLE);
            } else {
                holder.binding.imgMy.setVisibility(View.GONE);
            }

            if (Customization.IS_ENABLED_SENT_DATE) {
                holder.binding.lblSentDate.setText(Utility.toDisplayDateFormat(message.getTimestamp()));
                holder.binding.lblSentDate.setVisibility(View.VISIBLE);
            } else {
                holder.binding.lblSentDate.setVisibility(View.GONE);
            }


            if (Customization.IS_ENABLED_MESSAGE_SEEN_STATUS) {
                holder.binding.imgSentMessageStatus.setVisibility(View.VISIBLE);

                if (isLastMessageSeen) {
                    holder.binding.imgSentMessageStatus.setImageDrawable(Customization.SEEN_ICON != 0 ? mActivity.getDrawable(Customization.SEEN_ICON) : mActivity.getDrawable(R.drawable.ic_seen));
                } else if (message.getStatus().equals(Constants.SEEN_STATUS)) {
                    holder.binding.imgSentMessageStatus.setImageDrawable(Customization.DELIVERED_ICON != 0 ? mActivity.getDrawable(Customization.DELIVERED_ICON) : mActivity.getDrawable(R.drawable.ic_delivered));
                }


            } else {
                holder.binding.imgSentMessageStatus.setVisibility(View.GONE);
            }


            if (message.getContentType().equals(Constants.TEXT_CONTENT_TYPE)) {
                holder.binding.llSent.setVisibility(View.VISIBLE);
                holder.binding.llReceived.setVisibility(View.GONE);
                holder.binding.lblSentMessage.setVisibility(View.VISIBLE);
                holder.binding.imgSent.setVisibility(View.GONE);
                holder.binding.rlProgress.setVisibility(View.GONE);
                holder.binding.lblSentMessage.setText(message.getMessage());
            } else if (message.getContentType().equals(Constants.IMAGE_CONTENT_TYPE)) {
                holder.binding.llSent.setVisibility(View.VISIBLE);
                holder.binding.llReceived.setVisibility(View.GONE);
                holder.binding.lblSentMessage.setVisibility(View.GONE);
                holder.binding.imgSent.setVisibility(View.VISIBLE);
                Utility.loadImage(holder.binding.imgSent, mActivity, message.getMessage(), R.drawable.img_pic_placeholder);

                if (message.getStatus().equals(Constants.STATUS_UPLOADING)) {
                    progressBar = holder.binding.lineProgress;
                    holder.binding.rlProgress.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.rlProgress.setVisibility(View.GONE);
                }

            }


        } else {

            if (Customization.IS_ENABLED_RECEIVER_ICON) {

                if (Customization.RECEIVER_ICON != 0) {
                    Utility.loadImage(holder.binding.imgSender, mActivity, Customization.RECEIVER_ICON, R.drawable.img_user_placeholder);
                } else {
                    Utility.loadImage(holder.binding.imgSender, mActivity, message.getSender().getImageUrl(), R.drawable.img_user_placeholder);
                }

                holder.binding.imgSender.setVisibility(View.VISIBLE);
            } else {
                holder.binding.imgSender.setVisibility(View.GONE);
            }


            if (Customization.IS_ENABLED_SENT_DATE) {
                holder.binding.lblReceivedDate.setText(Utility.toDisplayDateFormat(message.getTimestamp()));
                holder.binding.lblReceivedDate.setVisibility(View.VISIBLE);
            } else {
                holder.binding.lblReceivedDate.setVisibility(View.GONE);
            }

            holder.binding.imgReceivedMessageStatus.setVisibility(View.GONE);

            if (message.getContentType().equals(Constants.TEXT_CONTENT_TYPE)) {
                holder.binding.llSent.setVisibility(View.GONE);
                holder.binding.llReceived.setVisibility(View.VISIBLE);
                holder.binding.lblReceivedMessage.setVisibility(View.VISIBLE);
                holder.binding.imgReceived.setVisibility(View.GONE);
                holder.binding.lblReceivedMessage.setText(message.getMessage());
            } else if (message.getContentType().equals(Constants.IMAGE_CONTENT_TYPE)) {
                holder.binding.llSent.setVisibility(View.GONE);
                holder.binding.llReceived.setVisibility(View.VISIBLE);
                holder.binding.lblReceivedMessage.setVisibility(View.GONE);
                holder.binding.imgReceived.setVisibility(View.VISIBLE);
                Utility.loadImage(holder.binding.imgReceived, mActivity, message.getMessage(), R.drawable.img_pic_placeholder);
            }

        }

    }


    public void setUploadingImageProgress(double progress) {

        if (progressBar != null) {
            progressBar.setProgress((int) progress);
        }
    }

    public void removeProgress() {
        //progressBar.setVisibility(View.GONE);
        progressBar = null;
    }


    @Override
    public int getItemCount() {
        // LogUtil.info(TAG, "getItemCount " + homeCuisineList.size());
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LayoutChatItemV2Binding binding;

        public ViewHolder(@NonNull LayoutChatItemV2Binding b) {
            super(b.getRoot());
            this.binding = b;
        }

//        public void bind(Object object) {
//            binding.executePendingBindings();
//        }

    }
}
