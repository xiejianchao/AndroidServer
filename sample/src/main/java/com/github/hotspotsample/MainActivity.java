package com.github.hotspotsample;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;

import com.github.androidserver.AndroidHttpServer;
import com.github.androidserver.OnConnectListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.tv_server_address)
    TextView mAddressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requireSomePermission();
    }

    @OnClick(R.id.btn_open_server)
    public void startServerClick() {
        AndroidHttpServer.getInstance().startServer(new OnConnectListener() {
            @Override
            public void onConnecting() {
                mAddressTv.setText("正在开启服务器");
            }

            @Override
            public void onSuccess(String ip) {
                StringBuilder sb = new StringBuilder();
                sb.append(ip);
                sb.append("\n");
                sb.append("图片接口地址：" + ip + "/image?pageIndex=0&pageSize=20\n");
                sb.append("视频接口地址：" + ip + "/video?pageIndex=0&pageSize=20\n");
                sb.append("删除图片接口地址：" + ip + "/image/delete?id=111&id=222\n");
                sb.append("删除图片接口地址：" + ip + "/video/delete?id=111&id=222\n");
                mAddressTv.setText(sb.toString());
            }

            @Override
            public void onFailure(Exception e) {
                mAddressTv.setText("开启服务失败");
            }
        });
    }

    @AfterPermissionGranted(1000)
    private void requireSomePermission() {
        String[] perms = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
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
     *
     * @param i
     * @param list
     */
    @Override
    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        Timber.d("权限申成功");
    }

    /**
     * 权限申请失败
     *
     * @param i
     * @param list
     */
    @Override
    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        Timber.d("权限申请失败");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidHttpServer.getInstance().stop();
    }
}
