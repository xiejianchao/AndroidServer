package com.github.androidserver.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

import timber.log.Timber;

import static android.content.Context.WIFI_SERVICE;

public class ApUtil {

    /**
     * 开启Ap热点
     *
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
     * 判断Ap热点是否开启
     *
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
     */
    public static String[] getApSSIDAndPwd(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager == null) {
            Timber.d("wifiManager == null");
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
            Timber.d(e.toString());
            return null;
        }
    }

    /**
     * 配置Ap热点信息
     *
     */
    private static WifiConfiguration createConfiguration(String ssid, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = ssid;
        config.hiddenSSID = false;
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
