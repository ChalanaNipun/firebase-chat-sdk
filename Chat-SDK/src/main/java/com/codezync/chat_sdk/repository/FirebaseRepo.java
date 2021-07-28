package com.codezync.chat_sdk.repository;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.codezync.chat_sdk.model.AdminContent;
import com.codezync.chat_sdk.model.AdminSession;
import com.codezync.chat_sdk.model.ChatRequest;
import com.codezync.chat_sdk.model.ContentData;
import com.codezync.chat_sdk.model.Message;
import com.codezync.chat_sdk.model.OpenChatResponse;
import com.codezync.chat_sdk.model.Sender;
import com.codezync.chat_sdk.model.SessionResponse;
import com.codezync.chat_sdk.model.User;
import com.codezync.chat_sdk.util.Constants;
import com.codezync.chat_sdk.util.Converter;
import com.codezync.chat_sdk.util.LogUtil;
import com.codezync.chat_sdk.util.OnNetworkResponseListener;
import com.codezync.chat_sdk.util.OnNetworkResponseListenerWithProgress;
import com.codezync.chat_sdk.util.OnValueChangeListener;
import com.codezync.chat_sdk.util.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRepo {

    private Activity activity;
    private FirebaseFirestore firebaseFirestore;
    private Sender sender;
    private String TAG = "FirebaseRepo";
    private StorageReference firebaseStorage;
    private StorageTask mUploadTask;
    private SessionResponse sessionResponse;
    private ChatRequest chatRequest;
    private ListenerRegistration chatListener, adminTypingListener, lastMessageListener;

    public FirebaseRepo(Activity activity, ChatRequest chatRequest) {
        this.activity = activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance().getReference("uploads");
        this.sender = Converter.createSender(chatRequest);
        this.chatRequest = chatRequest;
    }


    //    public void getAllSessions(OnNetworkResponseListener listener) {
//        firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).collection(Constants.CHAT_CONTENT_DOCUMENT).
//                addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
//
//                        List<String> sessionList = new ArrayList<>();
//                        for (DocumentChange documentChange : value.getDocumentChanges()) {
//                            sessionList.add(documentChange.getDocument().getId());
//                        }
//
//                        listener.onSuccessResponse(sessionList);
//                    }
//                });
//    }


    public void getChatById(String sessionId, OnNetworkResponseListener listener) {
        chatListener = firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).collection(Constants.CHAT_CONTENT_DOCUMENT).document(sessionId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value != null && value.getData() != null) {
                    SessionResponse response = (SessionResponse) Utility.stringToObject(new Gson().toJson(value.getData()), SessionResponse.class);
                    listener.onSuccessResponse(new OpenChatResponse(value.getId(), response));
                } else {
                    listener.onErrorResponse("cannot find chat for provided id : " + sessionId);
                }
            }
        });

    }


    public void getOpenSession(OnNetworkResponseListener listener) {
        firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).collection(Constants.CHAT_CONTENT_DOCUMENT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isFound = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    if (document.getData().get("status").equals(Constants.OPEN_STATUS)) {
                                        isFound = true;
                                        SessionResponse response = (SessionResponse) Utility.stringToObject(new Gson().toJson(document.getData()), SessionResponse.class);
                                        sessionResponse = response;
                                        listener.onSuccessResponse(new OpenChatResponse(document.getId(), response));
                                        break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (!isFound) {
//                                listener.onSuccessResponse(new OpenChatResponse("", null));
                                listener.onSuccessResponse(null);
                            }
                        } else {
                            listener.onErrorResponse(task.getException().getLocalizedMessage().toString());
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                listener.onErrorResponse(e.getLocalizedMessage().toString());
                Log.d(TAG, "Error getting documents: " + e.getLocalizedMessage());
            }
        });
    }


    public void sendMessage(Message message, String documentId, OnNetworkResponseListener listener) {
        if (Utility.isNotNull(documentId)) {
            firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).collection(Constants.CHAT_CONTENT_DOCUMENT)
                    .document(documentId).update(Constants.CHAT_MESSAGE_FIELD, FieldValue.arrayUnion(message))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

//                            updateLastMessage(message, documentId, listener);

                            // listener.onSuccessResponse(true);

                            if (!message.getStatus().equals(Constants.DEFAULT_MESSAGE_STATUS)) {
                                updateContent(message, listener, false);
                            }else {
                                listener.onSuccessResponse(true);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    listener.onErrorResponse(e.getLocalizedMessage());
                }
            });
        } else {

            List<Message> list = new ArrayList<>();
            list.add(message);


            User user = new User(sender.getSenderId(), Constants.OPEN_STATUS, message.getTimestamp(), list);
            firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).collection(Constants.CHAT_CONTENT_DOCUMENT)
                    .document().set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
//                            listener.onSuccessResponse(true);
                            //get Open Chat
