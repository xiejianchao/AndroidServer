package com.github.androidserver.server;

import com.github.androidserver.Constants;
import com.github.androidserver.ServerApplication;
import com.github.androidserver.manager.RequestManager;

import javax.inject.Inject;

import fi.iki.elonen.NanoHTTPD;
import timber.log.Timber;

public class Server extends NanoHTTPD {

    @Inject
    RequestManager mRequestManager;

    public Server() {
        super(Constants.Code.PORT);
        ServerApplication.getInstance().getServerComponent().inject(this);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Timber.d("requestManager instance:" + mRequestManager);
        return mRequestManager.handlerResponse(session);
    }
}