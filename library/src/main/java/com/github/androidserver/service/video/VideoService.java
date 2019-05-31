package com.github.androidserver.service.video;

import android.content.Context;

import com.github.androidserver.Constants;
import com.github.androidserver.R;
import com.github.androidserver.model.MediaInfo;
import com.github.androidserver.model.ResponseData;
import com.github.androidserver.service.BaseMedia;
import com.github.androidserver.utils.MediaHelper;
import com.github.androidserver.utils.MimeTypeUtil;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import fi.iki.elonen.NanoHTTPD.Response;
import timber.log.Timber;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

public class VideoService extends BaseMedia<MediaInfo> implements VideoMedia {

    @Inject
    Gson mGson;
    @Inject
    Context mContext;

    @Inject
    public VideoService() {}

    @Override
    public Response getListResponse(int pageIndex, int pageSize) {
        List<MediaInfo> videoList = MediaHelper.getVideoList(mContext, pageIndex, pageSize);
        String json = toJson(mGson, videoList);
        return newFixedLengthResponse(Response.Status.OK, MimeTypeUtil.MIME_JSON, json);
    }

    @Override
    public MediaInfo get(int id) {
        //TODO
        return null;
    }

    @Override
    public Response delete(String... ids) {
        List<String> idList = Arrays.asList(ids);
        Timber.d("要删除的id：" + idList.toString());
        int count = MediaHelper.deleteVideo(mContext, idList);
        Timber.d("删除" + count + "条视频");
        ResponseData<String> data = new ResponseData<>();
        data.code = Constants.Code.SUCCESS;
        data.data = Constants.Key.SUCCESS;
        data.message = mContext.getString(R.string.delete_success);
        return newFixedLengthResponse(Response.Status.OK, MimeTypeUtil.MIME_JSON, mGson.toJson(data));
    }

    @Override
    public Response responseVideo(String uri) {
        try {
            String mimeType = MimeTypeUtil.getMimeType(uri);
            FileInputStream fis = new FileInputStream(uri);
            int available = fis.available();

            Response response = newFixedLengthResponse(
                    Response.Status.OK,
                    mimeType,//mimeType must set "video/mp4"
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
