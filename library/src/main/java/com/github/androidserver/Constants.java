package com.github.androidserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class Constants {

    public static class Api {
        static List<String> list = new ArrayList<>();
        static Map<String, NanoHTTPD.Method> sMap = new HashMap<>();
        public static final String IMAGE = "/image";
        public static final String IMAGE_DELETE = "/image/delete";
        public static final String VIDEO = "/video";
        public static final String VIDEO_DELETE = "/video/delete";

        static {
            sMap.put(IMAGE, NanoHTTPD.Method.GET);
            sMap.put(VIDEO, NanoHTTPD.Method.GET);
            sMap.put(IMAGE_DELETE, NanoHTTPD.Method.DELETE);
            sMap.put(VIDEO_DELETE, NanoHTTPD.Method.DELETE);
        }

        public static final List<String> API = list;

        public static boolean isValidUri(String uri) {
            Enum method = sMap.get(uri);
            return method != null;
        }

        public static NanoHTTPD.Method getUriMethod(String uri) {
            NanoHTTPD.Method method = sMap.get(uri);
            return method;
        }
    }

    public static class Code {
        public static final int PORT = 8080;
        public static final int SIZE = 20;
        public static final int MAX_SIZE = 100;
        public static final int SUCCESS = 0;
    }

    public static class Key {
        public static final String REQUEST_ROOT = "/";
        public static final String ID = "id";
        public static final String PAGE_SIZE = "pageSize";
        public static final String PAGE_INDEX = "pageIndex";
        public static final String HTTP_PREFIX = "http://";
        public static final String IMAGE = "image";
        public static final String VIDEO = "video";
        public static final String DEFAULT_HOTSPOT_IP = "192.168.43.1";
        public static final String WIFI_HOTSPOT_NAME = "PilotHotspot";
        public static final String WIFI_HOTSPOT_PWD = "12345678";
        public static final String SUCCESS = "success";
    }
}
