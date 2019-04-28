package com.github.androidserver.manager;

import android.content.Context;

import com.github.androidserver.Constants;
import com.github.androidserver.MediaHelper;
import com.github.androidserver.model.MediaInfo;
import com.github.androidserver.service.MediaServiceImpl;
import com.github.androidserver.utils.MimeTypeUtil;

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
                Timber.d("请求的文件：%s" ,file.getAbsolutePath());
                if (MimeTypeUtil.isImageMimeType(file.getName())) {
                    return mMediaService.createImageService().responseImage(uri);
                } else if (MimeTypeUtil.isVideoMimeType(file.getName())){
                    return mMediaService.createVideoService().responseVideo(uri);
                } else {
                    return responseNotFound();
                }
            } else {
                if (uri.startsWith(Constants.Api.IMAGE)) {
                    return responseImage(session);
                } else if (uri.startsWith(Constants.Api.VIDEO)) {
                    return responseVideo(session);
                } else {
                    return responseNotFound();
                }
            }
        }
    }

    private Response responseNotFound() {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, "Illegal request");
    }

    private Response responseVideo(IHTTPSession session) {
        int pageIndex = getPageIndex(session);
        int pageSize = getPageSize(session);
        return mMediaService.createVideoService().getListResponse(pageIndex, pageSize);
    }

    private Response responseImage(IHTTPSession session) {
        int pageIndex = getPageIndex(session);
        int pageSize = getPageSize(session);
        return mMediaService.createImageService().getListResponse(pageIndex, pageSize);
    }

    public int getPageSize(IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> pageSizes = parameters.get(Constants.Key.PAGE_SIZE);
        int pageSize = 0;
        if (pageSizes != null && !pageSizes.isEmpty()) {
            pageSize = Integer.valueOf(pageSizes.get(0));
        }

        return pageSize;
    }

    public int getPageIndex(IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> pageIndexs = parameters.get(Constants.Key.PAGE_INDEX);
        int pageIndex = 0;
        if (pageIndexs != null && !pageIndexs.isEmpty()) {
            pageIndex = Integer.valueOf(pageIndexs.get(0));
        }

        return pageIndex;
    }

    @NonNull
    private Response responseImageHtml(IHTTPSession session) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPER html><html><body>");
        builder.append("<h3>只显示手机最新的20张照片</h3>");
        builder.append("<ol>");
        List<MediaInfo> imagelist = MediaHelper.getImageList(mContext, 0, 20);

        for (int i = 0, len = imagelist.size(); i < len; i++) {
            File file = new File(imagelist.get(i).localPath);
            if (file.exists()) {
                builder.append("<li> <a href=\"" + file.getPath() + "\">" + file.getName() +
                        "</a></li>");
            }
        }
        builder.append("<li>默认分享20张照片：  " + imagelist.size() + "</li>");
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
