package com.camerasurfaceview.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


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
    public static String strFolderDBName = "Practice";
    static Context context;
    private static App mInstance;


    public static String DB_NAME = "practice.db";
    public static String DB_PATH = "/sdcard/" + strFolderDBName + "/";



    public static String dateTimeFormateShort = "dd/MM/yyyy    HH:mm";//yyyyMMdd_HHmmssSSS
    public static String dateTimeStamp = "yyyyMMdd_HHmmss";
    public static String dateTimeFormateLong = "E MMM dd HH:mm:ss Z yyyy";


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        mInstance = this;



        create_Folder();  // only for testing purpose of local Database
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

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(3);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


    public static void showLog(String From, String msg) {
        System.out.println("From: " + From + " :---: " + msg);
    }

    public static void showLog(String msg) {
        System.out.println("From: :---: " + msg);
    }


    /*public static void showSnackBar(View view, String strMessage) {
        try {
            Snackbar snackbar = Snackbar.make(view, strMessage, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.BLACK);
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void hideSoftKeyboardMy(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /*------- For Calander ---------*/
    public static String getCurrentddMMMyyyyTime()
    {  //https://stackoverflow.com/questions/8654990/how-can-i-get-current-date-in-android

        String currentDate = "";

        try {
            Calendar c = Calendar.getInstance();
            App.showLog(TAG, "current Time: "+c.getTime());

            SimpleDateFormat df = new SimpleDateFormat(App.dateTimeFormateShort);
            currentDate = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }



        return currentDate;
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


    public static String getDateTimeLongFormate(String convert_date_string)
    {  //https://stackoverflow.com/questions/11097256/how-to-convert-mon-jun-18-000000-ist-2012-to-18-06-2012

        String final_date = "";
        String date1 = "";
        if (convert_date_string != null) {

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(App.dateTimeFormateShort);
                SimpleDateFormat outputFormat = new SimpleDateFormat(App.dateTimeFormateLong);
                String inputDateStr = convert_date_string;
                Date date = null;
                date = inputFormat.parse(inputDateStr);
                date1 = outputFormat.format(date);
                final_date = date1.toLowerCase();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return final_date;
    }


    public static String getddMMMyyyy(String convert_date_string)
    {  //https://stackoverflow.com/questions/11097256/how-to-convert-mon-jun-18-000000-ist-2012-to-18-06-2012

        String final_date = "";
        String date1 = "";
        if (convert_date_string != null) {

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(App.dateTimeFormateLong);
                SimpleDateFormat outputFormat = new SimpleDateFormat(App.dateTimeFormateShort);
                String inputDateStr = convert_date_string;
                Date date = null;
                date = inputFormat.parse(inputDateStr);
                date1 = outputFormat.format(date);
                final_date = date1.toLowerCase();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return final_date;
    }


}
