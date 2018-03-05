package com.camerasurfaceview.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.camerasurfaceview.R;
import com.camerasurfaceview.background.BroadcastReceiver;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;



/**
 * Created by Avinash Kahal on 02-Dec-17.
 */

public class App extends Application {

    public static String TAG = "APP";
    public DatabaseHelper dbHelper;
    public static String strFolderDBName = "CameraSurface";
    public static String strFolderHidePic = "HiddenPics";
    public static String strFolderYoutube = "Youtube-Download";

    public static String strDicFullMain = Environment.getExternalStorageDirectory() + File.separator + App.strFolderDBName;
    public static String strDicFullPath = strDicFullMain + File.separator + App.strFolderHidePic;
    public static String strDicYoutube = strDicFullMain + File.separator + App.strFolderYoutube;

    public static Context context;

    public static String DB_NAME = "youtube.db";
    public static String DATABASE_NAME = App.strDicFullMain + "/";

    //public static String DB_PATH = "/sdcard/" + strFolderDBName + "/";
    public static String DB_PATH = "/data/data/" + "com.camerasurfaceview" + "/databases/";


    public static String strWolfkeeper = "com.wolfkeeper";
    public static String strWhatsapp = "com.whatsapp";
    public static String packageTrack = strWolfkeeper;




    public static String dateTimeStamp = "yyyyMMdd_HHmmss";
    public static String dateTimeFormateLong = "E MMM dd HH:mm:ss Z yyyy";
    public static Bitmap bitmapFinal;
    public static String strCropFreely;
    public static String strGoogleKey = "AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8";
    public static int strTotalVideo = 10;


    //https://www.googleapis.com/youtube/v3/search?key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8=&part=snippet,id&order=date&maxResults=50
    public static String strBaseURL = "https://www.googleapis.com/youtube/v3/";



    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        create_Folder();
        create_FolderHidePics();
        create_FolderYoutube();

        /*PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(context, config);*/
        dbHelper = new DatabaseHelper(context);

        /*------ Facebook image loading ---------*/
        Fresco.initialize(this);
    }

    public static void create_Folder() {
        FileOutputStream out = null;
        try {
            String directoryPath = Environment.getExternalStorageDirectory() + File.separator + App.strFolderDBName;
            File appDir = new File(directoryPath);
            if (!appDir.exists() && !appDir.isDirectory()) {
                if (appDir.mkdirs()) {
                    App.showLog("===CreateDir===", "App dir created");
                } else {
                    App.showLog("===CreateDir===", "Unable to create app dir!");
                }
            } else {
                //App.showLog("===CreateDir===","App dir already exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void create_FolderHidePics() {
        FileOutputStream out = null;
        try {
            String directoryPath = Environment.getExternalStorageDirectory() + File.separator + App.strFolderDBName + File.separator + App.strFolderHidePic;
            File appDir = new File(directoryPath);
            if (!appDir.exists() && !appDir.isDirectory()) {
                if (appDir.mkdirs()) {
                    App.showLog("===CreateDir===", "App dir created");
                } else {
                    App.showLog("===CreateDir===", "Unable to create app dir!");
                }
            } else {
                //App.showLog("===CreateDir===","App dir already exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void create_FolderYoutube() {
        FileOutputStream out = null;
        try {
            String directoryPath = Environment.getExternalStorageDirectory() + File.separator + App.strFolderDBName + File.separator + App.strFolderYoutube;
            File appDir = new File(directoryPath);
            if (!appDir.exists() && !appDir.isDirectory()) {
                if (appDir.mkdirs()) {
                    App.showLog("===CreateDir===", "App dir created");
                } else {
                    App.showLog("===CreateDir===", "Unable to create app dir!");
                }
            } else {
                //App.showLog("===CreateDir===","App dir already exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_theme);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.whiteTR100));
            window.setNavigationBarColor(activity.getResources().getColor(R.color.whiteTR100));

            window.setBackgroundDrawable(background);
        }
    }

    public static void showLog(String From, String msg) {
        //Toast.makeText(context, From+" : "+msg, Toast.LENGTH_SHORT).show();
        System.out.println("From: " + From + " :---: " + msg);
    }

    public static void showToast(String msg) {
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();

    }


    /*
    * Seconds to Time
    * */
    public static String timeConversion(String totalSeconds) {

        int intTotalSeconds = Integer.parseInt(totalSeconds);
        int MINUTES_IN_AN_HOUR = 60;
        int SECONDS_IN_A_MINUTE = 60;

        int seconds = intTotalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = intTotalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        String totalTime = "";
        if(hours > 0)
        {
            totalTime = hours + ":" + minutes + ":" + seconds;
        }
        else
        {
            totalTime = minutes + ":" + seconds;
        }

        return totalTime;
    }


    /*
    * Get extenstion  -- https://stackoverflow.com/questions/9758151/get-the-file-extension-from-images-picked-from-gallery-or-camera-as-string
    * */
    public static String getExtention(String strUrl)
    {
        return strUrl.substring(strUrl.lastIndexOf("."));    // Extension with dot .jpg, .png
        //return strUrl.substring(strUrl.lastIndexOf(".") + 1);   // Without dot jpg, png
    }


    /*
    * Image Save with Time
    * */
    public static String getCurrentTimeStamp()
    {  //https://stackoverflow.com/questions/8654990/how-can-i-get-current-date-in-android

        String currentDate = "";

        try {
            Calendar c = Calendar.getInstance();
            App.showLog(TAG, "current Time: "+c.getTime());

            SimpleDateFormat df = new SimpleDateFormat(App.dateTimeStamp);
            currentDate = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentDate;
    }

    /*------ Check Internet -------*/
    public static boolean isInternetAvail(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    /*
    * For Game
    * */
    public static String pickBaseColor(boolean isBaseClr){

        String[] allColor = {
                "#FF0000",
                "#00FF00",
                "#0000FF",
                "#CCCCCC"
        };

        String[] baseColor = {
                "#FF0000",
                "#00FF00",
                "#0000FF",
        };


        String color = "#000000";

        if(isBaseClr == true)
        {
            int i = new Random().nextInt(baseColor.length);
            color = (baseColor[i]);
        }
        else
        {
            int i = new Random().nextInt(allColor.length);
            color = (allColor[i]);
        }


        return color;

    }



    /*
    * Background Service
    * */
    public static String strPrevTime = "";
    public static BroadcastReceiver alarm;
    public static void startAlarmServices(Context context)
    {
        if(alarm == null)
        {
            alarm = new BroadcastReceiver();
        }
        if(alarm != null)
        {
            alarm.CancelAlarm(context);
            alarm.SetAlarm(context);
        }
        else
        {
            App.showLog(TAG, "Alarm is null");
        }

    }

}
