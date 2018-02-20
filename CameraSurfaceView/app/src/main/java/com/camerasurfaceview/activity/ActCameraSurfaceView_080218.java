package com.camerasurfaceview.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.camerasurfaceview.R;
import com.camerasurfaceview.api.model.SurfaceViewModel;
import com.camerasurfaceview.common.App;
import com.camerasurfaceview.common.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActCameraSurfaceView_080218 extends Activity implements SurfaceHolder.Callback {

    @BindView(R.id.surfaceview)
    SurfaceView surfaceView;

    @BindView(R.id.ivCaptuer)
    ImageView ivCaptuer;

    @BindView(R.id.ivCameraSwitch)
    ImageView ivCameraSwitch;


    @BindView(R.id.viewpager)
    ViewPager viewPager;

    Camera camera;
    SurfaceHolder surfaceHolder;
    boolean isBackCameraOn = true;
    PictureCallback jpegCallback;


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

        App.setStatusBarGradiant(ActCameraSurfaceView_080218.this);
         /*
        *ButterKnife
        * */
        ButterKnife.bind(this);

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

            surfaceView.setZOrderOnTop(false);

            getWindow().setFormat(PixelFormat.UNKNOWN);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showCameraInSurfaceView();
            }
        },300);
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



            ivCaptuer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        /* Animation a = AnimationUtils.loadAnimation(ActCameraSurfaceView.this, R.anim.progress_anim);
                        a.setDuration(1000);
                        ivProgressBar.startAnimation(a);*/
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

                        Bitmap drawableSelectedIcon = null;

                        if(drawablePosition == 0) {
                            //drawableSelectedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.originalsurface_cat);
                            URL url = new URL("http://wolfkeeperuniversity.com/storage/banner_images/1513602678_5.jpg");
                            drawableSelectedIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        }
                        else if(drawablePosition == 1)
                            drawableSelectedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.surface_hair);
                        else if(drawablePosition == 2)
                            drawableSelectedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.surface_mask);



                        Bitmap cameraPic = BitmapFactory.decodeByteArray(data, 0, data.length);
                        width = cameraPic.getWidth();
                        height = cameraPic.getHeight();

                        // front camera -- width 1920 -- height 1080
                        // back camera -- width 4128 -- height 3096
                        if(!isBackCameraOn)
                        {
                            System.out.println("isBackCameraOn: "+isBackCameraOn);
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

                        /*Bitmap bitmap = Bitmap.createBitmap(bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(bitmap);

                        Drawable drawable1 = new BitmapDrawable(bitmapCameraPicFinal);
                        Drawable drawable2 = new BitmapDrawable(drawableSelectedIcon);

                        drawable1.setBounds(0, 0, bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight());
                        drawable2.setBounds(0, 0, bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight());

                        drawable1.draw(c);
                        drawable2.draw(c);*/


                        Bitmap bitmap = overlay(bitmapCameraPicFinal, drawableSelectedIcon);


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



    public Bitmap overlay(Bitmap bitmapCameraPicFinal, Bitmap drawableSelectedIcon) {
        try {

            Bitmap bmOverlay = Bitmap.createBitmap(bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight(), drawableSelectedIcon.getConfig());  //Bitmap.Config.ARGB_8888
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(bitmapCameraPicFinal, 0, 0, null);


            float left = (canvas.getWidth()  - drawableSelectedIcon.getWidth()) /2;
            float top = (canvas.getHeight()  - drawableSelectedIcon.getHeight()) /2;
            canvas.drawBitmap(drawableSelectedIcon, left, top, null);
            return bmOverlay;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
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

            //int img[] = {R.drawable.surface_cat, R.drawable.surface_hair, R.drawable.surface_mask};
            String img[] = {"http://wolfkeeperuniversity.com/storage/banner_images/1513602678_5.jpg"};

            for (int i = 0; i < img.length; i++) {
                arrActionWeek.add(new SurfaceViewModel(String.valueOf(i), String.valueOf(img[i])));
            }


            adapter = new ViewPagerAdapterAction(ActCameraSurfaceView_080218.this, arrActionWeek);
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


            //image.setBackgroundResource(Integer.parseInt(arrActionWeek.get(position).img));

            Picasso.with(context)
                    .load(arrActionWeek.get(position).img)
                    .into(image);


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
