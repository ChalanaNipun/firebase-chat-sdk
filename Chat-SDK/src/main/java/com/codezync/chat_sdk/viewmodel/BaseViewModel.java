package com.codezync.chat_sdk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

public class BaseViewModel extends AndroidViewModel {

    protected MutableLiveData<Boolean> isLoading;
    protected MutableLiveData<Boolean> onSuccess;
    protected MutableLiveData<String> onError;

    public BaseViewModel(@NonNull @NotNull Application application) {
        super(application);

        isLoading = new MutableLiveData<>();
        onError = new MutableLiveData<>();
        onSuccess = new MutableLiveData<>();
    }


    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(MutableLiveData<Boolean> isLoading) {
        this.isLoading = isLoading;
    }

    public MutableLiveData<Boolean> getOnSuccess() {
        return onSuccess;
    }

    public void setOnSuccess(MutableLiveData<Boolean> onSuccess) {
        this.onSuccess = onSuccess;
    }

    public MutableLiveData<String> getOnError() {
        return onError;
    }

    public void setOnError(MutableLiveData<String> onError) {
        this.onError = onError;
    }
}
