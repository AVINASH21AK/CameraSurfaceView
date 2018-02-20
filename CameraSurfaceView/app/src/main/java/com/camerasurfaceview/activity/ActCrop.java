package com.camerasurfaceview.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camerasurfaceview.R;
import com.camerasurfaceview.common.App;
import com.camerasurfaceview.common.FreeHandCropView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActCrop extends AppCompatActivity {

    @BindView(R.id.rlImgView)
    RelativeLayout rlImgView;

    @BindView(R.id.cropImg)
    ImageView cropImg;

    @BindView(R.id.cropImageView)
    CropImageView cropImageView;

    @BindView(R.id.btnSave)
    Button btnSave;

    @BindView(R.id.btnUndo)
    Button btnUndo;

    boolean strCropFreely = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_crop);

        App.setStatusBarGradiant(ActCrop.this);
        ButterKnife.bind(this);


        //getIntentActivity();
        setData();
        clickEvent();
    }

    public void getIntentActivity(){
        try{

            if(getIntent().hasExtra(App.strCropFreely))
            {
                if(getIntent().getStringExtra(App.strCropFreely).equalsIgnoreCase("No"))
                    strCropFreely = false;
                else if(getIntent().getStringExtra(App.strCropFreely).equalsIgnoreCase("Yes"))
                    strCropFreely = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setData(){
        try{

            if(strCropFreely) {
                rlImgView.setVisibility(View.VISIBLE);
                cropImg.setVisibility(View.GONE);
                cropImageView.setVisibility(View.GONE);

                cropImg.setImageBitmap(App.bitmapFinal);

                 /*
                * Free Crop Function
                * */
                addFreeHandCropView();
            }
            else
            {
                rlImgView.setVisibility(View.GONE);
                cropImg.setVisibility(View.GONE);
                cropImageView.setVisibility(View.VISIBLE);

                cropImageView.setImageBitmap(App.bitmapFinal);
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clickEvent(){
        try{

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String strFolderPath = App.DB_PATH + "Img_" + App.getCurrentTimeStamp() + ".png";
                    Bitmap bitmapCropped = null;

                    if(strCropFreely)
                    {
                        if(FreeHandCropView.getBitmapFromMemCache() != null)
                        {
                            bitmapCropped = FreeHandCropView.getBitmapFromMemCache();
                        }
                        else
                        {
                            bitmapCropped = App.bitmapFinal;
                        }
                    }
                    else
                    {
                        cropImageView.getAspectRatio();
                        cropImageView.getScaleType();

                        bitmapCropped = cropImageView.getCroppedImage();
                    }



                    saveBitmap(bitmapCropped, strFolderPath);

                }
            });



            btnUndo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    view.setVisibility(View.GONE);

                    setData();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void addFreeHandCropView() {

        FreeHandCropView.ImageCropListener listener = new FreeHandCropView.ImageCropListener() {
            @Override
            public void onClickDialogPositiveButton() {

                btnUndo.setVisibility(View.VISIBLE);

                rlImgView.setVisibility(View.GONE);
                cropImg.setVisibility(View.VISIBLE);

                Bitmap croppedImage = FreeHandCropView.getBitmapFromMemCache();
                cropImg.setImageBitmap(croppedImage);


            }

            @Override
            public void onClickDialogNegativeButton() {
                // ignore
            }
        };

        rlImgView.addView(new FreeHandCropView(this, App.bitmapFinal, listener),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }


    public void saveBitmap(Bitmap bitmapCropped, String strFolderPath){

        try{

            FileOutputStream finalOutStream = new FileOutputStream(strFolderPath);
            bitmapCropped.compress(Bitmap.CompressFormat.PNG, 100, finalOutStream);
            finalOutStream.close();

            Toast.makeText(this, "Image Saved...", Toast.LENGTH_SHORT).show();

            File imageFile = new File(strFolderPath);
            openSaveImg(imageFile);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openSaveImg(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }


}
