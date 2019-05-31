package com.github.androidserver.service.image;

import com.github.androidserver.service.Media;

import fi.iki.elonen.NanoHTTPD.Response;

public interface ImageMedia extends Media {

    Response responseImage(String uri);

}
