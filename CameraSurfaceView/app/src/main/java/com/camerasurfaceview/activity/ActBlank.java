package com.camerasurfaceview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.camerasurfaceview.R;
import com.camerasurfaceview.common.App;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActBlank extends AppCompatActivity {


    @BindView(R.id.btnCamera)
    Button btnCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_blank);

        App.setStatusBarGradiant(ActBlank.this);
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
