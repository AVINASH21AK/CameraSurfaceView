package com.camerasurfaceview.activity;

import android.app.Activity;
import android.os.Bundle;

import com.camerasurfaceview.R;
import com.camerasurfaceview.common.App;

import butterknife.ButterKnife;

public class ActDownloadProgress extends Activity
{

    String TAG = "ActDownloadProgress";
    String mVideoUrl, mVideoTitle, mVideoFullPath;


    public static final String INTENT_DOWNLOAD = "intent.DOWNLOAD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_download_progress);

        App.setStatusBarGradiant(ActDownloadProgress.this);
        ButterKnife.bind(this);



        getActivityIntent();
        initialize();
        clickEvent();



    }





    public void getActivityIntent(){
        try{

            if(getIntent().hasExtra("from"))
            {
                mVideoUrl = getIntent().getStringExtra("VideoUrl");
                mVideoTitle = getIntent().getStringExtra("VideoTitle");
                mVideoFullPath = getIntent().getStringExtra("VideoFullPath");

                App.showLog(TAG, "mVideoUrl: "+mVideoUrl);
                App.showLog(TAG, "mVideoTitle: "+mVideoTitle);
                App.showLog(TAG, "mVideoFullPath: "+mVideoFullPath);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initialize(){
        try{


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





}
