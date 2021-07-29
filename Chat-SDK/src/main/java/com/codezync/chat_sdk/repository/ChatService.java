package com.codezync.chat_sdk.repository;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.codezync.chat_sdk.R;
import com.codezync.chat_sdk.model.NewMessageModel;
import com.codezync.chat_sdk.util.CodeZyncChat;
import com.codezync.chat_sdk.util.DefaultLauncher;
import com.codezync.chat_sdk.util.DefaultLauncherPresenter;
import com.codezync.chat_sdk.util.LogUtil;
import com.codezync.chat_sdk.viewmodel.FirebaseViewModel;

import static android.content.Context.WINDOW_SERVICE;

public class ChatService {

    private static FirebaseViewModel viewModel;
    private static boolean isStarted;
    private static String TAG = "ChatService";
    private static Observer observer;
    private static Dialog dialog;
    private static DefaultLauncherPresenter presenter;
    private static int chatCount = 0;
    private static LayoutInflater inflater;

    public static void startService(FirebaseViewModel firebaseViewModel) {
        if (!isStarted) {
            isStarted = true;
            viewModel = firebaseViewModel;
            chatCount = 0;
            // viewModel.setListenerForGetUserTyping();
            LogUtil.debug(TAG, "Chat service started...");

            observer = new Observer<NewMessageModel>() {
                @Override
                public void onChanged(NewMessageModel newMessageModel) {
                    chatCount++;
                    if (presenter != null && presenter.isDisplaying()) {
                        presenter.setUnreadCount(chatCount);
                    } else {
                        presenter.setUnreadCount(0);
                    }
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

        Activity activity = CodeZyncChat.getActivity();


//        if (dialog == null) {
        dialog = new Dialog(activity, R.style.FloatingDialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.END);
        dialog.setCancelable(false);
        setDialogGravity(dialog, (Gravity.BOTTOM | Gravity.END));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_floating_button);
//            dialog.setCanceledOnTouchOutside(false);
//
//
        ImageView imageView = dialog.findViewById(R.id.img_chat);
        imageView.setOnClickListener(view -> {
            CodeZyncChat.reOpen();
            dialog.dismiss();
        });
//
//
//        }


//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        WindowManager wma = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        wma.getDefaultDisplay().getMetrics(displayMetrics);
//        Window window = dialog.getWindow();
//        window.setFlags(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG,
//                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
//        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

//        dialog.show();

        if (inflater == null) {
            inflater = LayoutInflater.from(activity.getApplication());
        }

        if (presenter == null) {

            presenter = new DefaultLauncherPresenter(inflater, new DefaultLauncher.Listener() {
                @Override
                public void onLauncherClicked(Context var1) {
                    presenter.removeLauncher();

                    presenter = null;
                    CodeZyncChat.reOpen();
                }
            });
            presenter.displayLauncherOnAttachedRoot(CodeZyncChat.mRoot);
            presenter.setUnreadCount(chatCount);
        }

//        if (!presenter.isDisplaying()) {
//            presenter.displayLauncherOnAttachedRoot(CodeZyncChat.mRoot);
//        }


//        DefaultLauncher defaultLauncher = new DefaultLauncher(CodeZyncChat.mRoot, inflater, new DefaultLauncher.Listener() {
//            @Override
//            public void onLauncherClicked(Context var1) {
//                CodeZyncChat.reOpen();
//            }
//        }, 0);
//        defaultLauncher.fadeOnScreen();


    }


    public static void hideFloatingIcon() {
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }

        if (presenter != null && presenter.isDisplaying()) {
            presenter.removeLauncher();
            presenter = null;
        }
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
            chatCount = 0;
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
