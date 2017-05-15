package com.nakhmedov.finance.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nakhmedov.finance.ui.activity.SearchActivity;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 5/15/17
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates
 */

public class NetworkUtil {
    public static boolean isNetActive(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
