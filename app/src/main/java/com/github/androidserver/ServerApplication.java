package com.github.androidserver;

import android.app.Application;
import android.content.Context;

import com.github.androidserver.inject.DaggerServerComponent;
import com.github.androidserver.inject.ServerComponent;

import timber.log.Timber;

public class ServerApplication extends Application {

    private static ServerApplication sApplication;
    private ServerComponent mServerComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.tag("ANDROID_HTTP_SERVER");
        }
        mServerComponent = DaggerServerComponent.create();
    }

    public static ServerApplication getInstance() {
        return sApplication;
    }

    public static Context getContext() {
        return sApplication.getApplicationContext();
    }

    public ServerComponent getServerComponent() {
        return mServerComponent;
    }
}
