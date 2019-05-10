package com.github.androidserver;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.github.androidserver.model.MediaInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class MediaHelper {

    public static List<MediaInfo> getImageList(Context context, int pageIndex, int pageSize) {
        List<MediaInfo> mediaList = new ArrayList<>();
        HashMap<String, List<MediaInfo>> allPhotosTemp = new HashMap<>();//所有照片
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID
                , MediaStore.Images.Media.DATA
                , MediaStore.Images.Media.MINI_THUMB_MAGIC
                , MediaStore.Images.Media.SIZE
                , MediaStore.Images.Media.DISPLAY_NAME};
        Cursor mCursor = context.getContentResolver().query(
                mImageUri,
                projection,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " desc limit " + pageSize + " offset " + pageIndex * pageSize);

        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                // 获取图片的路径
                int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String thumbnail = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.MINI_THUMB_MAGIC));
                int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)) / 1024;
                String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

                MediaInfo mediaInfo = new MediaInfo(id, path, thumbnail, size, displayName);
                mediaList.add(mediaInfo);
                // 获取该图片的父路径名
                String dirPath = new File(path).getParentFile().getAbsolutePath();
                //存储对应关系
                if (allPhotosTemp.containsKey(dirPath)) {
                    List<MediaInfo> data = allPhotosTemp.get(dirPath);
                    data.add(mediaInfo);
                    continue;
                } else {
                    List<MediaInfo> data = new ArrayList<>();
                    data.add(mediaInfo);
                    allPhotosTemp.put(dirPath, data);
                }
            }
            mCursor.close();
        }
        Timber.d("读取到的相册为：" + mediaList.toString());
        return mediaList;
    }

    public static List<MediaInfo> getVideoList(Context context, int pageIndex, int pageSize) {
        HashMap<String, List<MediaInfo>> allPhotosTemp = new HashMap<>();
        List<MediaInfo> mediaList = new ArrayList<>();
        Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection1 = {MediaStore.Video.Thumbnails._ID
                , MediaStore.Video.Thumbnails.DATA
                , MediaStore.Video.Media.DURATION
                , MediaStore.Video.Media.SIZE
                , MediaStore.Video.Media.DISPLAY_NAME
                , MediaStore.Video.Media.DATE_MODIFIED};
        Cursor mCursor = context.getContentResolver().query(mImageUri,
                projection1,
                MediaStore.Video.Media.MIME_TYPE + "=?",
                new String[]{"video/mp4"},
                MediaStore.Video.Media.DATE_MODIFIED + " desc limit " + pageSize + " offset " + pageIndex * pageSize);
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                // 获取视频的路径
                int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                int duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024; //单位kb
                if (size < 0) {
                    //某些设备获取size<0，直接计算
                    Timber.d("this video size < 0 " + path);
                    size = new File(path).length() / 1024;
                }
                String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                long modifyTime = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));//暂未用到

                //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
                MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                String[] projection2 = {MediaStore.Video.Thumbnails._ID,
                        MediaStore.Video.Thumbnails.DATA};

                Cursor cursor = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        projection2,
                        MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
                        new String[]{videoId + ""},
                        null);
                String thumbPath = "";
                while (cursor != null && cursor.moveToNext()) {
                    thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                }
                if (cursor != null) {
                    cursor.close();
                }

                MediaInfo mediaInfo = new MediaInfo(videoId, path, thumbPath, duration, size, displayName);
                mediaList.add(mediaInfo);
                // 获取该视频的父路径名
                String dirPath = new File(path).getParentFile().getAbsolutePath();
                //存储对应关系
                //保存视频和目录的对应关系，以备后续有相关需求
                if (allPhotosTemp.containsKey(dirPath)) {
                    List<MediaInfo> data = allPhotosTemp.get(dirPath);
                    data.add(mediaInfo);
                    continue;
                } else {
                    List<MediaInfo> data = new ArrayList<>();
                    data.add(mediaInfo);
                    allPhotosTemp.put(dirPath, data);
                }
            }
            mCursor.close();
        }

        return mediaList;
    }

    public static void getImageListOld(Context context) {
        //selection: 指定查询条件
        String selection = MediaStore.Images.Media.DATA + " like '%Camera%'";
        //设定查询目录
        String path = "/storage/emulated/0/DCIM/";
        //定义selectionArgs：
        String[] selectionArgs = {path + "%"};
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME
        };
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
