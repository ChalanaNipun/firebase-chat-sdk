package com.codezync.chat_sdk.chat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codezync.adapter.ChatAdapter;
import com.codezync.chat_sdk.R;
import com.codezync.chat_sdk.databinding.ActivityChatBinding;
import com.codezync.chat_sdk.model.AdminSession;
import com.codezync.chat_sdk.model.ChatRequest;
import com.codezync.chat_sdk.model.Message;
import com.codezync.chat_sdk.model.NewMessageModel;
import com.codezync.chat_sdk.model.OpenChatResponse;
import com.codezync.chat_sdk.model.Sender;
import com.codezync.chat_sdk.repository.ChatService;
import com.codezync.chat_sdk.util.AlertType;
import com.codezync.chat_sdk.util.CodeZyncChat;
import com.codezync.chat_sdk.util.Constants;
import com.codezync.chat_sdk.util.Converter;
import com.codezync.chat_sdk.util.Customization;
import com.codezync.chat_sdk.util.LogUtil;
import com.codezync.chat_sdk.util.NotificationUtility;
import com.codezync.chat_sdk.util.PathUtil;
import com.codezync.chat_sdk.util.PermissionManager;
import com.codezync.chat_sdk.util.ProgressDialog;
import com.codezync.chat_sdk.util.Utility;
import com.codezync.chat_sdk.viewmodel.FirebaseViewModel;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.File;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatAdapter adapter;

    private FirebaseViewModel viewModel;
    private Dialog progressBar;
    private ChatRequest chatRequest;
    private OpenChatResponse openSession;
    private PermissionManager permissionManager;
    private String TAG = "ChatActivity";
    private int uploadingImageIndex;
    private boolean isExit, isChatClosed;

    private Handler handler = new Handler();
    private long delay = 1500; // 1 seconds after user stops typing
    private long last_text_edit = 0;
    private boolean isTyping;
    private Sender sender;
    private boolean isListenersRegistered;
    private MediaPlayer mediaPlayer;
    private Message uploadingImage;


    private Runnable messageExitTextChanger = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                if (isTyping) {
                    isTyping = false;
                    customerStoppedTyping();
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat);

        readBundle();
        init();
        setObservers();

        viewModel.loadChat();

        viewModel.setListenerForGetUserTyping();
        viewModel.getAdminContent();

    }

    private void readBundle() {

        String chatRequestAsText = getIntent().getStringExtra(Constants.BUNDLE_CHAT_REQUEST);

        if (Utility.isNotNull(chatRequestAsText)) {
            chatRequest = (ChatRequest) Utility.stringToObject(chatRequestAsText, ChatRequest.class);
            sender = Converter.createSender(chatRequest);
        } else {
            NotificationUtility.showMessage(AlertType.ERROR, this, "", getString(R.string.init_error));
            finish();
        }
    }


    private void goOnline() {
        viewModel.updateOnlineStatus(true);

        //if background chat service isStarted should pause
        ChatService.pauseService();
    }

    private void goOffline() {
        viewModel.updateOnlineStatus(false);
        //  viewModel.stopListeners();
    }

    private void exit() {

        if (isChatClosed) {
            ChatService.stopService();
        } else {
            ChatService.startService(viewModel);
        }

        if (isExit) {
            viewModel.stopListeners();
            finish();
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {

        //You can find all the below details in the google-services.json file, NOTE: All details are fake and random created API keys, please use your in the downloaded file.
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setProjectId("chatapp-65c94")
//                .setApplicationId("1:435022619084:android:2aad242d2318325cb3bd69")
//                .setApiKey("AIzaSyB3INAXT813NO3MjV2o8uXcus5qpeOuEDQ")
//                .build();
//
//        FirebaseApp.initializeApp(this,options,"com.codezync.chat_sdk");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        viewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);


        //UI Customizations
        applyUICustomizations();


        adapter = new ChatAdapter(this, sender.getSenderId());


        viewModel.init(this, chatRequest);
        permissionManager = new PermissionManager(this);


        if (getActionBar() != null) {
            getSupportActionBar().hide();
        }


        progressBar = ProgressDialog.createProgressDialog(this);

        LinearLayoutManager linearLayoutManagerPreference = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerview.setLayoutManager(linearLayoutManagerPreference);
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setHasFixedSize(false);


        binding.btnSend.setOnClickListener(view -> {
            validateAndSend();
        });

        binding.btnImage.setOnClickListener(view -> {
            openImagePicker();
        });


        binding.imgBack.setOnClickListener(view -> {
            isExit = true;
            goOffline();
        });

        binding.txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(messageExitTextChanger);

                if (!isTyping) {
                    isTyping = true;
                    customerOnTyping();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(messageExitTextChanger, delay);
            }
        });

    }

    private void customerOnTyping() {
        viewModel.updateTypingStatus(true);
    }

    private void customerStoppedTyping() {
        viewModel.updateTypingStatus(false);
    }


    private void openImagePicker() {
        if (permissionManager.checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.ACTIVITY_RESULTS_READ_STORAGE)) {
            openImageChooser();
        }
    }


    private void applyUICustomizations() {
        if (Customization.TITLE_TEXT_COLOR != 0) {
            binding.lblUserName.setTextColor(getColor(Customization.TITLE_TEXT_COLOR));
        }

        if (Customization.SUB_TITLE_TEXT_COLOR != 0) {
            binding.lblUserStatus.setTextColor(getColor(Customization.SUB_TITLE_TEXT_COLOR));
        }

        if (Customization.BACKGROUND_IMAGE != 0) {
            binding.llMain.setBackground(getDrawable(Customization.BACKGROUND_IMAGE));
        }

        if (Customization.HEADER_SHAPE != 0) {
            binding.llHeader.setBackground(getDrawable(Customization.HEADER_SHAPE));
        } else if (Customization.HEADER_COLOR != 0) {
            binding.llHeader.setBackgroundColor(getColor(Customization.HEADER_COLOR));
        }

        if (Customization.IS_ENABLED_IMAGE_SENDING) {
            binding.btnImage.setVisibility(View.VISIBLE);

            if (Customization.IMAGE_PICKER_ICON != 0) {
                binding.btnImage.setBackground(getDrawable(Customization.IMAGE_PICKER_ICON));
            }

        } else {
            binding.btnImage.setVisibility(View.GONE);
        }


        if (Customization.NEW_MESSAGE_SOUND != 0) {
            mediaPlayer = MediaPlayer.create(this, Customization.NEW_MESSAGE_SOUND);
        } else {
            int resID = getResources().getIdentifier(Constants.MESSAGE_TONE_FILE_NAME, "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(this, resID);
        }

        if (Customization.SEND_ICON != 0) {
            binding.btnSend.setImageDrawable(getDrawable(Customization.SEND_ICON));
        }

        if (Utility.isNotNull(Customization.MESSAGE_HINT)) {
            binding.txtMessage.setHint(Customization.MESSAGE_HINT);
        }

        if(Customization.IS_ENABLED_ADMINS_ONLINE_STATUS){
            binding.lblUserStatus.setVisibility(View.VISIBLE);
        }else {
            binding.lblUserStatus.setVisibility(View.GONE);

        }


    }


    private void validateAndSend() {
        if (!Utility.isNotNull(binding.txtMessage.getText().toString())) {
//            NotificationUtility.showMessage(AlertType.ERROR, this, "", getString(R.string.empty_message_error));

            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .repeat(0)
                    .playOn(binding.cardMessage);


        } else {
            viewModel.sendTextMessage(openSession.getSessionId(), binding.txtMessage.getText().toString());
            binding.txtMessage.setText("");
        }
    }


    private void setListenersAndUpdateStatus() {
//        if (!isListenersRegistered) {
//            isListenersRegistered = true;
//            viewModel.updateLastMessageAsSeen();
//        }

        //no need. it's handled in typing status observer
    }

    private void setObservers() {
        viewModel.openChatResponseMutableLiveData.observe(this, new Observer<OpenChatResponse>() {
            @Override
            public void onChanged(OpenChatResponse openChatResponse) {
                openSession = openChatResponse;
                if (openChatResponse != null && openChatResponse.getSessionResponse() != null) {

                    if (openChatResponse.getSessionResponse().getStatus().equals(Constants.OPEN_STATUS)) {
                        adapter.setData(openChatResponse.getSessionResponse().getMessage());
                        binding.recyclerview.getLayoutManager().scrollToPosition(adapter.messageList.size() - 1);

                        ifImageUploadingShowUploadingImage();
                        LogUtil.debug(TAG, "CHAT REFRESHED");

                        setListenersAndUpdateStatus();

                        if (adapter.haveChat()) {
                            binding.recyclerview.setVisibility(View.VISIBLE);
                            binding.llAnimation.setVisibility(View.GONE);
                            binding.llMessageContainer.setVisibility(View.VISIBLE);
                        } else {
                            binding.recyclerview.setVisibility(View.GONE);
                            setEmptyChatData();
                            binding.llAnimation.setVisibility(View.VISIBLE);
                            binding.llMessageContainer.setVisibility(View.VISIBLE);
                        }


                    } else { // chat is closed
                        isChatClosed = true;
                        Utility.hideSoftKeyboard(ChatActivity.this);
                        binding.recyclerview.setVisibility(View.GONE);
                        setChatCloseData();
                        binding.llAnimation.setVisibility(View.VISIBLE);
                        binding.llMessageContainer.setVisibility(View.GONE);


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isExit = true;
                                goOffline();

                            }
                        }, 5000);
                    }


                } else {
                    binding.recyclerview.setVisibility(View.GONE);
                    binding.llAnimation.setVisibility(View.VISIBLE);
                }


            }
        });

        viewModel.onProgress.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                if (aDouble == null) {
                    //image uploading failed !
                    uploadingImage = null;
                    binding.btnImage.setVisibility(View.VISIBLE);
                } else {
                    if (aDouble > 100) {
                        adapter.removeProgress();
                        uploadingImage = null;
                        binding.btnImage.setVisibility(View.VISIBLE);
                        LogUtil.debug(TAG, "Image Done !");
                    } else {
                        binding.btnImage.setVisibility(View.GONE);
                        adapter.setUploadingImageProgress(aDouble);
                        LogUtil.debug(TAG, "Image Uploading " + aDouble + "%");
                    }

                }
            }
        });

        viewModel.isLoadingChat.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    binding.chatShimmerLayout.setVisibility(View.VISIBLE);
                    binding.chatShimmerLayout.startShimmer();
                    binding.recyclerview.setVisibility(View.GONE);
                    binding.llAnimation.setVisibility(View.GONE);
                } else {
                    binding.chatShimmerLayout.setVisibility(View.GONE);
                    binding.chatShimmerLayout.stopShimmer();
//                    binding.recyclerview.setVisibility(View.GONE);
//                    binding.animationView.setVisibility(View.GONE);
                }
            }
        });


        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    if ((progressBar != null) && (!progressBar.isShowing())) {
                        progressBar.show();
                    }
                } else {
                    if ((progressBar != null) && (progressBar.isShowing())) {
                        progressBar.dismiss();
                    }
                }
            }
        });

        viewModel.getOnError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                binding.btnImage.setVisibility(View.VISIBLE);
                NotificationUtility.showMessage(AlertType.ERROR_SNACK_BAR, ChatActivity.this, "", error);
            }
        });

        viewModel.onOffline.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isOffline) {
                if (isOffline) {
                    exit();
                }
            }
        });


        viewModel.onNewMessageReceived.observe(this, new Observer<NewMessageModel>() {
            @Override
            public void onChanged(NewMessageModel messageModel) {
                CodeZyncChat.setOnMessageReceived(messageModel);
                if (Customization.IS_ENABLED_NEW_MESSAGE_SOUND) {
                    playReceivedMessageSound();
                }
            }
        });

        viewModel.onAdminStatusChange.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String status) {
                binding.lblUserStatus.setText(status);
            }
        });

        viewModel.adminContentMutableLiveData.observe(this, new Observer<AdminSession>() {
            @Override
            public void onChanged(AdminSession adminSession) {
                binding.lblUserName.setText(adminSession.getAdminContent().getSender().getName());
                Utility.loadImage(binding.imgAdmin, ChatActivity.this, adminSession.getAdminContent().getSender().getImageUrl(), R.drawable.img_pic_placeholder);
            }
        });


        viewModel.onLastMessageSeen.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                adapter.setLastMessageSeen(aBoolean);
            }
        });

        //detect keyboard status
        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (adapter.haveChat()) {
                            binding.recyclerview.getLayoutManager().scrollToPosition(adapter.messageList.size() - 1);
                        }
                    }
                });
    }


    private void ifImageUploadingShowUploadingImage() {
        if (uploadingImage != null) {
            adapter.addData(uploadingImage);
        }
    }


    private void setEmptyChatData() {
        binding.lblEmptyChatMessage.setText(getString(R.string.start_your_conversation));
        binding.animationView.setAnimation("empty_chat_animation.json");
        binding.animationView.playAnimation();
    }

    private void setChatCloseData() {
        binding.lblEmptyChatMessage.setText(getString(R.string.chat_done_message));
        binding.animationView.setAnimation("chat_done_animation.json");
        binding.animationView.playAnimation();
    }


    //----------permissions------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.ACTIVITY_RESULTS_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                permissionManager.noPermissionToast();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == Constants.IMAGE_CHOOSER_REQUEST_CODE & resultCode == RESULT_OK) {
                // upload choose image
                Uri tempUri = data.getData();
                //   String filePath = getPath(ChatActivity.this, tempUri);
