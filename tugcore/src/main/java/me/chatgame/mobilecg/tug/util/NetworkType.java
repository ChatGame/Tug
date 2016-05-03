package me.chatgame.mobilecg.tug.util;

/**
 * Created by star on 16/5/3.
 */
public enum  NetworkType {
    NETWORK_UNKNOWN(0),
    NETWORK_WIFI(1),
    NETWORK_2G(2),
    NETWORK_3G(3),
    NETWORK_4G(4);

    private int id;

    public int id() {
        return this.id();
    }

    NetworkType(int id) {
        this.id = id;
    }

    public static NetworkType getInstance(int id) {
        switch(id) {
            case 0:
                return NETWORK_UNKNOWN;
            case 1:
                return NETWORK_WIFI;
            case 2:
                return NETWORK_2G;
            case 3:
                return NETWORK_3G;
            case 4:
                return NETWORK_4G;
            default:
                return NETWORK_UNKNOWN;
        }
    }
}