//                            callOpenSessionFinder(listener);
                            if (!message.getStatus().equals(Constants.DEFAULT_MESSAGE_STATUS)) {
                                updateContent(message, listener, true);
                            }else {
                                listener.onSuccessResponse(true);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    listener.onErrorResponse(e.getLocalizedMessage());
                }
            });
        }
    }


    private void updateContent(Message message, OnNetworkResponseListener listener, boolean isFirstMessage) {

        if (isFirstMessage) {
            ContentData contentData = new ContentData(chatRequest, message, true, false, false, Constants.OPEN_STATUS, "");
            updateLastMessageContents(contentData, listener);
        } else {
            updateLastMessage(message, listener);
        }
    }


    public void updateLastMessage(Message message, String documentId, OnNetworkResponseListener listener) {

        firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).collection(Constants.CHAT_CONTENT_DOCUMENT)
                .document(documentId).update(Constants.CHAT_MESSAGE_FIELD, FieldValue.arrayUnion(message))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccessResponse(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                listener.onErrorResponse(e.getLocalizedMessage());
            }
        });
    }


    public void deleteMessage(Message message, String documentId, OnNetworkResponseListener listener) {
        firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).collection(Constants.CHAT_CONTENT_DOCUMENT)
                .document(documentId).update(Constants.CHAT_MESSAGE_FIELD, FieldValue.arrayRemove(message))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccessResponse(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                listener.onErrorResponse(e.getLocalizedMessage());
            }
        });
    }


    public void uploadFile(Uri mImageUri, OnNetworkResponseListenerWithProgress listener) {
        if (mImageUri != null) {
            LogUtil.debug(TAG, "uploadingFile...");
            StorageReference fileReference = firebaseStorage.child(System.currentTimeMillis()
                    + "." + Utility.getFileExtension(activity, mImageUri));
            mUploadTask = mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                                    listener.onSuccessResponse(task.getResult().toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onErrorResponse(e.getLocalizedMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            listener.onProgress(progress);
                        }
                    });


        } else {
            LogUtil.debug(TAG, "uploadingFile > file is empty");
        }
    }


    public void updateOnlineStatus(boolean isOnline, OnNetworkResponseListener listener) {
        firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).update("customerOnline", isOnline).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.onSuccessResponse(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                listener.onErrorResponse(e.getLocalizedMessage());
            }
        });
    }

    public void updateTypingStatus(boolean isTyping, OnNetworkResponseListener listener) {
        firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).update("customerTyping", isTyping).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.onSuccessResponse(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                listener.onErrorResponse(e.getLocalizedMessage());
            }
        });
    }


    public void updateLastMessageContents(ContentData contentData, OnNetworkResponseListener listener) {
        firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).set(contentData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.onSuccessResponse(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                listener.onErrorResponse(e.getLocalizedMessage());
            }
        });
    }

    public void updateLastMessage(Message message, OnNetworkResponseListener listener) {
        firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).update("lastMessage", message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.onSuccessResponse(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                listener.onErrorResponse(e.getLocalizedMessage());
            }
        });
    }


    public void updateLastMessageStatus(String status, OnNetworkResponseListener listener) {

        firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).update("lastMessage.status", status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.onSuccessResponse(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                listener.onErrorResponse(e.getLocalizedMessage());
            }
        });
    }


    public void observeUserTyping(OnValueChangeListener listener) {
        adminTypingListener = firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value != null && value.getData() != null) {
                    ContentData contentData = (ContentData) Utility.stringToObject(new Gson().toJson(value.getData()), ContentData.class);
                    listener.onSuccessResponse(contentData);
                }
            }
        });

    }


//    public void observeLastMessage(OnValueChangeListener listener) {
//        adminTypingListener = firebaseFirestore.collection(Constants.CHAT_COLLECTION_PATH).document(sender.getSenderId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
//                LogUtil.debug(TAG, "Changed typing status...");
//                ContentData contentData = (ContentData) Utility.stringToObject(new Gson().toJson(value.getData()), ContentData.class);
//                listener.onSuccessResponse(contentData);
//            }
//        });
//
//    }


//    public void observeAdminStatus(String adminId, OnValueChangeListener listener) {
//        adminContentListener = firebaseFirestore.collection(Constants.ADMIN_COLLECTION_PATH).document(adminId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
//                AdminContent adminContent = (AdminContent) Utility.stringToObject(new Gson().toJson(value.getData()), AdminContent.class);
//                listener.onSuccessResponse(adminContent);
//            }
//        });
//
//    }

    public void getAdminContent(OnNetworkResponseListener listener) {
        firebaseFirestore.collection(Constants.ADMIN_COLLECTION_PATH).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    AdminContent adminContent = (AdminContent) Utility.stringToObject(new Gson().toJson(document.getData()), AdminContent.class);
                    listener.onSuccessResponse(new AdminSession(document.getId(), adminContent));
                    break;
                }
            }
        });

    }

    public void stopAllListener() {

        LogUtil.debug(TAG, "REMOVED OBSERVERS");

        if (chatListener != null) {
            chatListener.remove();
            chatListener = null;
        }

//        if (adminTypingListener != null) {
//            adminTypingListener.remove();
//            adminTypingListener = null;
//        }

        if (lastMessageListener != null) {
            lastMessageListener.remove();
            lastMessageListener = null;
        }
    }

}
