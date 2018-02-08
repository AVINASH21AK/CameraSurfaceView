package com.camerasurfaceview.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.camerasurfaceview.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActDashboard extends AppCompatActivity {


    @BindView(R.id.btnCamera)
    Button btnCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dashboard);

        /*
        *ButterKnife
        * */
        ButterKnife.bind(this);

        clickEvent();
    }


    public void clickEvent(){
        try{


            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(ActDashboard.this, ActCameraSurfaceView.class);
                    startActivity(i1);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
