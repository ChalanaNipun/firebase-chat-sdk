package com.codezync.chat_sdk.util;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.codezync.chat_sdk.R;


import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;

public class NotificationUtility {


    public static void showNoInternetMessage(Activity activity) {
        Toast.makeText(activity, activity.getString(R.string.no_internet_message), Toast.LENGTH_LONG).show();
    }

    private static void errorMessage(Activity activity, String title, String message) {
        Toasty.error(activity, message, Toast.LENGTH_LONG).show();
    }

    private static void warningMessage(Activity activity, String title, String message) {
        Toasty.warning(activity, message, Toast.LENGTH_LONG).show();
    }

    private static void infoMessage(Activity activity, String title, String message) {
        Toasty.info(activity, message, Toast.LENGTH_LONG).show();
    }

    private static void successMessage(Activity activity, String title, String message) {
        Toasty.success(activity, message, Toast.LENGTH_LONG).show();
    }

    private static void toastMessage(Activity activity, String title, String message) {
        Toasty.normal(activity, message, Toast.LENGTH_LONG).show();
    }

    public static void showMessage(AlertType alertType, Activity activity, String title, String message) {
        switch (alertType) {

            case INFO:
                infoMessage(activity, title, message);
                break;

            case ERROR:
                errorMessage(activity, title, message);
                break;

            case SUCCESS:
                successMessage(activity, title, message);
                break;

            case WARNING:
                warningMessage(activity, title, message);
                break;

            case TOAST:
                infoMessage(activity, title, message);
                break;

            default:
                showSnakeBarMessage(activity, alertType, message);
                break;

        }
    }


    public static void showSnakeBarMessage(Activity activity, AlertType type, String message) {
        if (type == AlertType.WARNING_SNACK_BAR) {
            Snacky.builder()
                    .setActivity(activity)
                    .setText(message)
                    .setDuration(3000)
                    .warning()
                    .show();
        } else if (type == AlertType.ERROR_SNACK_BAR) {
            Snacky.builder()
                    .setActivity(activity)
                    .setText(message)
                    .setDuration(3000)
                    .error()
                    .show();
        } else if (type == AlertType.SUCCESS_SNACK_BAR) {
            Snacky.builder()
                    .setActivity(activity)
                    .setText(message)
                    .setDuration(3000)
                    .success()
                    .show();
        } else if (type == AlertType.INFO_SNACK_BAR) {
            Snacky.builder()
                    .setActivity(activity)
                    .setText(message)
                    .setDuration(3000)
                    .info()
                    .show();
        } else if (type == AlertType.ERROR_SNACK_BAR_WITH_BUTTON) {
            Snacky.builder()
                    .setActivity(activity)
                    .setText(message)
                    .setDuration(3000)
                    .error()
                    .setAction(activity.getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //do something...
                        }
                    })
                    .show();
        }
    }

}
