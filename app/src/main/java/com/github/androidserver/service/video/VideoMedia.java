package com.github.androidserver.service.video;

import com.github.androidserver.service.Media;

import fi.iki.elonen.NanoHTTPD;

public interface VideoMedia<T> extends Media {

    NanoHTTPD.Response responseVideo(String uri);

}
