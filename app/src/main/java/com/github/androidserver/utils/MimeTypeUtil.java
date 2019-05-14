package com.github.androidserver.utils;

public class MimeTypeUtil {

    public static final String MIME_JSON = "application/json";

    public static String getMimeType(String uri) {
        String filename = uri.substring(1);
        String mimetype = "text/html";
        if (filename.endsWith(".html") || filename.endsWith(".htm")) {
            mimetype = "text/html";
        } else if (filename.endsWith(".js")) {
            mimetype = "text/javascript";
        } else if (filename.endsWith(".css")) {
            mimetype = "text/css";
        } else if (filename.endsWith(".gif")) {
            mimetype = "text/gif";
        } else if (filename.endsWith(".jpeg") || filename.endsWith(".jpg")) {
            mimetype = "image/jpeg";
        } else if (filename.endsWith(".png")) {
            mimetype = "image/png";
        }  else if (filename.endsWith(".mp4")) {
            mimetype = "video/mp4";
        } else {
            mimetype = "text/html";
        }
        return mimetype;
    }

    public static boolean isImageMimeType(String fileName) {
        boolean isImage;
        if (fileName.endsWith(".gif")) {
            isImage = true;
        } else if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            isImage = true;
        } else if (fileName.endsWith(".png")) {
            isImage = true;
        } else {
            isImage = false;
        }
        return isImage;
    }

    public static boolean isVideoMimeType(String fileName) {
        return fileName.endsWith(".mp4");
    }

}
