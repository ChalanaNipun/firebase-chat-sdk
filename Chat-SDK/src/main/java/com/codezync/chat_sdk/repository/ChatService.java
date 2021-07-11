package com.codezync.chat_sdk.repository;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.codezync.chat_sdk.model.NewMessageModel;
import com.codezync.chat_sdk.util.CodeZyncChat;
import com.codezync.chat_sdk.util.LogUtil;
import com.codezync.chat_sdk.viewmodel.FirebaseViewModel;

public class ChatService {

    private static FirebaseViewModel viewModel;
    private static boolean isStarted;
    private static String TAG = "ChatService";
    private static Observer observer;

    public static void startService(FirebaseViewModel firebaseViewModel) {
        if (!isStarted) {
            isStarted = true;
            viewModel = firebaseViewModel;
            // viewModel.setListenerForGetUserTyping();
            LogUtil.debug(TAG, "Chat service started...");

            observer = new Observer<NewMessageModel>() {
                @Override
                public void onChanged(NewMessageModel newMessageModel) {
                    CodeZyncChat.setOnMessageReceived(newMessageModel);
                }
            };

        } else {
            LogUtil.debug(TAG, "Already service started...");
        }

        bindObserver();

    }


    private static void bindObserver() {
        if (observer != null) {
            LogUtil.debug(TAG, "bindObserver");
            viewModel.onNewMessageReceived.observe((LifecycleOwner) CodeZyncChat.getActivity(), observer);
        }
    }

    private static void unBindObserver() {
        if (observer != null) {
            viewModel.onNewMessageReceived.removeObserver(observer);
            LogUtil.debug(TAG, "unBindObserver");
        }
    }


    public static void stopService() {
        if (isStarted) {
            isStarted = false;
            unBindObserver();
            viewModel = null;
            LogUtil.debug(TAG, "Chat Service Stopped...");
        }
    }


    public static void pauseService() {
        if (isStarted) {
            unBindObserver();
            LogUtil.debug(TAG, "Chat Service Paused...");
        }
    }
}
