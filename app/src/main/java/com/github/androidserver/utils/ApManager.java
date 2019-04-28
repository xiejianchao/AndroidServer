package com.github.androidserver.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

import static android.content.Context.WIFI_SERVICE;

public class ApManager {

    private static final String TAG = "ApManager";

    /**
     * 判断Ap热点是否开启
     *
     * @param context 上下文
     * @return 开关
     */
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifimanager == null) {
            return false;
        }
        try {
            @SuppressLint("PrivateApi")
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 开启Ap热点
     *
     * @param ssid     SSID
     * @param password 密码
     * @param context  上下文
     * @return 是否成功
     */
    public static boolean openAp(Context context, String ssid, String password) {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifimanager == null) {
            return false;
        }
        if (wifimanager.isWifiEnabled()) {
            wifimanager.setWifiEnabled(false);
        }
        try {
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, null, false);
            method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean enable = (boolean) method.invoke(wifimanager, createConfiguration(ssid, password), true);
            return enable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭Ap热点
     *
     * @param context 上下文
     */
    public static void closeAp(Context context) {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifimanager == null) {
            return;
        }
        try {
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, null, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备曾开启过的Ap热点的名称和密码（无关设备现在是否有开启Ap）
     *
     * @param context 上下文
     * @return Ap热点名、Ap热点密码（密码可能为空）
     */
    public static String[] getApSSIDAndPwd(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e(TAG, "wifiManager == null");
            return null;
        }
        try {
            Method method = wifiManager.getClass().getMethod("getWificonfiguration");
            method.setAccessible(true);
            WifiConfiguration wifiConfiguration = (WifiConfiguration) method.invoke(wifiManager);
            String[] params = new String[2];
            params[0] = wifiConfiguration.SSID;
            params[1] = wifiConfiguration.preSharedKey;
            return params;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    /**
     * 配置Ap热点信息
     *
     * @param ssid     Ap热点SSID
     * @param password Ap热点密码
     * @return Ap热点信息
     */
    private static WifiConfiguration createConfiguration(String ssid, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = ssid;
        config.preSharedKey = password;
        config.status = WifiConfiguration.Status.ENABLED;
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return config;
    }

}
