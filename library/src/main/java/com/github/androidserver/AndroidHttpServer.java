package com.github.androidserver;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.github.androidserver.exception.ApException;
import com.github.androidserver.utils.ApUtil;
import com.github.androidserver.utils.IPUtil;
import com.github.androidserver.utils.StringUtil;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import timber.log.Timber;

public class AndroidHttpServer {

    private static final int CONNECT_START = 0;
    private static final int CONNECT_SUCCESS = 1;
    private static final int CONNECT_FAILURE = 2;

    @Inject
    Server mHttpServer;

    private ExecutorService mExecutorService;
    private OnConnectListener mListener;

    private AndroidHttpServer() {
        ContextProvider.getServerComponent().inject(this);
        getThreadPool();
    }

    public static AndroidHttpServer getInstance() {
        return ServerHolder.sInstance;
    }

    private Handler mHandler = new Handler(ContextProvider.getAppContext().getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mListener == null) {
                return;
            }
            int what = msg.what;
            switch (what) {
                case CONNECT_START:
                    mListener.onConnecting();
                    break;
                case CONNECT_SUCCESS:
                    mListener.onSuccess((String) msg.obj);
                    break;
                case CONNECT_FAILURE:
                    mListener.onFailure((Exception) msg.obj);
                    break;
            }
        }
    };

    public void startServer(OnConnectListener listener) {
        this.mListener = listener;
        getThreadPool().execute(new ServerThread());
    }

    public void stop() {
        if (mHttpServer != null) {
            if (mHttpServer.isAlive()) {
                mHttpServer.closeAllConnections();
                mHttpServer.stop();
                getThreadPool().shutdown();
                Timber.d("关闭服务器");
            }
        }
        ApUtil.closeAp(ContextProvider.getAppContext());
        mListener = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    private ExecutorService getThreadPool() {
        if (mExecutorService == null || mExecutorService.isShutdown()) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }
        return mExecutorService;
    }

    private class ServerThread implements Runnable {

        @Override
        public void run() {
            try {
                mHandler.sendEmptyMessage(CONNECT_START);

                //开启server前需要先开启热点
                if (!openHotspot()) {
                    return;
                }

                if (!mHttpServer.isAlive()) {
                    mHttpServer.start();
                } else {
                    Timber.d("服务器已经开启，不需要再次开启。");
                }
                sendServerIp();
            } catch (IOException e) {
                e.printStackTrace();
                sendErrorMsg(e);
            }
        }
    }

    private boolean openHotspot() {
        if (!ApUtil.isApOn(ContextProvider.getAppContext())) {
            boolean openStatus = ApUtil.openAp(ContextProvider.getAppContext(),
                    Constants.Key.WIFI_HOTSPOT_NAME,
                    Constants.Key.WIFI_HOTSPOT_PWD);
            Timber.d("热点开启状态：" + openStatus);
            if (!openStatus) {
                sendErrorMsg(new ApException(ContextProvider.getAppContext().
                        getString(R.string.hotspot_open_fail)));
            }
            return openStatus;
        } else {
            return true;
        }
    }

    private void sendServerIp() {
        String localIp = IPUtil.getLocalIp(ContextProvider.getAppContext());
        if (TextUtils.isEmpty(localIp)) {
            Timber.d("获取服务器ip地址失败，返回默认ip地址：192.168.43.1");
            localIp = Constants.Key.DEFAULT_HOTSPOT_IP;
        }
        String serverIp = StringUtil.getFullServerIp(localIp);
        sendSuccessMsg(serverIp);
        Timber.d("Http server started, http://" + localIp);
    }

    private void sendErrorMsg(Throwable throwable) {
        Message message = mHandler.obtainMessage();
        message.what = CONNECT_FAILURE;
        message.obj = throwable;
        mHandler.sendMessage(message);
    }

    private void sendSuccessMsg(String ip) {
        Message message = mHandler.obtainMessage();
        message.what = CONNECT_SUCCESS;
        message.obj = ip;
        mHandler.sendMessage(message);
    }

    private static class ServerHolder {
        private static AndroidHttpServer sInstance = new AndroidHttpServer();
    }
}

