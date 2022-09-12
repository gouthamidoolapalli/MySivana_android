package com.mysivana.firebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mysivana.util.AppConstants;
import com.mysivana.util.SharedPrefsUtils;

public class DeleteIntentReceiver extends BroadcastReceiver {
    private int notificationCount;
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationCount = SharedPrefsUtils.getNotificationCount(context);
        if (notificationCount > 0) {
            notificationCount--;
        }
        SharedPrefsUtils.setNotificationCount(context, notificationCount);

        //notify activity with count
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(AppConstants.INTENT_ACTION_NEW_NOTIFICATION);
        context.sendBroadcast(broadcastIntent);
    }
}

