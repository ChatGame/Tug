package me.chatgame.mobilecg.tug.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import me.chatgame.mobilecg.tug.util.interfaces.INetworkUtil;

/**
 * Created by star on 16/5/3.
 */
public class NetworkUtil implements INetworkUtil {
    ConnectivityManager manager;

    public NetworkUtil(Context context) {
        manager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
    }

    @Override
    public NetworkType getNetworkType() {
        NetworkInfo net = manager.getActiveNetworkInfo();
        int type = 0;
        int subType = 0;
        if (null == net) {
            return NetworkType.NETWORK_UNKNOWN;
        }
        if (net.getState() == NetworkInfo.State.CONNECTED) {
            type = net.getType();
            subType = net.getSubtype();
        }
        if (type == ConnectivityManager.TYPE_WIFI) {
            return NetworkType.NETWORK_WIFI;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return NetworkType.NETWORK_2G; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return NetworkType.NETWORK_2G; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return NetworkType.NETWORK_2G; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return NetworkType.NETWORK_3G; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return NetworkType.NETWORK_3G; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return NetworkType.NETWORK_2G; // ~ 100 kbps GSM
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return NetworkType.NETWORK_3G; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return NetworkType.NETWORK_3G; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return NetworkType.NETWORK_3G; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return NetworkType.NETWORK_3G; // ~ 400-7000 kbps
                case 13: // ~ TelephonyManager.NETWORK_TYPE_LTE
                    return NetworkType.NETWORK_4G; // ~ LTE - 4G
                case 14: // ~ TelephonyManager.NETWORK_TYPE_EHRPD
                    return NetworkType.NETWORK_3G; // ~ EHRPD - CDMA+
                case 15: // ~ TelephonyManager.NETWORK_TYPE_HSPAP
                    return NetworkType.NETWORK_3G; // ~ HSPA+
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return NetworkType.NETWORK_UNKNOWN;
                default:
                    return NetworkType.NETWORK_UNKNOWN;
            }
        } else {
            return NetworkType.NETWORK_UNKNOWN;
        }
    }
}
