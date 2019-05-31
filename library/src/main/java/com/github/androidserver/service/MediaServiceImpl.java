package com.github.androidserver.service;


import com.github.androidserver.service.image.ImageService;
import com.github.androidserver.service.video.VideoService;

import javax.inject.Inject;

public class MediaServiceImpl implements IMediaService {

    @Inject
    ImageService mImageService;
    @Inject
    VideoService mVideoService;

    @Inject
    public MediaServiceImpl() {}


    @Override
    public ImageService createImageService() {
        return mImageService;
    }

    @Override
    public VideoService createVideoService() {
        return mVideoService;
    }


}
