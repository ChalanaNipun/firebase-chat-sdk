package com.codezync.chat_sdk.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.codezync.chat_sdk.R;

public class DefaultLauncher implements View.OnTouchListener {
    private static final int ANIMATION_DURATION_MS = 300;
    private final TextView badgeCount;
    final View launcherRoot;
    final DefaultLauncher.Listener listener;
    private final ImageButton badge;

    public DefaultLauncher(ViewGroup root, LayoutInflater inflater, DefaultLauncher.Listener listener, int bottomPadding) {
        this.listener = listener;
        inflater.inflate(R.layout.fragment_floating, root, true);
        this.launcherRoot = root.findViewById(R.id.launcher_root);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.launcherRoot.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomPadding);
        this.launcherRoot.setLayoutParams(params);
        this.badge = (ImageButton) this.launcherRoot.findViewById(R.id.launcher_icon);
        this.badgeCount = (TextView) this.launcherRoot.findViewById(R.id.launcher_badge_count);
        this.launcherRoot.setOnTouchListener(this);
    }

    public void setLauncherColor(@ColorInt int color) {
        Context context = this.badge.getContext();
//        Drawable iconBg = ContextCompat.getDrawable(context, R.drawable.img_circle);
//        iconBg.setColorFilter(color, PorterDuff.Mode.SRC_IN);
//        Drawable countBg = iconBg.getConstantState().newDrawable();
//        int countBackgroundColour = ContextCompat.getColor(context, R.color.white);
//        countBg.setColorFilter(countBackgroundColour, PorterDuff.Mode.SRC_IN);

    }

    public void setBadgeCount(String unreadCount) {
        this.badgeCount.setVisibility(View.VISIBLE);
        this.badgeCount.setText(unreadCount);
    }

    public void hideBadgeCount() {
        this.badgeCount.setVisibility(View.GONE);
    }

    public void fadeOnScreen() {
        this.launcherRoot.setAlpha(0.0F);
        this.launcherRoot.animate().alpha(1.0F).setDuration(100L).start();
    }

    public void fadeOffScreen(@Nullable Animator.AnimatorListener listener) {
        this.launcherRoot.animate().alpha(0.0F).setDuration(100L).setListener(listener).start();
    }

    public void pulseForTransformation(final Animator.AnimatorListener listener) {
        this.launcherRoot.animate().scaleX(1.1F).scaleY(1.1F).setDuration(100L).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                listener.onAnimationEnd(animation);
                DefaultLauncher.this.launcherRoot.animate().scaleX(1.0F).scaleY(1.0F).setDuration(100L).start();
            }
        }).start();
    }

    public void removeView() {
        if (this.launcherRoot.getParent() != null) {
            ((ViewGroup) this.launcherRoot.getParent()).removeView(this.launcherRoot);
        }

    }

    public boolean isAttachedToRoot(ViewGroup root) {
        return this.launcherRoot.getParent() == root;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                v.setScaleX(0.9F);
                v.setScaleY(0.9F);
                break;
            case 1:
                this.callListenerWithFadeOut();
        }

        return true;
    }

    private void callListenerWithFadeOut() {
        this.launcherRoot.setAlpha(1.0F);
        this.launcherRoot.animate().alpha(0.0F).setDuration(50L).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                DefaultLauncher.this.listener.onLauncherClicked(DefaultLauncher.this.launcherRoot.getContext());
            }
        }).start();
    }

    public void updateBottomPadding(int bottomPadding) {
        final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.launcherRoot.getLayoutParams();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(new int[]{params.bottomMargin, bottomPadding});
        valueAnimator.setDuration(300L);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, (Integer) animation.getAnimatedValue());
                DefaultLauncher.this.launcherRoot.setLayoutParams(params);
            }
        });
        valueAnimator.start();
    }

    public interface Listener {
        void onLauncherClicked(Context var1);
    }
}