package com.camerasurfaceview.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camerasurfaceview.R;
import com.camerasurfaceview.api.model.GameModel;
import com.camerasurfaceview.common.App;
import com.camerasurfaceview.common.CountUpTimer;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActGame extends AppCompatActivity {

    String TAG = "ActGame";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @BindView(R.id.ivBaseClr)
    ImageView ivBaseClr;

    CountUpTimer timer;
    boolean winGame = false;
    boolean firstGame = true;


    int TotalTime = 5000;
    int TimeInterval = 1000;
    GameAdapter gameAdapter;

    String strBaseColor = "";
    ArrayList<GameModel> arrGame = new ArrayList<>();


    /*
    *  - https://libgdx.badlogicgames.com/showentry.html?id=97afb857-00b0-412c-9899-c2368a7d2177
    * */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_game);

        App.setStatusBarGradiant(ActGame.this);
        ButterKnife.bind(this);

        initialize();

    }


    public void initialize() {
        try {

            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(ActGame.this, 3);
            recyclerView.setLayoutManager(gridLayoutManager);

            recyclerView.setNestedScrollingEnabled(false);  //recycleview smooth scrool inside nestedScrollview
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());


            changeGame();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeGame() {
        strBaseColor = App.pickBaseColor(true);
        ivBaseClr.setBackgroundColor(Color.parseColor(strBaseColor));

        App.showLog(TAG, "strBaseColor: " + strBaseColor);
        boolean isValidGame = false;


        arrGame = new ArrayList<>();
        for (int i = 0; i < 9; i++) {

            String strColor = App.pickBaseColor(false);
            if(strBaseColor.equalsIgnoreCase(strColor))
            {
                isValidGame = true;
            }
            arrGame.add(new GameModel("", String.valueOf(i), "", strColor));

            if(i==8)
            {
                if(isValidGame == false)
                {
                    App.showLog(TAG, "isValidGame: " + isValidGame);
                    changeGame();
                }
                else
                {

                    if(winGame || firstGame)
                    {

                        App.showLog(TAG, "winGame: " + winGame);
                        App.showLog(TAG, "firstGame: " + firstGame);

                        firstGame = false;
                        winGame = false;

                        App.showLog(TAG, "----Game Start-----");

                        timer = null;
                        countUpTimer(TotalTime, TimeInterval);

                        if (gameAdapter != null)
                            gameAdapter.notifyDataSetChanged();

                        gameAdapter = new GameAdapter(ActGame.this, arrGame);
                        recyclerView.setAdapter(gameAdapter);
                    }
                    else
                    {
                        Toast.makeText(this, "You Loose Game...Try Again", Toast.LENGTH_LONG).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i1 = new Intent(ActGame.this, ActDashboard.class);
                                startActivity(i1);
                                finish();
                            }
                        },2500);
                    }

                }
            }
        }
    }


    public void countUpTimer(final int TOTAL_TIME, final int TICK) {
        try {

            if(timer == null) {
                timer = new CountUpTimer(TOTAL_TIME, TICK, new CountUpTimer.TimerDelegate() {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        App.showLog(TAG, "onTick: " + (millisUntilFinished / 1000));


                        int progressPlus = (int) (millisUntilFinished / 1000);
                        int progress = (100*progressPlus)/(TOTAL_TIME/ 1000);
                        App.showLog(TAG, "progress: " + progress);

                        progressbar.setProgress(progress);

                    }

                    @Override
                    public void onFinish() {
                        //do anything you want
                        App.showLog(TAG, "onFinish");
                        if(winGame) {
                            changeGame();
                        }else {
                            Toast.makeText(ActGame.this, "You Loose Game...Try Again", Toast.LENGTH_LONG).show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i1 = new Intent(ActGame.this, ActDashboard.class);
                                    startActivity(i1);
                                    finish();
                                }
                            },2500);
                        }
                    }
                });
                timer.start();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class GameAdapter extends RecyclerView.Adapter<GameAdapter.VersionViewHolder> {
        ArrayList<GameModel> arrGame;
        Context mContext;


        public GameAdapter(Context context, ArrayList<GameModel> arrGame) {
            this.arrGame = arrGame;
            mContext = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_game, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {


                versionViewHolder.ivImg.setBackgroundColor(Color.parseColor("" + arrGame.get(i).color));

                versionViewHolder.ivImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (arrGame.get(i).color.equalsIgnoreCase(strBaseColor)) {
                            arrGame.get(i).color = "#CCCCCC";
                            notifyDataSetChanged();
                        }

                        for(int j=0; j<arrGame.size(); j++)
                        {
                            if (!arrGame.get(j).color.equalsIgnoreCase(strBaseColor)) {
                                winGame = true;
                            }
                            else {
                                winGame = false;
                                break;    //-------to overcome -- break bcz of 1to8 are unSelected and 9 is selected than game is win
                            }
                        }


                        App.showLog(TAG, "arrGame.size(): " + arrGame.size());
                        App.showLog(TAG, "winGame: " + winGame);
                        App.showLog(TAG, "firstGame: " + firstGame);
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return arrGame.size();
        }


        class VersionViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImg;

            public VersionViewHolder(View itemView) {
                super(itemView);

                ivImg = (ImageView) itemView.findViewById(R.id.ivImg);
            }

        }
    }

}
