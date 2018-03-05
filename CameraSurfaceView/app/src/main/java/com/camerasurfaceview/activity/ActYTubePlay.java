package com.camerasurfaceview.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.camerasurfaceview.R;
import com.camerasurfaceview.api.model.YTubeIDModel;
import com.camerasurfaceview.common.App;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ActYTubePlay extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    /*
    Download JAR file from - https://developers.google.com/youtube/android/player/downloads/
    Example -https://www.numetriclabz.com/integrate-youtube-player-in-android-application-tutorial/
    */


    String TAG = "ActYouTube";

    //1. https://www.googleapis.com/youtube/v3/search?key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8=&part=snippet,id&order=date&maxResults=50
    //1. withSearchKeyWord:-- https://www.googleapis.com/youtube/v3/search?key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8=&q=love&part=snippet,id&order=date&maxResults=50
    //2. http://www.youtube.com/watch?v={video_id_here}

    @BindView(R.id.youtube_view)
    YouTubePlayerView youtube_view;

    YTubeIDModel modelYouTube;
    String VIDEO_ID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_youtube_play);

        App.setStatusBarGradiant(ActYTubePlay.this);
        ButterKnife.bind(this);

        getActivityIntent();
        initialize();

    }




    public void getActivityIntent() {
        try {

            if(getIntent().hasExtra("YTubeIDModel"))
            {
                modelYouTube = (YTubeIDModel)getIntent().getSerializableExtra("YTubeIDModel");

                VIDEO_ID = modelYouTube.id.videoId;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initialize() {
        try {

            youtube_view.initialize(App.strGoogleKey, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if(null== youTubePlayer) return;

        // Start buffering
        if (!wasRestored) {
            youTubePlayer.cueVideo(VIDEO_ID);
            youTubePlayer.setFullscreen(true);
            youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
