package com.github.androidserver.model;

import com.google.gson.Gson;

public abstract class BaseJsonModel {

    public String toJson() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

}
