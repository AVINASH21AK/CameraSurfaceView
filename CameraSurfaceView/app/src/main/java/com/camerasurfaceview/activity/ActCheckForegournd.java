package com.camerasurfaceview.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.camerasurfaceview.R;
import com.camerasurfaceview.common.App;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActCheckForegournd extends AppCompatActivity {

    String TAG = "ActCheckForegournd";

    @BindView(R.id.tvIsForeGround)
    TextView tvIsForeGround;

    String packageName = "com.wolfkeeper";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_check_foreground);

        App.setStatusBarGradiant(ActCheckForegournd.this);
        ButterKnife.bind(this);

        initialize();
        clickEvent();
    }


    public void initialize(){
        try{


            boolean isAppInstalled = appInstalledOrNot("com.check.application");


            if(isAppInstalled)
            {
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                startActivity(LaunchIntent);

                App.showLog(TAG, "Application is already installed.");

                //boolean foregroud = new ForegroundCheckTask().execute(ActCheckForegournd.this).get();

                if(isForeground(packageName))
                    tvIsForeGround.setText("Application is Running.");
                else
                    tvIsForeGround.setText("Application is not Running.");
            }
            else
            {
                App.showLog(TAG, "Application is not currently installed.");
                tvIsForeGround.setText("Application is not currently installed.");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clickEvent(){
        try{



        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /*
    * Check app is install
    * */
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }



    /*
    * Check app is ForeGround  -- https://stackoverflow.com/questions/8489993/check-android-application-is-in-foreground-or-not
    * */
    class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }


    /*
    * Check app is ForeGround   -- http://androidblog.reindustries.com/check-if-an-android-activity-is-currently-running/
    * */
    public boolean isForeground(String PackageName){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List< ActivityManager.RunningTaskInfo > task = manager.getRunningTasks(1);

        ComponentName componentInfo = task.get(0).topActivity;

        if(componentInfo.getPackageName().equals(PackageName)) return true;

        return false;
    }



}
