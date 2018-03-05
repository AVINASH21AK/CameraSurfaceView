package com.camerasurfaceview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.camerasurfaceview.R;
import com.camerasurfaceview.common.App;

import butterknife.ButterKnife;


public class ActExpandTextview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_expandtextview);

        App.setStatusBarGradiant(ActExpandTextview.this);
        ButterKnife.bind(this);

        initialize();
        clickEvent();
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
