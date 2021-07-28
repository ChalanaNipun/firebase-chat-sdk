package com.codezync.chat_sdk.repository;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.codezync.chat_sdk.R;
import com.codezync.chat_sdk.model.NewMessageModel;
import com.codezync.chat_sdk.util.CodeZyncChat;
import com.codezync.chat_sdk.util.Customization;
import com.codezync.chat_sdk.util.LogUtil;
import com.codezync.chat_sdk.viewmodel.FirebaseViewModel;
import com.google.rpc.Code;

public class ChatService {

    private static FirebaseViewModel viewModel;
    private static boolean isStarted;
    private static String TAG = "ChatService";
    private static Observer observer;
    private static Dialog dialog;

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

        openDialog();
    }

    private static void openDialog() {
        if (dialog == null) {
            dialog = new Dialog(CodeZyncChat.getActivity(), R.style.FloatingDialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            Window window = dialog.getWindow();
//            window.setGravity(Gravity.BOTTOM | Gravity.END);
            //   setDialogGravity(dialog, (Gravity.BOTTOM | Gravity.END));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_floating_button);
            dialog.setCanceledOnTouchOutside(false);


            ImageView imageView = dialog.findViewById(R.id.img_chat);
            imageView.setOnClickListener(view -> {
                CodeZyncChat.reOpen();
                dialog.dismiss();
            });

//            if (Customization.CHAT_FLOATING_ICON != 0) {
//                imageView.setImageDrawable(CodeZyncChat.getActivity().getDrawable(Customization.CHAT_FLOATING_ICON));
//            }
//
//            if (Customization.FLOATING_ICON_WIDTH > 30) {
//                imageView.getLayoutParams().width = Customization.FLOATING_ICON_WIDTH;
//                imageView.getLayoutParams().height = Customization.FLOATING_ICON_WIDTH;
//            }

        }
        dialog.show();
    }

    private static void setDialogGravity(Dialog dialog, int gravity) {
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                params.horizontalMargin = 0;
                params.gravity = gravity;
                params.dimAmount = 0;
                params.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(params);
            }
        }
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
