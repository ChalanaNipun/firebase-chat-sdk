package com.codezync.chat_sdk.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.codezync.chat_sdk.R;


public class ProgressDialog {

    private Dialog progressDialog;
    private Context mContext;
    private View mView;

    public ProgressDialog(Context context, View view) {
        this.mContext = mContext;
        this.mView = mView;

        progressDialog = new Dialog(mContext, R.style.DialogTheme);
        if (mView != null) {
            progressDialog.setContentView(mView);
        } else {
            progressDialog.setContentView(R.layout.progress_dialog);
        }
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public ProgressDialog(Context context, Activity activity, int view) {
        this.mContext = mContext;
        this.mView = activity.getLayoutInflater().inflate(view, null);
        progressDialog = new Dialog(mContext, R.style.DialogTheme);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setContentView(mView);
    }

    public ProgressDialog(Context context) {
        this.mContext = context;
        progressDialog = new Dialog(mContext, R.style.DialogTheme);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showProgress() {
        if (progressDialog != null) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
    }

    public void hideProgress() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public static Dialog createProgressDialog(Context context) {
        Dialog progress;
        progress = new Dialog(context, R.style.DialogTheme);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setContentView(R.layout.progress_dialog);
        return progress;
    }


}
