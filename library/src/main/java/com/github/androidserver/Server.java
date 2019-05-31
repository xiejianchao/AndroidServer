package com.github.androidserver;

import com.github.androidserver.manager.RequestManager;

import javax.inject.Inject;

import fi.iki.elonen.NanoHTTPD;
import timber.log.Timber;

public class Server extends NanoHTTPD {

    @Inject
    RequestManager mRequestManager;

    @Inject
    public Server() {
        super(Constants.Code.PORT);
        ContextProvider.getServerComponent().inject(this);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Timber.d("requestManager instance:" + mRequestManager);
        return mRequestManager.handlerResponse(session);
    }
}