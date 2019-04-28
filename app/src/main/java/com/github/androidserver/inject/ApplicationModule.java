package com.github.androidserver.inject;

import android.content.Context;

import com.github.androidserver.ServerApplication;
import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    @ServerScope
    @Provides
    public Context providerContext() {
        return ServerApplication.getContext();
    }

    @ServerScope
    @Provides
    public Gson providerGson() {
        return new Gson();
    }

}
