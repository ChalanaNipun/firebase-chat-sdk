package com.codezync.chat_sdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;


import com.codezync.chat_sdk.R;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.checkSelfPermission;


public class PermissionManager {
    private static final String TAG = PermissionManager.class.getSimpleName();

    public static final int PERMISSIONS_REQUEST_CODE = 1240;
    private boolean isConnected;
    private Context mContext;
    private Activity mActivity;
    private PermissionManager instance;

    public PermissionManager(Activity activity) {
        mContext = activity;
        mActivity = activity;
        instance = this;
    }


    //ToDo: internet checking part moved to InernetManager : Chalana Nupun
//    public boolean isNetworkAvailable() {
//        Log.d(TAG, "Executing isNetworkAvailable");
//        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        isConnected = activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//
//        return isConnected;
//    }

    public boolean isGpsEnabled() {
        Log.d(TAG, "Executing isGpsEnabled");
        LocationManager mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            Log.d(TAG, "Executing disabled again");
            return false;
        }
    }

    public boolean checkPermission(String[] appPermission) {

        List<String> listPermissionNeeded = new ArrayList<>();
        for (String perm : appPermission) {
            if (checkSelfPermission(mContext, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(perm);
            }
        }

        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(mActivity,
                    listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                    PERMISSIONS_REQUEST_CODE
            );
            return false;
        }

        return true;

    }


    public boolean checkPermissions(String[] required) {
        boolean isGranted = true;
        List<String> deniedPermissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : required) {
                LogUtil.debug("checking", permission);
                if (mContext.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                    isGranted = false;
                    deniedPermissions.add(permission);
                }
            }
        } else {
            return true;
        }

        if (!isGranted) {
            String[] requestPermissions = new String[deniedPermissions.size()];
            for (int i = 0; i < deniedPermissions.size(); i++) {
                requestPermissions[i] = deniedPermissions.get(i);
            }
            mActivity.requestPermissions(requestPermissions, PERMISSIONS_REQUEST_CODE);
        }
        return isGranted;
    }

    public boolean checkPermissions(String[] required, int REQUEST_CODE) {
        boolean isGranted = true;
        List<String> deniedPermissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : required) {
                LogUtil.debug("checking", permission);
                if (mContext.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                    isGranted = false;
                    deniedPermissions.add(permission);
                }
            }
        } else {
            return true;
        }

        if (!isGranted) {
            String[] requestPermissions = new String[deniedPermissions.size()];
            for (int i = 0; i < deniedPermissions.size(); i++) {
                requestPermissions[i] = deniedPermissions.get(i);
            }
            mActivity.requestPermissions(requestPermissions, REQUEST_CODE);
        }
        return isGranted;
    }

    public boolean onRequestPermissionsResult(int requestCode, String[] permissions) {
        boolean isGranted = true;
//        LogUtil.debug("permission","request code > "+requestCode);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
//                LogUtil.debug("permission","request code matched ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                    List<String> deniedPermissions = new ArrayList<>();
                    for (String permission : permissions) {

                        if (mContext.checkSelfPermission(permission) !=
                                PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                            isGranted = false;
//                            LogUtil.debug("permission","checking granted > "+permission);
                        }
                    }

                    if (!isGranted) {
                        String allPermissions = "", curretPermission = "";
                        String[] requestPermissions = new String[deniedPermissions.size()];
                        for (int i = 0; i < deniedPermissions.size(); i++) {
                            requestPermissions[i] = deniedPermissions.get(i);
                            curretPermission = deniedPermissions.get(i).split("android.permission.")[1];
                            if (curretPermission.contains("_")) {
                                if (curretPermission.split("_").length == 2) {
                                    curretPermission = curretPermission.split("_")[0] + " " + curretPermission.split("_")[1];
                                } else {
                                    String simpleName = "";
                                    for (String name : curretPermission.split("_")) {
                                        simpleName += name + " ";
                                    }
                                    curretPermission = simpleName;
                                }
                            }
                            allPermissions += curretPermission + "\n";
                        }
                        NotificationUtility.showMessage(AlertType.ERROR, mActivity, "", mActivity.getString(R.string.permissions_denied));
                    }
                }

        }
        return isGranted;
    }


    public boolean isGPSEnabled(Activity activity) {
        LocationManager mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            LogUtil.debug(TAG, "Executing disabled again");
            return false;
        }
    }


    public void requestPermissions(String[] permissions, int REQUEST_CODE) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissions[0])) {
        } else {
            ActivityCompat.requestPermissions(mActivity, permissions, REQUEST_CODE);
        }
    }
//    private void turnGPSOn(Activity activity){
//        String provider = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//
//        if(!provider.contains("gps")){ //if gps is disabled
//            final Intent poke = new Intent();
//            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//            poke.setData(Uri.parse("3"));
//            sendBroadcast(poke);
//        }
//    }

    public void noPermissionToast() {

        NotificationUtility.showMessage(AlertType.ERROR, mActivity, "", mActivity.getString(R.string.permissions_denied));
    }
}
