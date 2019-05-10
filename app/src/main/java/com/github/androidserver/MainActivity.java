package com.github.androidserver;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.androidserver.server.Server;
import com.github.androidserver.utils.ApManager;
import com.github.androidserver.utils.IPUtils;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    @BindView(R.id.tv_server_address)
    TextView mAddressTv;

    private Server mHttpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requireSomePermission();
        startHttpServer();
    }

    private void startHttpServer() {
        try {
            mHttpServer = new Server();
            if (!mHttpServer.isAlive()) {
                mHttpServer.start();
            }
            String localIp = IPUtils.getLocalIp(ServerApplication.getContext());
            mAddressTv.setText(localIp);
            Timber.d("服务器连接地址：http://" + localIp);
            Timber.d("Http server started");
        } catch (IOException e) {
            Timber.d("Http server could not start . e. cause:" + e.getCause());
        }
    }

    @OnClick(R.id.btn_get_ap_ip)
    public void getApIpClick() {
        String localIp = IPUtils.getLocalIp(ServerApplication.getContext());
        String info = "服务器连接地址：http://" + localIp + ":" + Constants.Code.PORT;
        Timber.d(info);
        mAddressTv.setText(info);
    }

    @OnClick(R.id.btn_open_ap)
    public void openApClick() {
        boolean enable = ApManager.openAp(this, "futurus-wifi", "12345678");
        Timber.d("获取热点成功");
        if (enable) {
            Toast.makeText(this, "获取热点成功", Toast.LENGTH_SHORT).show();
            String localIp = IPUtils.getLocalIp(ServerApplication.getContext());
            String info = "服务器连接地址：http://" + localIp + ":" + Constants.Code.PORT;
            Timber.d(info);
            mAddressTv.setText(info);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHttpServer != null) {
            if (mHttpServer.isAlive()) {
                mHttpServer.closeAllConnections();
                mHttpServer.stop();
            }
        }
    }

    @AfterPermissionGranted(1000)
    private void requireSomePermission() {
        String[] perms = {
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            //有权限
        } else {
            //没权限
            EasyPermissions.requestPermissions(this, "需要文件读取权限",
                    1000, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 权限申成功
     * @param i
     * @param list
     */
    @Override
    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        Timber.d("权限申成功");
    }

    /**
     * 权限申请失败
     * @param i
     * @param list
     */
    @Override
    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        Timber.d("权限申请失败");
    }
}
