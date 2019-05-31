package com.github.androidserver.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

public class WifiUtil {

    private void search(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            //开启wifi
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
    }
}