package com.github.androidserver.model;


import com.github.androidserver.utils.StringUtil;

public class MediaInfo extends BaseJsonModel{


    /**
     * unique id in MediaStore
     */
    public int id;
    /**
     * local path in android
     */
    public String localPath;
    /**
     * original url
     */
    public String url;
    /**
     * thumbnail url
     */
    public String thumbnailUrl;
    /**
     * image size
     */
    public long size;
    /**
     * image name
     */
    public String name;
    public int duration;

    public MediaInfo(int id, String localPath, String thumbnail, long size, String name) {
        this.id = id;
        this.localPath = localPath;
        this.url = StringUtil.convertPath2Url(localPath);
        this.thumbnailUrl = StringUtil.convertPath2Url(thumbnail);
        this.size = size;
        this.name = name;
    }

    public MediaInfo(int id, String localPath, String thumbnail, int duration, long size, String name) {
        this.id = id;
        this.localPath = localPath;
        this.url = StringUtil.convertPath2Url(localPath);
        this.thumbnailUrl = StringUtil.convertPath2Url(thumbnail);
        this.duration = duration;
        this.size = size;
        this.name = name;
    }

    @Override
    public String toString() {
        return "MediaInfo{" +
                "id=" + id +
                ", localPath='" + localPath + '\'' +
                ", url='" + url + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", size=" + size +
                ", name='" + name + '\'' +
                '}';
    }
}
