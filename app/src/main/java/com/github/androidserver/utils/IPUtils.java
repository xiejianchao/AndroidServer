package com.github.androidserver.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class IPUtils {

    private static final String IP_PLACE_HOLDER = ",";

    /**
     * 如果连接到wifi，获取的是wifi路由分配给当前设备的ip地址
     * 如果开启了热点，则获取本机dhcp分配的地址
     *
     * @param context
     * @return
     */
    public static String getLocalIp(Context context) {
        String ip = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = intToIp(ipAddress);
        } else {
            String ipInfo = getNetInfo();
            if (!TextUtils.isEmpty(ipInfo) && ipInfo.split(IP_PLACE_HOLDER).length > 0) {
                String[] split = ipInfo.split(IP_PLACE_HOLDER);
                ip = split[0];
            }
        }
        return ip;
    }

    /**
     * 获取当前网络的广播地址
     * @param context
     * @return
     */
    public static String getBroadcastIp(Context context) {
        String ip = "";
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            String dhcpInfos = intToIp(dhcpInfo.netmask);
            String[] split = intToIp(ipAddress).split("\\.");
            ip = split[0] + "." + split[1] + "." + split[2] + "." + (255 - Integer.parseInt(dhcpInfos.split("\\.")[3]));//根据子网掩码获取广播的IP地址
        } else {
            String asd = getNetInfo();
            String[] split = asd.split(",");
            String ipStr = split[0];
            String NetMask = split[1];
            String[] split1 = ipStr.split("\\.");
            ip = split1[0] + "." + split1[1] + "." + split1[2] + "." + (255 - Integer.parseInt(NetMask.split("\\.")[3]));//根据子网掩码获取广播的IP地址
        }
        return ip;
    }


    private static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "."
                + (0xFF & paramInt >> 8) + "."
                + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }


    public static String getNetInfo() {
        String ipAddress = "";
        String maskAddress = "";

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                List<InterfaceAddress> mList = networkInterface.getInterfaceAddresses();
                for (InterfaceAddress l : mList) {
                    InetAddress inetAddress = l.getAddress();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (hostAddress.indexOf(":") > 0) {
                            continue;
                        } else {
                            ipAddress = hostAddress;
                            maskAddress = calcMaskByPrefixLength(l.getNetworkPrefixLength());
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAddress + IP_PLACE_HOLDER + maskAddress;
    }


    private static String calcMaskByPrefixLength(int length) {
        int mask = -1 << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }

}