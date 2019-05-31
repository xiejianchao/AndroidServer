package com.github.androidserver.utils;


import com.github.androidserver.Constants;
import com.github.androidserver.ContextProvider;

public class StringUtil {

    public static String convertPath2Url(String path) {
        String ip = IPUtil.getLocalIp(ContextProvider.getAppContext());
        return Constants.Key.HTTP_PREFIX + ip + ":" + Constants.Code.PORT + path;
    }

    public static String getFullServerIp(String serverIp) {
        return Constants.Key.HTTP_PREFIX + serverIp + ":" + Constants.Code.PORT;
    }

}
