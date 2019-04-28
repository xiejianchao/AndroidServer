package com.github.androidserver.service.image;

import com.github.androidserver.MediaHelper;
import com.github.androidserver.model.MediaInfo;
import com.github.androidserver.utils.MimeTypeUtil;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import timber.log.Timber;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

public class ImageService extends BaseImageMedia<MediaInfo> {

    @Inject
    Gson mGson;

    @Inject
    public ImageService() {}

    @Override
    public Response getListResponse(int pageIndex, int pageSize) {
        List<MediaInfo> imageList = MediaHelper.getImageList(mContext, pageIndex, pageSize);
        String json = toJson(mGson, imageList);
        return newFixedLengthResponse(Status.OK, MimeTypeUtil.MIME_JSON, json);
    }

    @Override
    public MediaInfo get(int id) {
        return null;
    }

    @Override
    public Response responseImage(String uri) {
        try {
            String mimeType = MimeTypeUtil.getMimeType(uri);
            FileInputStream fis = new FileInputStream(uri);
            int available = fis.available();

            Response response = newFixedLengthResponse(
                    Status.OK,
                    mimeType,//image can be see，mimeType must set "image/jpeg" || "image/jpg" || "image/png"
                    fis,
                    available);
            response.addHeader("Content-Length", String.valueOf(available));
            return response;
        } catch (IOException e) {
            Timber.e(e);
            return newFixedLengthResponse(Status.NOT_FOUND, "*/*", "Not Found");
        }
    }

}
