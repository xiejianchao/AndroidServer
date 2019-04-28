package com.github.androidserver.utils;

public class MimeTypeUtil {

    public static final String MIME_JSON = "application/json";

    public static String getMimeType(String uri) {
        String filename = uri.substring(1);
        String mimetype = "text/html";
        if (filename.contains(".html") || filename.contains(".htm")) {
            mimetype = "text/html";
        } else if (filename.contains(".js")) {
            mimetype = "text/javascript";
        } else if (filename.contains(".css")) {
            mimetype = "text/css";
        } else if (filename.contains(".gif")) {
            mimetype = "text/gif";
        } else if (filename.contains(".jpeg") || filename.contains(".jpg")) {
            mimetype = "image/jpeg";
        } else if (filename.contains(".png")) {
            mimetype = "image/png";
        }  else if (filename.contains(".mp4")) {
            mimetype = "video/mp4";
        } else {
            mimetype = "text/html";
        }
        return mimetype;
    }

    public static boolean isImageMimeType(String fileName) {
        boolean isImage;
        String mimetype;
        if (fileName.contains(".gif")) {
            mimetype = "image/gif";
            isImage = true;
        } else if (fileName.contains(".jpeg") || fileName.contains(".jpg")) {
            mimetype = "image/jpeg";
            isImage = true;
        } else if (fileName.contains(".png")) {
            mimetype = "image/png";
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
