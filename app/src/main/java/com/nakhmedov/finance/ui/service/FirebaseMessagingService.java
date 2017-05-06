package com.nakhmedov.finance.ui.service;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 5/6/17
 * Time: 11:39 AM
 * To change this template use File | Settings | File Templates
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getCanonicalName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.i(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.i(TAG, "FCM Data Message: " + remoteMessage.getData());

    }
}
