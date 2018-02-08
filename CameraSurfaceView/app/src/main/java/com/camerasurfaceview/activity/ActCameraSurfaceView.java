package com.camerasurfaceview.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.camerasurfaceview.R;
import com.camerasurfaceview.api.model.SurfaceViewModel;
import com.camerasurfaceview.common.App;
import com.camerasurfaceview.common.CustomProgressDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ActCameraSurfaceView extends Activity implements SurfaceHolder.Callback {

    FrameLayout framelayTop;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    ImageView ivCaptuer, ivCameraSwitch, ivProgressBar;

    boolean isBackCameraOn = false;
    PictureCallback jpegCallback;

    ViewPager viewPager;
    ViewPagerAdapterAction adapter;
    ArrayList<SurfaceViewModel> arrActionWeek = new ArrayList<SurfaceViewModel>();
    CustomProgressDialog customProgressDialog;

    String strFolderPath = "";
    int drawablePosition = 0;

    /* -------- All References -------------
    https://stackoverflow.com/questions/37827937/camera-live-preview-freezes-on-camera-switch
    * surfaceview - http://android-er.blogspot.in/2010/12/camera-preview-on-surfaceview.html
    * overlay - https://stackoverflow.com/questions/21877525/save-overlay-image-camerapreview
    * overlay2 - https://stackoverflow.com/questions/25721628/android-drawing-multiple-different-size-bitmaps-to-same-size-canvas
    * viewToBitmap - https://stackoverflow.com/questions/10674570/how-to-convert-linearlayout-to-image
    *
    * */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_camera);

        initialize();
        clickEvent();
        setViewPager();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showCameraInSurfaceView();
            }
        },300);


    }


    public void initialize() {
        try {

            customProgressDialog = new CustomProgressDialog(this);
            ivCaptuer = (ImageView) findViewById(R.id.ivCaptuer);
            ivCameraSwitch = (ImageView) findViewById(R.id.ivCameraSwitch);
            ivProgressBar = (ImageView) findViewById(R.id.ivProgressBar);

            framelayTop = (FrameLayout) findViewById(R.id.framelayTop);
            viewPager = (ViewPager) findViewById(R.id.viewpager);

            surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
            surfaceView.setZOrderOnTop(false);

            getWindow().setFormat(PixelFormat.UNKNOWN);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void clickEvent() {
        try {

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrollStateChanged(int state) {}
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                public void onPageSelected(int position) {
                    drawablePosition = position;
                }
            });


            ivCameraSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isBackCameraOn) {
                        isBackCameraOn = false;
                        showCameraInSurfaceView();
                    } else {
                        isBackCameraOn = true;
                        showCameraInSurfaceView();
                    }
                }
            });


            Animation a = AnimationUtils.loadAnimation(ActCameraSurfaceView.this, R.anim.progress_anim);
            a.setDuration(1000);
            ivProgressBar.startAnimation(a);
            ivCaptuer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        customProgressDialog.show();
                        strFolderPath = App.DB_PATH + "Img_" + App.getCurrentTimeStamp() + ".png";
                        captureImage2();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            jpegCallback = new PictureCallback()
            {
                public void onPictureTaken(byte[] data, Camera camera)
                {
                    try {

                        //https://github.com/josnidhin/Android-Camera-Example/blob/master/src/com/example/cam/CamTestActivity.java

                        int width;
                        int height;
                        Matrix matrix = new Matrix();

                        Bitmap drawableSelectedIcon = null;// = BitmapFactory.decodeResource(getResources(), R.drawable.surface_cat);

                        if(drawablePosition == 0)
                            drawableSelectedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.surface_cat);
                        else if(drawablePosition == 1)
                            drawableSelectedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.surface_hair);
                        else if(drawablePosition == 2)
                            drawableSelectedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.surface_mask);



                        Bitmap cameraPic = BitmapFactory.decodeByteArray(data, 0, data.length);
                        width = cameraPic.getWidth();
                        height = cameraPic.getHeight();

                        if(!isBackCameraOn)
                        {
                            System.out.println("isBackCameraOn: "+isBackCameraOn);

                            /*float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
                            Matrix matrixMirrorY = new Matrix();
                            matrixMirrorY.setValues(mirrorY);
                            matrix.preScale(1.0f, -1.0f);
                            matrix.postConcat(matrixMirrorY);*/




                            matrix.setRotate(270);
                        }
                        else
                        {
                            System.out.println("isBackCameraOn: "+isBackCameraOn);
                            matrix.setRotate(90);
                        }


                        /*
                        * Marging camera & viewpager img with same height and width
                        * */
                            Bitmap bitmapCameraPicFinal = Bitmap.createBitmap(cameraPic, 0, 0, width, height, matrix, true);

                        Bitmap bitmap = Bitmap.createBitmap(bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(bitmap);

                        Drawable drawable1 = new BitmapDrawable(bitmapCameraPicFinal);
                        Drawable drawable2 = new BitmapDrawable(drawableSelectedIcon);

                        drawable1.setBounds(0, 0, bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight());
                        drawable2.setBounds(0, 0, bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight());

                        drawable1.draw(c);
                        drawable2.draw(c);




                        /*
                        * saving img after margin to single image
                        * */
                        FileOutputStream outStream = new FileOutputStream(strFolderPath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.close();



                        /*
                        * Refresh camera view
                        * */
                        System.out.println("camera re-start and opening photo");
                        customProgressDialog.dismiss();
                        showCameraInSurfaceView();


                        /*
                        * Open capture image
                        * */
                        File imageFile = new File(strFolderPath);
                        openSaveImg(imageFile);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    public void SaveBitmap(){
         /*
                    Bitmap bitmap = viewToBitmap(framelayTop);

                    try {
                        //FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() +"/" + App.strFolderDBName + "/" + "filename.png"
                        FileOutputStream output = new FileOutputStream(App.DB_PATH + "Img_"+App.getCurrentTimeStamp()+".png");
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                        output.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
    }


    public void captureImage2() throws IOException {
        try {
            camera.takePicture(null, null, jpegCallback);
        } catch (Exception e) {
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


    /*
    * show front or back camera
    * */
    public void showCameraInSurfaceView() {
        try {

            releaseCameraAndPreview();
            if (isBackCameraOn) {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            } else {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }

            // camera = Camera.open();
            if (camera != null) {
                try {
                    camera.setDisplayOrientation(90);  //default camera show 90 rotated so change it
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * initialize camera
    * */
    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }


    /*
    * setup ViewPager images
    * */
    public void setViewPager() {
        try {

            int img[] = {R.drawable.surface_cat, R.drawable.surface_hair, R.drawable.surface_mask};

            for (int i = 0; i < img.length; i++) {
                arrActionWeek.add(new SurfaceViewModel(String.valueOf(i), String.valueOf(img[i])));
            }


            adapter = new ViewPagerAdapterAction(ActCameraSurfaceView.this, arrActionWeek);
            viewPager.setAdapter(adapter);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewPagerAdapterAction extends PagerAdapter {
        // Declare Variables
        Context context;
        ArrayList<SurfaceViewModel> arrActionWeek;
        LayoutInflater inflater;

        public ViewPagerAdapterAction(Context context, ArrayList<SurfaceViewModel> arrActionWeek) {
            this.context = context;
            this.arrActionWeek = arrActionWeek;
        }

        @Override
        public int getCount() {
            return arrActionWeek.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            // Declare Variables
            ImageView image;


            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.row_surface, container, false);


            image = (ImageView) itemView.findViewById(R.id.image);


            // rlBg.setBackgroundResource(Integer.parseInt(arrActionWeek.get(position).bgimg));
            image.setBackgroundResource(Integer.parseInt(arrActionWeek.get(position).img));


            // Add viewpager_item.xml to ViewPager
            ((ViewPager) container).addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Remove viewpager_item.xml from ViewPager
            ((ViewPager) container).removeView((RelativeLayout) object);

        }
    }


    /*
    * Surfaceview 3 methods
    * */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;

        }
    }
}
