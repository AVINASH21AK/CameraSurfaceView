package com.camerasurfaceview.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.camerasurfaceview.R;
import com.camerasurfaceview.common.App;
import com.camerasurfaceview.common.CustomWebView;
import com.camerasurfaceview.common.DatabaseHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActDashboard extends AppCompatActivity {  //implements HardButtonReceiver.HardButtonListener

    String TAG = "ActDashboard";
    DatabaseHelper dbHelper;

    @BindView(R.id.btnPMS) Button btnPMS;
    @BindView(R.id.btnCamera) Button btnCamera;
    @BindView(R.id.btnGame) Button btnGame;
    @BindView(R.id.btnHidePics) Button btnHidePics;
    @BindView(R.id.btnCheckForeground) Button btnCheckForeground;
    @BindView(R.id.btnTransition) Button btnTransition;
    @BindView(R.id.btnYoutube) Button btnYoutube;

    CustomWebView cts;
    //private HardButtonReceiver mButtonReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dashboard);

        App.setStatusBarGradiant(ActDashboard.this);

        /*
        *ButterKnife
        * */
        ButterKnife.bind(this);

        /*
        *  - https://github.com/gauntface/Android-Headset-Example
        *  - https://stackoverflow.com/questions/10125083/android-checking-headset-button-click
        *  - https://stackoverflow.com/questions/39410268/how-to-listen-to-action-down-key-pressed-event-in-android-onmediabuttonevent/39413753#39413753
        * */


        App.startAlarmServices(ActDashboard.this);

        initialize();
        clickEvent();

        /*Intent i1 = new Intent(ActDashboard.this, ActYouTube.class);
        startActivity(i1);*/
    }


    public void initialize(){
        try{
            dbHelper = new DatabaseHelper(this);

          /*  mButtonReceiver = new HardButtonReceiver(this);
            IntentFilter i1 = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
            i1.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY + 1);

            // register the receiver
            registerReceiver(mButtonReceiver, i1);
            App.showLog(TAG, "HeadsetExample: The Button Receiver has been registered");*/


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /*public void onStop() {
       try{


        super.onStop();

        // In the onStop method you should unregister the receiver
        unregisterReceiver(mButtonReceiver);
        App.showLog(TAG, "HeadsetExample: The Button Receiver has been unregistered");
       }catch (Exception e){
           e.printStackTrace();
       }
    }


    *//**
     * HardButtonListener methods
     *//*
    @Override
    public void onPrevButtonPress() {
        App.showLog(TAG, "HeadsetExample: Previous button pressed");
    }

    @Override
    public void onNextButtonPress() {
        App.showLog(TAG, "HeadsetExample: Next button pressed");
    }

    @Override
    public void onPlayPauseButtonPress() {
        App.showLog(TAG, "HeadsetExample: Play / Pause button pressed");
    }*/


    public void clickEvent(){
        try{


            btnPMS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cts = new CustomWebView(ActDashboard.this, 0xff007392);
                    cts.launchUrl("http://eliteinfoworld.net/pms/login.php");
                }
            });

            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(ActDashboard.this, ActCameraSurfaceView.class);
                    startActivity(i1);
                }
            });

            btnCamera.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    btnGame.setVisibility(View.VISIBLE);

                    return false;
                }
            });

            btnGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(ActDashboard.this, ActGame.class);
                    startActivity(i1);
                }
            });

            btnHidePics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(ActDashboard.this, ActHidePic.class);
                    startActivity(i1);
                }
            });

            btnCheckForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(ActDashboard.this, ActCheckForegournd.class);
                    startActivity(i1);
                }
            });

            btnTransition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(ActDashboard.this, ActExpandTextview.class);
                    startActivity(i1);
                }
            });

            btnYoutube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbHelper.DropAllTable();

                    Intent i1 = new Intent(ActDashboard.this, ActYouTube.class);
                    startActivity(i1);
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
