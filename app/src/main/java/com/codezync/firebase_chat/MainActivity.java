package com.codezync.firebase_chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.codezync.chat_sdk.model.ChatRequest;
import com.codezync.chat_sdk.model.NewMessageModel;
import com.codezync.chat_sdk.util.CodeZyncChat;
import com.codezync.chat_sdk.util.OnMessageReceivedListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static LifecycleOwner lifecycleOwner;
    static CodeZyncChat chat;

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

            init();
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
            chat = CodeZyncChat.init(this, request);
            chat.startChat();

//                chat.onMessageReceived.observe(this, new Observer<String>() {
//                    @Override
//                    public void onChanged(String s) {
//                        Log.e("CHALANA","OnMessageReceived");
//                    }
//                });

            chat.OnMessageReceived(new OnMessageReceivedListener<NewMessageModel>() {
                @Override
                public void onReceive(NewMessageModel response) {
                    Log.e("CHALANA", "OnMessageReceived-----");
                }
            });


//            chat.setEnabledMessageSeenStatus(false);
//            chat.setEnabledSentDate(false);
            chat.setEnabledImageSending(true);
            chat.setSeenIcon(R.drawable.double_tick);
            chat.setSenderBackgroundColor(R.color.purple_200);
            chat.setHeaderShape(R.drawable.header_new);
            chat.setBackgroundImage(R.drawable.gb);
            chat.setHeaderHeight(180);
            chat.setEnabledSenderIcon(false);
            chat.setChatBubbles(R.drawable.bubble_sent, R.drawable.bubble_received);
            chat.setReceivedMessageTextColor(R.color.white);
            chat.setEnabledEmptyChatAnimation(true);
            chat.setIsArabicLanguage(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}