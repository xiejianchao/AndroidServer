package com.github.androidserver;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.github.androidserver.injection.DaggerServerComponent;
import com.github.androidserver.injection.ServerComponent;

import timber.log.Timber;

public class ContextProvider extends ContentProvider {

    private static Context mContext;
    private static ServerComponent mServerComponent;
    
    @Override
    public boolean onCreate() {
        mContext = getContext();
        initTimber();
        mServerComponent = DaggerServerComponent.create();
        return true;
    }

    public static Context getAppContext() {
        return mContext;
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static ServerComponent getServerComponent() {
        return mServerComponent;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
