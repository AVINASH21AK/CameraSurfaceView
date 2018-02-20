package com.camerasurfaceview.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import com.camerasurfaceview.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;



/**
 * Created by Avinash Kahal on 02-Dec-17.
 */

public class App extends Application {

    public static String TAG = "APP";
    public static String strFolderDBName = "CameraSurface";
    public static String strFolderHidePic = "HiddenPics";

    public static String strDicFullMain = Environment.getExternalStorageDirectory() + File.separator + App.strFolderDBName;
    public static String strDicFullPath = strDicFullMain + File.separator + App.strFolderHidePic;

    static Context context;

    public static String DB_PATH = "/sdcard/" + strFolderDBName + "/";

    public static String dateTimeStamp = "yyyyMMdd_HHmmss";
    public static String dateTimeFormateLong = "E MMM dd HH:mm:ss Z yyyy";
    public static Bitmap bitmapFinal;
    public static String strCropFreely;



    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        create_Folder();
        create_FolderHidePics();
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


}