//                resizedImage = new File(getPath(ChatActivity.this, tempUri));
                // File imageFile = Utility.resizeImage(filePath);
                uploadImage(null, tempUri);
            }
        }
    }


    private void uploadImage(File file, Uri uri) {
//        if (file != null) {
//            viewModel.sendImage(openSession.getSessionId(), file);
        //for testing upload to fire storage
        uploadingImageIndex = adapter.getItemCount();
        uploadingImage = new Message(Utility.getCurrentTimestamp(), sender, uri.toString(), Constants.IMAGE_CONTENT_TYPE, Constants.STATUS_UPLOADING);
        ifImageUploadingShowUploadingImage();
        //adapter.notifyDataSetChanged();
        binding.recyclerview.getLayoutManager().scrollToPosition(adapter.messageList.size() - 1);

        viewModel.uploadToFireStorage(openSession.getSessionId(), uri);
//        } else {
//            NotificationUtility.showMessage(AlertType.ERROR, this, "", getString(R.string.image_error));
//        }
    }

    public String getPath(Context context, Uri uriImage) {
        try {
            return PathUtil.getPath(context, uriImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openImageChooser() {
        Utility.openImageChooser(this, Constants.IMAGE_CHOOSER_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        isExit = true;
        goOffline();
    }

    @Override
    protected void onPause() {
        isExit = false;
        goOffline();
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        goOnline();
    }


    public void playReceivedMessageSound() {

        LogUtil.debug(TAG, "New Message received !");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.start();
        } else {
            mediaPlayer.start();
        }
    }
}