package com.github.androidserver.utils;


import com.github.androidserver.Constants;
import com.github.androidserver.ServerApplication;

public class StringUtil {

    public static String convertPath2Url(String path) {
        String ip = IPUtils.getLocalIp(ServerApplication.getContext());
        return Constants.Key.HTTP_PREFIX + ip + ":" + Constants.Code.PORT + path;
    }

}
