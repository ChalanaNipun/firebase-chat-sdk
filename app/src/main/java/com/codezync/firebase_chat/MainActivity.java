package com.codezync.firebase_chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import android.os.Bundle;
import android.util.Log;

import com.codezync.chat_sdk.model.ChatRequest;
import com.codezync.chat_sdk.model.NewMessageModel;
import com.codezync.chat_sdk.util.CodeZyncChat;
import com.codezync.chat_sdk.util.OnMessageReceivedListener;

public class MainActivity extends AppCompatActivity {

    static LifecycleOwner lifecycleOwner;
    static CodeZyncChat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lifecycleOwner = this;

        findViewById(R.id.hello).setOnClickListener(view -> {

            init();
        });

        init();

    }

    private void init() {
        ChatRequest request = new ChatRequest();
        request.setEmail("chalana@gmail.com");
        request.setEmailOrPhoneNo("chalana@gmail.com");
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}