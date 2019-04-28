package com.github.androidserver.model;

public class VideoInfo extends BaseJsonModel {

    public int id;

    public int width;

    public int height;

    public String thumbnail;

    public String url;
    /**
     * video path in android
     */
    public String localPath;

    public int size;

    public VideoInfo(int id, int width, int height, String thumbnail, String url, String localPath, int size) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.thumbnail = thumbnail;
        this.url = url;
        this.localPath = localPath;
        this.size = size;
    }
}
