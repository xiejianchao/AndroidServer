package com.github.androidserver.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.github.androidserver.model.MediaInfo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class MediaHelper {

    /**
     * 获取指定页数和数量的设备照片
     * 不能直接运行在主线程
     *
     * @param context
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public static List<MediaInfo> getImageList(Context context, int pageIndex, int pageSize) {
        List<MediaInfo> mediaList = new ArrayList<>();
        HashMap<String, List<MediaInfo>> photoFolder = new HashMap<>();
        Cursor mCursor = getImageCursor(context, pageIndex, pageSize);
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                MediaInfo mediaInfo = createImageModule(context, mCursor);
                mediaList.add(mediaInfo);
                saveFolderInfo(photoFolder, mediaInfo);
            }
            mCursor.close();
        }
        return mediaList;
    }

    /**
     * 获取指定页数和数量的设备视频
     * 第一次运行比较耗时，并且生成缩略图可能会阻塞，不能直接在主线程调用
     *
     * @param context
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public static List<MediaInfo> getVideoList(Context context, int pageIndex, int pageSize) {
        HashMap<String, List<MediaInfo>> videoFolder = new HashMap<>();
        List<MediaInfo> mediaList = new ArrayList<>();
        Cursor cursor = getVideoCursor(context, pageIndex, pageSize);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MediaInfo mediaInfo = createVideoModule(context, cursor);
                mediaList.add(mediaInfo);
                saveFolderInfo(videoFolder, mediaInfo);
            }
            cursor.close();
        }
        return mediaList;
    }

    public static int deleteVideo(Context context, List<String> videoIdList) {
        String whereIdArgs = joinIdArgs(videoIdList);
        int deleteRow = context.getContentResolver().
                delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Video.Media._ID + "=" + whereIdArgs,
                        null);
        Timber.d("预期删除" + videoIdList.size() + "条数据，实际删除" + deleteRow + "条");
        return deleteRow;
    }

    public static int deleteImage(Context context, List<String> imageIdList) {
        String whereIdArgs = joinIdArgs(imageIdList);
        int deleteRow = context.getContentResolver().
                delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.Media._ID + "=" + whereIdArgs,
                        null);
        Timber.d("预期删除" + imageIdList.size() + "条数据，实际删除" + deleteRow + "条");
        return deleteRow;
    }

    private static String joinIdArgs(List<String> idList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < idList.size(); i++) {
            if (i == 0) {
                sb.append(idList.get(i));
            } else {
                sb.append(" OR " + "_id = " + idList.get(i));
            }
        }
        Timber.d("where条件：" + sb);
        return sb.toString();
    }

    /**
     * 存储图片或者视频和文件夹的对应关系，防止以后可能会有类似需求
     *
     * @param allPhotosTemp
     * @param mediaInfo
     */
    private static void saveFolderInfo(HashMap<String, List<MediaInfo>> allPhotosTemp, MediaInfo mediaInfo) {
        String dirPath = new File(mediaInfo.localPath).getParentFile().getAbsolutePath();
        if (allPhotosTemp.containsKey(dirPath)) {
            List<MediaInfo> data = allPhotosTemp.get(dirPath);
            data.add(mediaInfo);
            return;
        } else {
            List<MediaInfo> data = new ArrayList<>();
            data.add(mediaInfo);
            allPhotosTemp.put(dirPath, data);
        }
    }

    private static String getVideoThumbnail(Context context, int videoId) {
        checkVideoThumbnail(context, videoId);
        String[] projection = {MediaStore.Video.Thumbnails._ID,
                MediaStore.Video.Thumbnails.DATA};
        Cursor cursor = getVideoThumbnailCursor(context, videoId, projection);
        String thumbPath = "";
        while (cursor != null && cursor.moveToNext()) {
            thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
        }
        if (cursor != null) {
            cursor.close();
        }
        return thumbPath;
    }

    private static String getImageThumbnail(Context context, int imageId) {
        String[] projection = {MediaStore.Images.Thumbnails._ID,
                MediaStore.Images.Thumbnails.DATA};
        Cursor cursor = getImageThumbnailCursor(context, imageId, projection);
        String thumbPath = "";
        while (cursor != null && cursor.moveToNext()) {
            thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
        }
        if (cursor != null) {
            cursor.close();
        }
        return thumbPath;
    }

    /**
     * 确保视频缩略图能够生成，此方法可能会阻塞和耗时，直至缩略图生成
     *
     * @param context
     * @param videoId
     */
    private static void checkVideoThumbnail(Context context, int videoId) {
        //缩略图相关问题：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
        MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                videoId,
                MediaStore.Video.Thumbnails.MICRO_KIND,
                null);
    }

    private static Cursor getVideoThumbnailCursor(Context context, int videoId, String[] projection) {
        return context.getContentResolver().
                query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
                        new String[]{videoId + ""},
                        null);
    }

    private static Cursor getImageThumbnailCursor(Context context, int imageId, String[] projection) {
        return context.getContentResolver().
                query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                        new String[]{imageId + ""},
                        null);
    }

    private static Cursor getVideoCursor(Context context, int pageIndex, int pageSize) {
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = getVideoProjection();
        return context.getContentResolver().query(videoUri,
                projection,
                MediaStore.Video.Media.MIME_TYPE + "=?",
                new String[]{"video/mp4"},
                MediaStore.Video.Media.DATE_MODIFIED + " desc limit " + pageSize + " offset " + pageIndex * pageSize);
    }

    private static Cursor getImageCursor(Context context, int pageIndex, int pageSize) {
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = getImageProjection();
        return context.getContentResolver().query(
                imageUri,
                projection,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " desc limit " + pageSize + " offset " + pageIndex * pageSize);
    }

    private static MediaInfo createVideoModule(Context context, Cursor cursor) {
        int videoId = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024; //单位kb
        if (size < 0) {
            //某些设备获取size < 0，直接计算
            Timber.d("this video size < 0 " + path);
            size = new File(path).length() / 1024;
        }
        String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
        long modifyTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));//暂未用到
        String thumbPath = getVideoThumbnail(context, videoId);
        return new MediaInfo(videoId, path, thumbPath, duration, size, displayName);
    }

    private static MediaInfo createImageModule(Context context, Cursor mCursor) {
        int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
        String thumbnail = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.MINI_THUMB_MAGIC));
        String imageThumbnail = getImageThumbnail(context, id);
        int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)) / 1024;
        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        return new MediaInfo(id, path, imageThumbnail, size, displayName);
    }

    @NotNull
    private static String[] getImageProjection() {
        return new String[]{MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME};
    }

    @NotNull
    private static String[] getVideoProjection() {
        return new String[]{MediaStore.Video.Thumbnails._ID,
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED};
    }

    /**
     * TODO
     * 查询指定目录的图片|视频
     *
     * @param context
     */
    public static void getImageList(Context context) {
        //查询条件
        String selection = MediaStore.Images.Media.DATA + " like '%Camera%'";
        //查询目录
        String path = "/storage/emulated/0/DCIM/";
        //selectionArgs：
        String[] selectionArgs = {path + "%"};
        String[] projection = getImageProjection();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                selection, selectionArgs, null);
        if (cursor != null) {
            do {
                cursor.getColumnIndexOrThrow("");
            } while (cursor.moveToNext());
        }
    }
}
