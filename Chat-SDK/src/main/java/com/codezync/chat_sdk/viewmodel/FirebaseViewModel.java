package com.codezync.chat_sdk.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.codezync.chat_sdk.R;
import com.codezync.chat_sdk.model.AdminSession;
import com.codezync.chat_sdk.model.ChatRequest;
import com.codezync.chat_sdk.model.ContentData;
import com.codezync.chat_sdk.model.Message;
import com.codezync.chat_sdk.model.NewMessageModel;
import com.codezync.chat_sdk.model.OpenChatResponse;
import com.codezync.chat_sdk.model.Sender;
import com.codezync.chat_sdk.repository.FirebaseRepo;
import com.codezync.chat_sdk.util.Constants;
import com.codezync.chat_sdk.util.Converter;
import com.codezync.chat_sdk.util.LogUtil;
import com.codezync.chat_sdk.util.NotificationUtility;
import com.codezync.chat_sdk.util.OnNetworkResponseListener;
import com.codezync.chat_sdk.util.OnNetworkResponseListenerWithProgress;
import com.codezync.chat_sdk.util.OnValueChangeListener;
import com.codezync.chat_sdk.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FirebaseViewModel extends BaseViewModel {


    public MutableLiveData<OpenChatResponse> openChatResponseMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isUploadingImage = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSendingMessage = new MutableLiveData<>();
    public MutableLiveData<Double> onProgress = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoadingChat = new MutableLiveData<>();
    public MutableLiveData<Boolean> onOffline = new MutableLiveData<>();
    public MutableLiveData<Boolean> onLastMessageSeen = new MutableLiveData<>();
    public MutableLiveData<AdminSession> adminContentMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> onAdminStatusChange = new MutableLiveData<>();
    public MutableLiveData<NewMessageModel> onNewMessageReceived = new MutableLiveData<>();
    private FirebaseRepo repo;
    private AdminSession adminSession;

    private Sender sender;
    private Activity activity;
    private String lastMessageWithDateTime = "";


    public void init(Activity activity, ChatRequest chatRequest) {
        this.sender = sender;
        this.activity = activity;
        sender = Converter.createSender(chatRequest);
        repo = new FirebaseRepo(activity, chatRequest);
    }

    private void onLoading() {
        isLoading.postValue(true);
    }

    private void onSendingMessage() {
        isSendingMessage.postValue(true);
    }

    private void onUploadingImage() {
        isUploadingImage.postValue(true);
    }

    private void stopLoading() {
        isLoading.postValue(false);
    }

    private void stopSendingMessage() {
        isSendingMessage.postValue(false);
    }

    private void stopUploading() {
        isUploadingImage.postValue(false);
    }


    private void onLoadingChat() {
        isLoadingChat.postValue(true);
    }

    private void stopLoadingChat() {
        isLoadingChat.postValue(false);
    }

    public FirebaseViewModel(@NonNull @NotNull Application application) {
        super(application);
    }


    private void getChats(String sessionId) {
        onLoadingChat();
        repo.getChatById(sessionId, new OnNetworkResponseListener<OpenChatResponse, String>() {
            @Override
            public void onSuccessResponse(OpenChatResponse response) {
                stopLoadingChat();
                openChatResponseMutableLiveData.postValue(response);

                //detect user 1st message
                if (response.getSessionResponse().getMessage().size() == 2) {
                    //send default message from app side
                    if (adminSession != null) {
                        Message defaultMessage = new Message(Utility.getCurrentTimestamp(), adminSession.getAdminContent().getSender(), adminSession.getAdminContent().getDefaultMessage(), Constants.TEXT_CONTENT_TYPE, Constants.DEFAULT_MESSAGE_STATUS);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendMessage(sessionId, defaultMessage);
                            }
                        }, 1000);
                    }

                }

            }

            @Override
            public void onErrorResponse(String response) {
                stopLoadingChat();
                onError.postValue(response);
            }

            @Override
            public void onNetworkError() {
                stopLoadingChat();
                NotificationUtility.showNoInternetMessage(activity);
            }
        });
    }


    public void loadChat() {
        onLoadingChat();
        repo.getOpenSession(new OnNetworkResponseListener<OpenChatResponse, String>() {
            @Override
            public void onSuccessResponse(OpenChatResponse response) {
                stopLoadingChat();
                if (response == null) { //Not available open chat
                    sendInitMessage();
                } else {
                    getChats(response.getSessionId());
                }
            }

            @Override
            public void onErrorResponse(String response) {
                stopLoadingChat();
                onError.postValue(response);
            }

            @Override
            public void onNetworkError() {
                stopLoadingChat();
                NotificationUtility.showNoInternetMessage(activity);
            }
        });
    }


    private void sendInitMessage() {
        // onLoading();
        Message message = new Message(Utility.getCurrentTimestamp(), sender, "", Constants.TEXT_CONTENT_TYPE, Constants.STATUS_HIDDEN);
        repo.sendMessage(message, "", new OnNetworkResponseListener<Boolean, String>() {
            @Override
            public void onSuccessResponse(Boolean response) {
                //   stopLoading();
                if (response) {
                    loadChat();
                } else {
                    try {
                        throw new Exception(activity.getString(R.string.init_message_error));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onErrorResponse(String response) {
                // stopLoading();
                onError.postValue(response);
            }

            @Override
            public void onNetworkError() {
                // stopLoading();
                NotificationUtility.showNoInternetMessage(activity);
            }
        });
    }


    private void sendMessage(String sessionId, Message message) {


        if (message.getContentType().equals(Constants.TEXT_CONTENT_TYPE)) {
            onSendingMessage();
        } else {
            onUploadingImage();
        }

        repo.sendMessage(message, Utility.isNotNull(sessionId) ? sessionId : "", new OnNetworkResponseListener<Boolean, String>() {
            @Override
            public void onSuccessResponse(Boolean response) {

                if (message.getContentType().equals(Constants.TEXT_CONTENT_TYPE)) {
                    stopSendingMessage();
                } else {
                    stopUploading();
                }

                onSuccess.postValue(response);
            }

            @Override
            public void onErrorResponse(String response) {
                if (message.getContentType().equals(Constants.TEXT_CONTENT_TYPE)) {
                    stopSendingMessage();
                } else {
                    stopUploading();
                }

                onError.postValue(response);
            }

            @Override
            public void onNetworkError() {
                if (message.getContentType().equals(Constants.TEXT_CONTENT_TYPE)) {
                    stopSendingMessage();
                } else {
                    stopUploading();
                }
                NotificationUtility.showNoInternetMessage(activity);
            }
        });
    }

    private void sendMessage(String sessionId, String txtMessage, String contentType) {

        Message message = new Message(Utility.getCurrentTimestamp(), sender, txtMessage, contentType, Constants.DELIVERED_MESSAGE_STATUS);

        if (contentType.equals(Constants.TEXT_CONTENT_TYPE)) {
            onSendingMessage();
        } else {
            onUploadingImage();
        }

        repo.sendMessage(message, Utility.isNotNull(sessionId) ? sessionId : "", new OnNetworkResponseListener<Boolean, String>() {
            @Override
            public void onSuccessResponse(Boolean response) {

                if (contentType.equals(Constants.TEXT_CONTENT_TYPE)) {
                    stopSendingMessage();
                } else {
                    stopUploading();
                }

                onSuccess.postValue(response);
            }

            @Override
            public void onErrorResponse(String response) {
                if (contentType.equals(Constants.TEXT_CONTENT_TYPE)) {
                    stopSendingMessage();
                } else {
                    stopUploading();
                }

                onError.postValue(response);
            }

            @Override
            public void onNetworkError() {
                if (contentType.equals(Constants.TEXT_CONTENT_TYPE)) {
                    stopSendingMessage();
                } else {
                    stopUploading();
                }
                NotificationUtility.showNoInternetMessage(activity);
            }
        });
    }


    private void uploadImage(String sessionId, File file) {
        //upload part here.
        //TODO : upload is done should send it to firestore as a message

        //for fire storage


        //  sendMessage(sessionId, "URL HERE", Constants.IMAGE_CONTENT_TYPE);
    }

    public void uploadToFireStorage(String sessionId, Uri uri) {

        LogUtil.debug("TAG", "5");
        onProgress.postValue(0.0);
        repo.uploadFile(uri, new OnNetworkResponseListenerWithProgress<String, String>() {
            @Override
            public void onSuccessResponse(String response) {
                LogUtil.debug("TAG", "6");
                onProgress.postValue(101.0);
                sendMessage(sessionId, response, Constants.IMAGE_CONTENT_TYPE);
            }

            @Override
            public void onErrorResponse(String response) {
                LogUtil.debug("TAG", "7");
                onProgress.postValue(null);
                onError.postValue(response);
            }

            @Override
            public void onProgress(double response) {
                onProgress.postValue(response);
            }

            @Override
            public void onNetworkError() {
                onProgress.postValue(null);
                NotificationUtility.showNoInternetMessage(activity);
            }
        });


    }

    public void sendTextMessage(String sessionId, String message) {
        sendMessage(sessionId, message, Constants.TEXT_CONTENT_TYPE);
    }

    public void sendImage(String sessionId, File file) {
        uploadImage(sessionId, file);
    }


    public void updateOnlineStatus(boolean isOnline) {
        if (!isOnline) {
            onLoading();
        }

        repo.updateOnlineStatus(isOnline, new OnNetworkResponseListener<Boolean, String>() {
            @Override
            public void onSuccessResponse(Boolean response) {

                if (!isOnline) {
                    stopLoading();
                    onOffline.postValue(response);
                }
            }

            @Override
            public void onErrorResponse(String response) {
                if (!isOnline) {
                    stopLoading();
                }
                //  onError.postValue(response);
            }

            @Override
            public void onNetworkError() {
                if (!isOnline) {
                    stopLoading();
                }
                NotificationUtility.showNoInternetMessage(activity);
            }
        });
    }

    public void updateTypingStatus(boolean isTyping) {
        // onLoading();
        repo.updateTypingStatus(isTyping, new OnNetworkResponseListener<Boolean, String>() {
            @Override
            public void onSuccessResponse(Boolean response) {
                // stopLoading();
            }

            @Override
            public void onErrorResponse(String response) {
                //  stopLoading();
                // onError.postValue(response);
            }

            @Override
            public void onNetworkError() {
                //  stopLoading();
                NotificationUtility.showNoInternetMessage(activity);
            }
        });
    }


    public void setListenerForGetUserTyping() {
        repo.observeUserTyping(new OnValueChangeListener<ContentData, String>() {
            @Override
            public void onSuccessResponse(ContentData response) {
                if (response != null) {
                    onAdminStatusChange.postValue(response.isAdminTyping() ? activity.getString(R.string.typing) : response.isAdminOnline() ? activity.getString(R.string.online) : activity.getString(R.string.offline));
                    //set last message status
                    if (response.getLastMessage() != null) {
                        if (response.getLastMessage().isSentMessage(sender.getSenderId())) {
                            onLastMessageSeen.postValue(response.getLastMessage().getStatus().equals(Constants.SEEN_STATUS) ? true : false);
                        } else {
                            onLastMessageSeen.postValue(true);
                            if (!response.getLastMessage().getStatus().equals(Constants.SEEN_STATUS)) {
                                //if new message notifyToUser
                                if (!lastMessageWithDateTime.equals(Converter.createIdentifier(response.getLastMessage()))) {
                                    lastMessageWithDateTime = Converter.createIdentifier(response.getLastMessage());
                                    onNewMessageReceived.postValue(new NewMessageModel(lastMessageWithDateTime,response));
                                }
                                updateLastMessageAsSeen();
                            }
                        }
                    }


                }
            }

            @Override
            public void onErrorResponse(String response) {
                onError.postValue(response);
                LogUtil.debug("OBS", "error");
            }
        });
    }


    public void getAdminContent() {
        repo.getAdminContent(new OnNetworkResponseListener<AdminSession, String>() {
            @Override
            public void onSuccessResponse(AdminSession response) {
                if (response != null) {
                    adminSession = response;
                    adminContentMutableLiveData.postValue(response);
                }
            }

            @Override
            public void onErrorResponse(String response) {
                onError.postValue(response);
            }

            @Override
            public void onNetworkError() {
                NotificationUtility.showNoInternetMessage(activity);
            }
        });
    }


    public void stopListeners() {
        repo.stopAllListener();
    }

    public void updateLastMessageAsSeen() {
        repo.updateLastMessageStatus(Constants.SEEN_STATUS, new OnNetworkResponseListener<Boolean, String>() {
            @Override
            public void onSuccessResponse(Boolean response) {

            }

            @Override
            public void onErrorResponse(String response) {

            }

            @Override
            public void onNetworkError() {

            }
        });
    }

}
