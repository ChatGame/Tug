package me.chatgame.mobilecg.tug.util.interfaces;

import me.chatgame.mobilecg.tug.util.NetworkType;

/**
 * Created by star on 16/5/3.
 */
public interface INetworkUtil {
    /**
     * 获取当前网络类型 <br>
     * 注意类型包括：2G,3G,4G,WIFI,UNKNOWN
     *
     * @return network type ({@linkplain NetworkType})
     */
    NetworkType getNetworkType();
}
