package com.github.androidserver.service;

import com.github.androidserver.ServerApplication;
import com.google.gson.Gson;

import java.util.List;

public abstract class BaseMedia<T> implements Media {

    protected ServerApplication mContext = ServerApplication.getInstance();

    public String toJson(Gson gson, List<T> list) {
        String json = gson.toJson(list);
        return json;
    }

    public String toJson(Gson gson, T t) {
        String json = gson.toJson(t);
        return json;
    }

}
