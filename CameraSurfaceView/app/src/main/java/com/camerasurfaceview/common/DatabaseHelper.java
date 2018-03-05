package com.camerasurfaceview.common;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.camerasurfaceview.api.model.YoutubeSqliteModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;


public class DatabaseHelper extends SQLiteOpenHelper
{


    String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 2;
    static Context context;
    SQLiteDatabase db;



    public static final String TABLE_NAME_YOUTUBE = "downlaod";

    public void open() throws SQLException {
        db = this.getWritableDatabase();
    }

    @Override
    public synchronized void close() {

        if (db != null)
            db.close();
        super.close();
    }


    public DatabaseHelper(Context context) {
        super(context, App.DB_PATH + App.DB_NAME, null, DATABASE_VERSION);
        App.showLog(TAG,"===Table path and name=== " +App.DB_PATH + App.DB_NAME);
        this.context = context;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        db.execSQL("DROP TABLE IF EXISTS downlaod");

        App.showLog(TAG,"===Table Update===");
        // Create tables again
        onCreate(db);
    }

    public void DropTable(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + name + "'");
        db.close();
    }

    public void DropAllTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS 'downlaod'");

        onCreate(db);
        App.showLog(TAG,"===All Table Drop===");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        try {

            db.execSQL("CREATE TABLE IF NOT EXISTS downlaod(id INTEGER PRIMARY KEY AUTOINCREMENT, videoNames TEXT, status TEXT, type TEXT, videoURL TEXT, downloadID TEXT);");

            App.showLog(TAG,"===Table created===");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void InsertYoutube(YoutubeSqliteModel modelYouTube) {
        try {

                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();

                //values.put("id", modelYouTube.id);
                values.put("videoNames", modelYouTube.videoTitle);
                values.put("status", modelYouTube.downlaodStatus);
                values.put("type", modelYouTube.videoExt);
                values.put("videoURL", modelYouTube.videoURL);
                values.put("downloadID", modelYouTube.videoURL);

                db.insert("downlaod", null, values);
                db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<YoutubeSqliteModel> getAllYouTubeSqlite() {

        ArrayList<YoutubeSqliteModel> data = new ArrayList<>();
        try {

            String selectQuery = "SELECT  * FROM downlaod";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = null;

            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {


                        YoutubeSqliteModel modelYoutube = new YoutubeSqliteModel();
                        String id = cursor.getString(cursor
                                .getColumnIndex("id"));

                        String videoNames = cursor.getString(cursor
                                .getColumnIndex("videoNames"));

                        String status = cursor.getString(cursor
                                .getColumnIndex("status"));

                        String type = cursor.getString(cursor
                                .getColumnIndex("type"));

                        String videoURL = cursor.getString(cursor
                                .getColumnIndex("videoURL"));

                        String downloadID = cursor.getString(cursor
                                .getColumnIndex("downloadID"));


                        try {
                            modelYoutube.id = id;
                            modelYoutube.videoTitle = videoNames;
                            modelYoutube.downlaodStatus = status;
                            modelYoutube.videoExt = type;
                            modelYoutube.videoURL = videoURL;
                            modelYoutube.downloadID = downloadID;


                            data.add(modelYoutube);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            db.close(); // Closing database connection
        } catch (Exception e) {

        }
        return data;

    }


}
