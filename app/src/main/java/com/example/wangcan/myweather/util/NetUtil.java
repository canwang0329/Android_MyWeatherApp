package com.example.wangcan.myweather.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

/**
 * Created by lenovo on 2015/3/22.
 */
public class NetUtil {

    public static final int NETWORN_NONE=0;
    public static final int NETWORN_WIFI=1;
    public static final int NETWORN_MOBILE=2;

    public static int getNetworkState(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);//ConnectivityManager管理网络连接的相关的类

        //check wifi first
        State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        //if the wifi is connected return NETWORN_WIFI
        if (state == State.CONNECTED || state == State.CONNECTING) {
            return NETWORN_WIFI;
        }
        //If the wifi is not connected,check the mobile
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (state == State.CONNECTED || state == State.CONNECTING) {
            return NETWORN_MOBILE;
        }

        return NETWORN_NONE;
    }
}
