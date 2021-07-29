package com.codezync.chat_sdk.util;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

public class DefaultLauncherPresenter implements DefaultLauncher.Listener {

    private LayoutInflater inflater;
    private int previousUnreadCount = 0;
    private int bottomPadding = 0;
    @VisibleForTesting
    @Nullable
    DefaultLauncher defaultLauncher;
    DefaultLauncher.Listener listener;


    public DefaultLauncherPresenter(LayoutInflater inflater, DefaultLauncher.Listener listener) {
        this.inflater = inflater;
        this.listener = listener;
//        this.bottomPadding = this.getDefaultPadding(inflater.getContext().getResources());
    }

    public void displayLauncherOnAttachedRoot(ViewGroup root) {
        if (this.defaultLauncher != null && !this.defaultLauncher.isAttachedToRoot(root)) {
            this.defaultLauncher.removeView();
            this.defaultLauncher = null;
//            removeLauncher();
        }

        if (this.defaultLauncher == null) {
            this.defaultLauncher = new DefaultLauncher(root, this.inflater, this, this.bottomPadding);
            this.defaultLauncher.fadeOnScreen();
        }
        this.setUnreadCount(this.previousUnreadCount);
    }

    public DefaultLauncher getAndUnsetLauncher() {
        DefaultLauncher localLauncher = this.defaultLauncher;
        this.defaultLauncher = null;
        return localLauncher;
    }


    public void removeLauncher() {
        if (this.defaultLauncher != null) {
            this.defaultLauncher.fadeOffScreen((Animator.AnimatorListener) null);
            this.defaultLauncher.removeView();
            this.defaultLauncher = null;
        }

    }

    public boolean isDisplaying() {
        return this.defaultLauncher != null;
    }

    public void setUnreadCount(int unreadCount) {
        if (this.isDisplaying()) {
            String unreadCountText = String.valueOf(unreadCount);
            if (unreadCount > 0) {
                this.defaultLauncher.setBadgeCount(unreadCountText);
            } else {
                this.defaultLauncher.hideBadgeCount();
            }
        }

        this.previousUnreadCount = unreadCount;
    }

    @Override
    public void onLauncherClicked(Context var1) {
        listener.onLauncherClicked(var1);
    }

//    private int getDefaultPadding(Resources resources) {
//        return resources.getDimensionPixelSize() + resources.getDimensionPixelSize(dimen.intercom_bottom_padding);
//    }

//    public void setBottomPadding(int bottomPadding) {
//        this.bottomPadding = this.getDefaultPadding(this.inflater.getContext().getResources()) + bottomPadding;
//        if (this.isDisplaying()) {
//            this.defaultLauncher.updateBottomPadding(this.bottomPadding);
//        }
//
//    }
}
