package com.github.androidserver.service.image;

import com.github.androidserver.Constants;
import com.github.androidserver.R;
import com.github.androidserver.model.MediaInfo;
import com.github.androidserver.model.ResponseData;
import com.github.androidserver.utils.MediaHelper;
import com.github.androidserver.utils.MimeTypeUtil;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
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
    public Response delete(String... ids) {
        List<String> idList = Arrays.asList(ids);
        Timber.d("要删除的图片id：" + idList.toString());
        int count = MediaHelper.deleteImage(mContext, idList);
        Timber.d("删除" + count + "张图片");
        ResponseData<String> data = new ResponseData<>();
        data.code = Constants.Code.SUCCESS;
        data.data = Constants.Key.SUCCESS;
        data.message = mContext.getString(R.string.delete_success);
        return newFixedLengthResponse(Status.OK, MimeTypeUtil.MIME_JSON, mGson.toJson(data));
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
