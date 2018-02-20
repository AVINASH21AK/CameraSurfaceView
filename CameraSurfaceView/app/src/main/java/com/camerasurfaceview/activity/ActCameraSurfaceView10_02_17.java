package com.camerasurfaceview.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.lang.String.format;

public class ActCameraSurfaceView10_02_17 extends Activity implements SurfaceHolder.Callback {

    String TAG = "ActCameraSurfaceView";

    @BindView(R.id.surfaceview)
    SurfaceView surfaceView;

    @BindView(R.id.ivCaptuer)
    ImageView ivCaptuer;

    @BindView(R.id.ivCameraSwitch)
    ImageView ivCameraSwitch;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.rlFinal)
    RelativeLayout rlFinal;

    @BindView(R.id.ivCameraCapture)
    ImageView ivCameraCapture;

    @BindView(R.id.ivViewPgrImg)
    ImageView ivViewPgrImg;


    @BindView(R.id.ivFinalMerge)
    ImageView ivFinalMerge;


    Camera camera;
    SurfaceHolder surfaceHolder;
    boolean isBackCameraOn = true;
    PictureCallback jpegCallback;



    ImageView image;

    ViewPagerAdapterAction adapter;
    ArrayList<SurfaceViewModel> arrActionWeek = new ArrayList<SurfaceViewModel>();
    CustomProgressDialog customProgressDialog;

    String strFolderPath = "";
    int drawablePosition = 0;


    /*-------Draw over image
    * http://android-er.blogspot.in/2015/12/open-image-free-draw-something-on.html
    * */


    /* -------- All References -------------
    * https://stackoverflow.com/questions/1540272/android-how-to-overlay-a-bitmap-draw-over-a-bitmap
    * https://stackoverflow.com/questions/37827937/camera-live-preview-freezes-on-camera-switch
    * surfaceview - http://android-er.blogspot.in/2010/12/camera-preview-on-surfaceview.html
    * overlay - https://stackoverflow.com/questions/21877525/save-overlay-image-camerapreview
    * overlay2 - https://stackoverflow.com/questions/25721628/android-drawing-multiple-different-size-bitmaps-to-same-size-canvas
    * viewToBitmap - https://stackoverflow.com/questions/10674570/how-to-convert-linearlayout-to-image
    * resize bitmap - https://stackoverflow.com/questions/9958362/change-bitmap-resolution-in-android-app
    * async save bitmap - https://github.com/josnidhin/Android-Camera-Example/blob/master/src/com/example/cam/CamTestActivity.java
    * */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_camera);

        App.setStatusBarGradiant(ActCameraSurfaceView10_02_17.this);
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

                        //customProgressDialog.show();
                        strFolderPath = App.DB_PATH + "Img_" + App.getCurrentTimeStamp() + ".png";
                        captureImage2();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });



            /*---Similar issue
            * https://stackoverflow.com/questions/24961797/android-resize-bitmap-keeping-aspect-ratio
            * */
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
                            //drawableSelectedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.surface_cat);
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


                        if(!isBackCameraOn)
                        {
                            //Front Camera
                            System.out.println("isBackCameraOn: "+isBackCameraOn);
                            matrix.setRotate(270);
                        }
                        else
                        {
                            //Back Camera
                            System.out.println("isBackCameraOn: "+isBackCameraOn);
                            matrix.setRotate(90);
                        }


                        /*
                        * Marging camera & viewpager img with same height and width
                        * */
                        Bitmap bitmapCameraPicFinal = Bitmap.createBitmap(cameraPic, 0, 0, width, height, matrix, true);


                        App.showLog(TAG, "bitmapCameraPicFinal Width: "+bitmapCameraPicFinal.getWidth());  //3096
                        App.showLog(TAG, "bitmapCameraPicFinal Height: "+bitmapCameraPicFinal.getHeight());  //4128
                        App.showLog(TAG, "drawableSelectedIcon Width: "+drawableSelectedIcon.getWidth());  //529
                        App.showLog(TAG, "drawableSelectedIcon Height: "+drawableSelectedIcon.getHeight());  //609   -- ratio w/h=0.86863711

                        //back camera w-3096 h-4128
                        //Front camera  w-1080 h-1920
                        //drawable w-529 h-609

                        ivFinalMerge.setVisibility(View.GONE);
                        rlFinal.setVisibility(View.VISIBLE);
                        ivCameraCapture.setImageBitmap(bitmapCameraPicFinal);
                        ivViewPgrImg.setImageBitmap(drawableSelectedIcon);


                        
                       // Bitmap bitmap = loadBitmapFromView(rlFinal);

                        ivCameraCapture.setVisibility(View.GONE);
                        ivViewPgrImg.setVisibility(View.GONE);
                        ivFinalMerge.setVisibility(View.VISIBLE);


                        //Bitmap bitmap123 = overlayBitmapToCenter(bitmapCameraPicFinal, getScaledBitmap(drawableSelectedIcon, width, height));
                        Bitmap bitmap123 = overlayBitmapToCenter(bitmapCameraPicFinal, scaledPhotoToFile(drawableSelectedIcon, width, height));
                        //Bitmap bitmap123 = overlayBitmapToCenter(bitmapCameraPicFinal, drawableSelectedIcon);

                        ivFinalMerge.setImageBitmap(bitmap123);
                       // ivFinalMerge.setImageBitmap(bitmap);






                        /*Bitmap bitmap = Bitmap.createBitmap(bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(bitmap);

                        Drawable drawable1 = new BitmapDrawable(bitmapCameraPicFinal);
                        Drawable drawable2 = new BitmapDrawable(drawableSelectedIcon);

                        drawable1.setBounds(0, 0, bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight());
                        drawable2.setBounds(0, 0, bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight());

                        drawable1.draw(c);
                        drawable2.draw(c);*/


                       /* Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int deviceWidth = size.x;
                        int deviceHeight = size.y;*/


                       /* int drawableWidth = (width*drawableSelectedIcon.getWidth())/deviceWidth;
                        int drawableHeight = (height*drawableSelectedIcon.getHeight())/deviceHeight;*/

                        /*int drawableHeight= (width*drawableSelectedIcon.getHeight())/drawableSelectedIcon.getWidth();
                        int  drawableWidth = (height*drawableSelectedIcon.getWidth())/drawableSelectedIcon.getHeight();

                        App.showLog(TAG, "drawableWidth: "+drawableWidth);  //4586
                        App.showLog(TAG, "drawableHeight: "+drawableHeight);  //1290

                        App.showLog(TAG, "deviceWidth: "+deviceWidth);  //1080
                        App.showLog(TAG, "deviceHeight: "+deviceHeight);  //1920

                        App.showLog(TAG, "drawableSelectedIcon Width: "+drawableSelectedIcon.getWidth());  //1200
                        App.showLog(TAG, "drawableSelectedIcon Height: "+drawableSelectedIcon.getHeight());  //800

                        App.showLog(TAG, "camera Width: "+width);  //4128
                        App.showLog(TAG, "camera Height: "+height);  //3096*/





                        //Bitmap bitmap = overlay(bitmapCameraPicFinal, getResizedBitmap(drawableSelectedIcon, drawableWidth, drawableHeight));



                        //saving img after margin to single image
                        /*FileOutputStream outStream = new FileOutputStream(strFolderPath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.close();

                        //Refresh camera view
                        System.out.println("camera re-start and opening photo");
                        rlFinal.setVisibility(View.GONE);
                        customProgressDialog.dismiss();
                        showCameraInSurfaceView();

                        //Open capture image
                        File imageFile = new File(strFolderPath);
                        openSaveImg(imageFile);*/


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                /*} catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/



                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Bitmap getScaledBitmap(Bitmap b, int reqWidth, int reqHeight)
    {
        /*Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, b.getWidth(), b.getHeight()), new RectF(0, 0, reqWidth, reqHeight), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);*/


        Bitmap background = Bitmap.createBitmap((int)reqWidth, (int)reqHeight, Bitmap.Config.ARGB_8888);

        float originalWidth = b.getWidth();
        float originalHeight = b.getHeight();

        Canvas canvas = new Canvas(background);

        float scale = reqWidth / originalWidth;

        float xTranslation = 0.0f;
        float yTranslation = (reqHeight - originalHeight * scale) / 2.0f;

        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(b, transformation, paint);

        return background;
    }


    public Bitmap scaledPhotoToFile(Bitmap photoBm, int reqWidth, int reqHeight) {
        try{

        int bmOriginalWidth = photoBm.getWidth();
        int bmOriginalHeight = photoBm.getHeight();

        double originalWidthToHeightRatio =  1.0 * bmOriginalWidth / bmOriginalHeight;
        double originalHeightToWidthRatio =  1.0 * bmOriginalHeight / bmOriginalWidth;

        int maxHeight = reqHeight;
        int maxWidth = reqWidth;

        photoBm = getScaledBitmap(photoBm, bmOriginalWidth, bmOriginalHeight, originalWidthToHeightRatio, originalHeightToWidthRatio,maxHeight, maxWidth);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photoBm.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        }catch (Exception e){
            e.printStackTrace();
        }

        return photoBm;
    }

    private Bitmap getScaledBitmap(Bitmap bm, int bmOriginalWidth, int bmOriginalHeight, double originalWidthToHeightRatio, double originalHeightToWidthRatio, int maxHeight, int maxWidth) {
        if(bmOriginalWidth > maxWidth || bmOriginalHeight > maxHeight) {
            App.showLog(TAG, format("RESIZING bitmap FROM %sx%s ", bmOriginalWidth, bmOriginalHeight));

            if(bmOriginalWidth > bmOriginalHeight) {
                bm = scaleDeminsFromWidth(bm, maxWidth, bmOriginalHeight, originalHeightToWidthRatio);
            } else if (bmOriginalHeight > bmOriginalWidth){
                bm = scaleDeminsFromHeight(bm, maxHeight, bmOriginalHeight, originalWidthToHeightRatio);
            }

            App.showLog(TAG, format("RESIZED bitmap TO %sx%s ", bm.getWidth(), bm.getHeight())) ;
        }
        return bm;
    }

    private Bitmap scaleDeminsFromHeight(Bitmap bm, int maxHeight, int bmOriginalHeight, double originalWidthToHeightRatio) {
        int newHeight = (int) Math.max(maxHeight, bmOriginalHeight * .55);
        int newWidth = (int) (newHeight * originalWidthToHeightRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }

    private static Bitmap scaleDeminsFromWidth(Bitmap bm, int maxWidth, int bmOriginalWidth, double originalHeightToWidthRatio) {
        //scale the width
        int newWidth = (int) Math.max(maxWidth, bmOriginalWidth * .75);
        int newHeight = (int) (newWidth * originalHeightToWidthRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }


    /*
    * https://stackoverflow.com/questions/1540272/android-how-to-overlay-a-bitmap-draw-over-a-bitmap
    * */
    public static Bitmap overlayBitmapToCenter(Bitmap bitmap1, Bitmap bitmap2) {
        int bitmap1Width = bitmap1.getWidth();
        int bitmap1Height = bitmap1.getHeight();
        int bitmap2Width = bitmap2.getWidth();
        int bitmap2Height = bitmap2.getHeight();

        float marginLeft = (float) (bitmap1Width * 0.5 - bitmap2Width * 0.5);
        float marginTop = (float) (bitmap1Height * 0.5 - bitmap2Height * 0.5);

        Bitmap overlayBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, bitmap1.getConfig());
        Canvas canvas = new Canvas(overlayBitmap);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, marginLeft, marginTop, null);
        return overlayBitmap;
    }

    public static Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            App.showLog("--------", "loadBitmapFromView if condition");
            v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }else {
            App.showLog("--------", "loadBitmapFromView else condition");
            Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            v.draw(c);
            return b;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
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


            adapter = new ViewPagerAdapterAction(ActCameraSurfaceView10_02_17.this, arrActionWeek);
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



            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.row_surface, container, false);



            image = (ImageView) itemView.findViewById(R.id.image);


            //image.setBackgroundResource(Integer.parseInt(arrActionWeek.get(position).img));

            Picasso.with(context)
                    //.load(Integer.parseInt(arrActionWeek.get(position).img))
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
