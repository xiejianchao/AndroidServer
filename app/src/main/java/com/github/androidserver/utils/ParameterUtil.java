package com.github.androidserver.utils;

import com.github.androidserver.Constants;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class ParameterUtil {

    public static List<String> getIds(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> params = session.getParameters();
        return params.get(Constants.Key.ID);
    }

}
