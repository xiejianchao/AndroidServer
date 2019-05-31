package com.github.androidserver;

public interface OnConnectListener {

    void onConnecting();
    void onSuccess(String ip);
    void onFailure(Exception e);

}