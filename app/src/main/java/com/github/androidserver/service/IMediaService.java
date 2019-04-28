package com.github.androidserver.service;


import com.github.androidserver.service.image.ImageService;
import com.github.androidserver.service.video.VideoService;

public interface IMediaService {

    ImageService createImageService();
    VideoService createVideoService();

}
