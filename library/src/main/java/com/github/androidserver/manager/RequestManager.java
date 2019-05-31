package com.github.androidserver.manager;

import android.content.Context;

import com.github.androidserver.Constants;
import com.github.androidserver.utils.MediaHelper;
import com.github.androidserver.model.MediaInfo;
import com.github.androidserver.service.MediaServiceImpl;
import com.github.androidserver.utils.MimeTypeUtil;
import com.github.androidserver.utils.ParameterUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import timber.log.Timber;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

public class RequestManager {

    @Inject
    Context mContext;
    @Inject
    MediaServiceImpl mMediaService;

    @Inject
    public RequestManager() {}

    public Response handlerResponse(IHTTPSession session) {
        String uri = session.getUri();
        if (Constants.Key.REQUEST_ROOT.equals(uri)
                || session.getUri().equals("")
                || uri.contains("favicon.ico")) {
            return responseImageHtml(session);
        } else {
            File file = new File(uri);
            if (file.exists()) {
                //文件请求
                Timber.d("请求的文件：%s", file.getAbsolutePath());
                return getFileResponse(uri, file);
            } else {
                NanoHTTPD.Method method = session.getMethod();
                switch (method) {
                    case GET:
                        return getListResponse(session, uri);
                    case DELETE:
                        return deleteFile(session, uri);
                    default:
                        return responseNotFound();
                }
            }
        }
    }

    private Response deleteFile(IHTTPSession session, String uri) {
        List<String> ids = ParameterUtil.getIds(session);
        String[] idArr = new String[ids.size()];
        if (uri.startsWith(Constants.Api.IMAGE_DELETE)) {
            return mMediaService.createImageService().delete(ids.toArray(idArr));
        } else if (uri.startsWith(Constants.Api.VIDEO_DELETE)) {
            return mMediaService.createVideoService().delete(ids.toArray(idArr));
        }
        return responseNotFound();
    }

    private Response getListResponse(IHTTPSession session, String uri) {
        if (uri.startsWith(Constants.Api.IMAGE)) {
            return responseImage(session);
        } else if (uri.startsWith(Constants.Api.VIDEO)) {
            return responseVideo(session);
        } else {
            return responseNotFound();
        }
    }

    private Response getFileResponse(String uri, File file) {
        if (file.exists()) {
            if (MimeTypeUtil.isImageMimeType(file.getName())) {
                return mMediaService.createImageService().responseImage(uri);
            } else if (MimeTypeUtil.isVideoMimeType(file.getName())) {
                return mMediaService.createVideoService().responseVideo(uri);
            } else {
                return responseNotFound();
            }
        } else {
            return responseNotFound();
        }
    }

    private Response responseNotFound() {
        return newFixedLengthResponse(Response.Status.NOT_FOUND,
                NanoHTTPD.MIME_HTML,
                "Illegal request");
    }

    private Response responseVideo(IHTTPSession session) {
        int pageIndex = ParameterUtil.getPageIndex(session);
        int pageSize = ParameterUtil.getPageSize(session);
        return mMediaService.createVideoService().getListResponse(pageIndex, pageSize);
    }

    private Response responseImage(IHTTPSession session) {
        int pageIndex = ParameterUtil.getPageIndex(session);
        int pageSize = ParameterUtil.getPageSize(session);
        return mMediaService.createImageService().getListResponse(pageIndex, pageSize);
    }

    @NonNull
    private Response responseImageHtml(IHTTPSession session) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPER html><html><body>");
        builder.append("<h3>只显示手机最新的20张照片</h3>");
        builder.append("<ol>");
        List<MediaInfo> imageList = MediaHelper.getImageList(mContext, 0, 20);

        for (int i = 0, len = imageList.size(); i < len; i++) {
            File file = new File(imageList.get(i).localPath);
            if (file.exists()) {
                builder.append("<li> <a href=\"" + file.getPath() + "\">" + file.getName() +
                        "</a></li>");
            }
        }
        builder.append("<li>默认分享20张照片：  " + imageList.size() + "</li>");
        builder.append("</ol>");
        builder.append("</body></html>\n");
        return newFixedLengthResponse(String.valueOf(builder));
    }

    private Response responseDownloadImage(String uri) {
        FileInputStream fis = null;
        try {
            File file = new File(uri);
            fis = new FileInputStream(file);
            int available = fis.available();
            // 为了安全，判断下载的是否为合法允许的文件
            Response response = newFixedLengthResponse(Response.Status.OK, "application/octet-stream", fis, available);
            response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse("file not exists");
        }
    }

}
