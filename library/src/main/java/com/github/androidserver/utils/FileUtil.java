package com.github.androidserver.utils;

import android.text.TextUtils;

public class FileUtil {

    public static String getFileName(String path) {
        String fileName = null;
        try {
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            String[] split = path.split("/");
            fileName = split[split.length - 1];
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
