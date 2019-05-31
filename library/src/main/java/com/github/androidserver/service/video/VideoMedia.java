package com.github.androidserver.service.video;

import com.github.androidserver.service.Media;

import fi.iki.elonen.NanoHTTPD.Response;

public interface VideoMedia<T> extends Media {

    Response responseVideo(String uri);

}
