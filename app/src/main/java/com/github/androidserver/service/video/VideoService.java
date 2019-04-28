package com.github.androidserver.service.video;

import com.github.androidserver.MediaHelper;
import com.github.androidserver.model.MediaInfo;
import com.github.androidserver.model.VideoInfo;
import com.github.androidserver.service.BaseMedia;
import com.github.androidserver.utils.MimeTypeUtil;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import fi.iki.elonen.NanoHTTPD.Response;
import timber.log.Timber;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

public class VideoService extends BaseMedia<MediaInfo> implements VideoMedia {

    @Inject
    Gson mGson;

    @Inject
    public VideoService() {}

    @Override
    public Response getListResponse(int pageIndex, int pageSize) {
        List<MediaInfo> videoList = MediaHelper.getVideoList(mContext, pageIndex, pageSize);
        String json = toJson(mGson, videoList);
        return newFixedLengthResponse(Response.Status.OK, MimeTypeUtil.MIME_JSON, json);
    }

    @Override
    public VideoInfo get(int id) {
        return null;
    }

    @Override
    public Response responseVideo(String uri) {
        try {
            String mimeType = MimeTypeUtil.getMimeType(uri);
            FileInputStream fis = new FileInputStream(uri);
            int available = fis.available();

            Response response = newFixedLengthResponse(
                    Response.Status.OK,
                    mimeType,//image can be see，mimeType must set "image/jpeg" || "image/jpg" || "image/png"
                    fis,
                    available);
            response.addHeader("Content-Length", String.valueOf(available));
            return response;
        } catch (IOException e) {
            Timber.e(e);
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "*/*", "Not Found");
        }
    }


}
