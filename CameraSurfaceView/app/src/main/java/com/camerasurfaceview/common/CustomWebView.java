package com.camerasurfaceview.common;

/**
 * Created by prashant.patel on 9/12/2017.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.net.Uri;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.widget.Toast;


public class CustomWebView {
    private CustomTabsClient mCustomTabsClient;
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsServiceConnection mCustomTabsServiceConnection;
    private CustomTabsIntent mCustomTabsIntent;

    private Activity activity;
    private int color;

    public CustomWebView(Activity act, int c) {
        this.activity = act;
        this.color = c;
        init();
    }

    private void init() {
        try {
            mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
                @Override
                public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                    mCustomTabsClient = customTabsClient;
                    mCustomTabsClient.warmup(0L);
                    mCustomTabsSession = mCustomTabsClient.newSession(null);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mCustomTabsClient = null;
                }
            };
            CustomTabsClient.bindCustomTabsService(activity, activity.getPackageName(), mCustomTabsServiceConnection);
            mCustomTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession)
                    .setShowTitle(true)
                    .setToolbarColor(color)
                    .build();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void launchUrl(String Url){
        try{
            mCustomTabsIntent.launchUrl(activity, Uri.parse(Url));
        }catch (Exception e){
            Toast.makeText(activity.getApplication(), "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}

