package com.codezync.firebase_chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import android.os.Bundle;
import android.view.ViewGroup;

import com.codezync.chat_sdk.model.ChatRequest;
import com.codezync.chat_sdk.util.CZChat;
import com.codezync.chat_sdk.util.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    static LifecycleOwner lifecycleOwner;
    static CZChat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Locale locale= new Locale("ar");
//        Configuration configuration = getResources().getConfiguration();
//        configuration.setLayoutDirection(locale);
//        configuration.setLocale(locale);
//        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);

        lifecycleOwner = this;

        findViewById(R.id.hello).setOnClickListener(view -> {

//            init();
            final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
            final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            final String utcTime = sdf.format(new Date());

            System.out.println(utcTime);
        });

        findViewById(R.id.button).setOnClickListener(view -> {

            Toasty.error(getBaseContext(), "Hi chalana", Toasty.LENGTH_LONG).show();
        });

        init();

    }

    private void init() {
        ChatRequest request = new ChatRequest("chalana@gmail.com", "patpatGO Customer Care", R.drawable.attachment_icon);
        request.setEmail("chalana@gmail.com");
        request.setAppVersion("1.0.0");
        request.setPhoneNo("0716359376");
        request.setName("Chalana Nupun");
        request.setDeviceName("S9+");
        request.setImageUrl("https://i.ibb.co/Hx4RmGK/me.png");
        request.setPlatform("Android");

        try {
            final ViewGroup root = Utility.getRootView(this);
            chat = CZChat.client().registerUser(this, request, root);
            chat.startChat();


//                chat.onMessageReceived.observe(this, new Observer<String>() {
//                    @Override
//                    public void onChanged(String s) {
//                        Log.e("CHALANA","OnMessageReceived");
//                    }
//                });

//            chat.OnMessageReceived(new OnMessageReceivedListener<NewMessageModel>() {
//                @Override
//                public void onReceive(NewMessageModel response) {
//                    Log.e("CHALANA", "OnMessageReceived-----");
//                }
//            });
//
//
//            chat.setEnabledMessageSeenStatus(false);
//            chat.setEnabledSentDate(false);
//            chat.setEnabledImageSending(true);
//            chat.setSeenIcon(R.drawable.double_tick);
//            chat.setSenderBackgroundColor(R.color.purple_200);
//            chat.setHeaderShape(R.drawable.header_new);
//            chat.setBackgroundImage(R.drawable.gb);
//            chat.setHeaderHeight(180);
//            chat.setEnabledSenderIcon(false);
//            chat.setChatBubbles(R.drawable.bubble_sent, R.drawable.bubble_received);
//            chat.setReceivedMessageTextColor(R.color.white);
//            chat.setEnabledEmptyChatAnimation(true);
//            chat.setIsArabicLanguage(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}