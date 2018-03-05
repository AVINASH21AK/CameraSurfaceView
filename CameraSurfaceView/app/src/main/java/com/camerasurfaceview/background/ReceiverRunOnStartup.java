package com.camerasurfaceview.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.camerasurfaceview.common.App;


public class ReceiverRunOnStartup extends BroadcastReceiver {

    String TAG = "ReceiverRunOnStartup";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            App.showLog(TAG, "=======Phone start up=Start alarm--app-===");
            App.startAlarmServices(context);
        }
    }

}