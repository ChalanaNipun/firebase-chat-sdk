package com.codezync.chat_sdk.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static String objectToString(Object object) {
        return new Gson().toJson(object);
    }

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String TIME_DISPLAY_FORMAT = "MMM dd, yyyy hh:mm a";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ENGLISH);
    private static final SimpleDateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat(TIME_DISPLAY_FORMAT, Locale.ENGLISH);


    public static Object stringToObject(String string, Class clz) {
        if (isNotNull(string)) {
            return new Gson().fromJson(string, clz);
        } else {
            return null;
        }
    }

    public static String toDisplayDateFormat(String date) {
        String simpleDate = "";
        try {
            Date current = DATE_FORMAT.parse(date);
            simpleDate = DATE_DISPLAY_FORMAT.format(current);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return simpleDate;
    }


    public static boolean isNotNull(String value) {
        if (value == null) {
            return false;
        } else if (value.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }


    public static void hideSoftKeyboard(Activity activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    View view = activity.getCurrentFocus();
                    if (view == null) {
                        view = new View(activity);
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } catch (Exception e) {

                }
            }
        }, 1000);
    }

    public static void loadImage(ImageView imageView, Context context, String url, int placeHolderImage) {

//        Glide.with(context).load(url)
//                .asBitmap().centerCrop()
//                .error(placeHolderImage)
//                .placeholder(placeHolderImage)
//                .into(imageView);

        if(isNotNull(url)){
            Picasso.with(context)
                    .load(url).placeholder(placeHolderImage).error(placeHolderImage).into(imageView);
        }

    }

    public static void loadImage(ImageView imageView, Context context, int  url, int placeHolderImage) {

//        Glide.with(context).load(url)
//                .asBitmap().centerCrop()
//                .error(placeHolderImage)
//                .placeholder(placeHolderImage)
//                .into(imageView);


            Picasso.with(context)
                    .load(url).placeholder(placeHolderImage).error(placeHolderImage).into(imageView);


    }

    public static String getCurrentTimestamp() {
        return DATE_FORMAT.format(new Date());
    }

    public static String getSystemTimeAsLong() {
        return Calendar.getInstance().getTimeInMillis() + "";
    }

    public static void openImageChooser(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }


    public static File resizeImage(String in) {
        String path = Environment.getExternalStorageDirectory() + "/" + Constants.RESIZE_IMAGE_OUTPUT_FOLDER;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }

        String fileName = getSystemTimeAsLong() + ".png";

        return ImageResize(in, (path + "/" + fileName));
    }

    public static String getFileExtension(Activity activity, Uri uri) {
        ContentResolver cR = activity.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    public static File ImageResize(String in, String out) {

        File resizedImage = null;
        File imgFileOrig = null;

        try {
            imgFileOrig = new File(in);
            Bitmap b = BitmapFactory.decodeFile(imgFileOrig.getAbsolutePath());
            int origWidth = b.getWidth();
            int origHeight = b.getHeight();

            final int destWidth = 1000;

            if (origWidth > destWidth) {


                int destHeight = origHeight / (origWidth / destWidth);

//                int destHeight = 1000;
                // we create an scaled bitmap so it reduces the image, not just trim it
                Bitmap b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight, false);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                // compress to the format you want, JPEG, PNG...
                // 70 is the 0-100 quality percentage
                b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
                // we save the file, at least until we have made use of it
                File f = new File(out);
                f.createNewFile();
                //write the bytes in file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(outStream.toByteArray());
                // remember close de FileOutput
                fo.close();

                resizedImage = f;
            }
        } catch (Exception e) {
            Log.e("Utility", "Resize failed");
            resizedImage = imgFileOrig;
        }

        return resizedImage;
    }
}
