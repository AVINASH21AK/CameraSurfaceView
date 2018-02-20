package com.camerasurfaceview.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camerasurfaceview.R;
import com.camerasurfaceview.api.model.SurfaceViewModel;
import com.camerasurfaceview.common.App;
import com.camerasurfaceview.common.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.lang.String.format;

public class ActCameraSurfaceView extends Activity implements SurfaceHolder.Callback {

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
    FrameLayout rlFinal;

    @BindView(R.id.ivCameraCapture)
    ImageView ivCameraCapture;

    @BindView(R.id.ivViewPgrImg)
    ImageView ivViewPgrImg;

    @BindView(R.id.ivCropImage)
    ImageView ivCropImage;

    Camera camera;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;
    SurfaceHolder surfaceHolder;
    boolean isBackCameraOn = true;
    PictureCallback jpegCallback;


    Bitmap bitmapFinal;
    ImageView image;

    ViewPagerAdapterAction adapter;
    ArrayList<SurfaceViewModel> arrActionWeek = new ArrayList<SurfaceViewModel>();
    CustomProgressDialog customProgressDialog;

    String strFolderPath = "";
    int drawablePosition = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_camera);

        App.setStatusBarGradiant(ActCameraSurfaceView.this);
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

            ivCropImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(bitmapFinal != null)
                    {
                        App.bitmapFinal = bitmapFinal;
                        showCropDialog(); 
                    }else {
                        Toast.makeText(ActCameraSurfaceView.this, "Capture Image First...", Toast.LENGTH_SHORT).show();
                    }
                    
                }
            });



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



            /*---Similar issue
            * https://stackoverflow.com/questions/24961797/android-resize-bitmap-keeping-aspect-ratio
            * */
            jpegCallback = new PictureCallback()
            {
                public void onPictureTaken(byte[] data, Camera camera)
                {
                    try {

                        int width;
                        int height;
                        Matrix matrix = new Matrix();

                        Bitmap drawableSelectedIcon = null;

                        if(drawablePosition == 0) {
                            drawableSelectedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.surface_cat);
                            /*URL url = new URL("http://wolfkeeperuniversity.com/storage/banner_images/1513602678_5.jpg");
                            drawableSelectedIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());*/
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


                        /*
                        * Save Camera Image
                        * */
                        FileOutputStream outStream = new FileOutputStream(strFolderPath);
                        bitmapCameraPicFinal.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.close();


                        /*
                        * Get Camera Image which was saved before
                        * */
                        File fileCameraImgPath = new File(strFolderPath);
                        Bitmap bmCameraImg = BitmapFactory.decodeFile(fileCameraImgPath.getAbsolutePath());


                        /*
                        * show camera and overlaped image together
                        * */
                        rlFinal.setVisibility(View.INVISIBLE);
                        ivCameraCapture.setImageBitmap(bmCameraImg);
                        ivViewPgrImg.setImageBitmap(drawableSelectedIcon);

                        /*
                        * flip camera front image
                        * */
                        if(!isBackCameraOn) {
                            App.showLog(TAG, "flip on::"+isBackCameraOn);
                            ivCameraCapture.setScaleX(-1);
                        }
                        else {
                            App.showLog(TAG, "flip off::"+isBackCameraOn);
                        }


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                try
                                {
                                    /*
                                    * Marge both camera and overlaped image
                                    * */
                                    bitmapFinal = loadBitmapFromView(rlFinal);
                                    customProgressDialog.dismiss();

                                    //Toast.makeText(ActCameraSurfaceView.this, "Image Saved..U can crop it", Toast.LENGTH_SHORT).show();

                                    /*
                                    * save final marge image
                                    * */
                                    FileOutputStream finalOutStream = new FileOutputStream(strFolderPath);
                                    bitmapFinal.compress(Bitmap.CompressFormat.PNG, 100, finalOutStream);
                                    finalOutStream.close();


                                    /*
                                    * Save Bitmap and pass to CropActivity
                                    * */

                                    /*
                                    * get final saved image
                                    * */

                                    App.bitmapFinal = bitmapFinal;
                                    /*File margeImgFile = new File(strFolderPath);
                                    App.bitmapFinal = BitmapFactory.decodeFile(margeImgFile.getAbsolutePath());*/

                                    /*Intent i1 = new Intent(ActCameraSurfaceView.this, ActCrop.class);
                                    startActivity(i1);*/


                                    //Refresh camera view
                                    System.out.println("camera re-start and opening photo");
                                    rlFinal.setVisibility(View.GONE);
                                    customProgressDialog.dismiss();

                                    //Open capture image
                                    File imageFile = new File(strFolderPath);
                                    openSaveImg(imageFile);

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        },350);


                        showCameraInSurfaceView();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static Bitmap loadBitmapFromView(View v) {

        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;

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

                    Camera.Parameters myParameters = camera.getParameters();
                    Camera.Size myBestSize = previewSize(myParameters);

                    if(myBestSize != null){
                        myParameters.setPreviewSize(myBestSize.width, myBestSize.height);
                        myParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        camera.setParameters(myParameters);

                        App.showLog(TAG, "width: "+myBestSize.width+" height: "+myBestSize.height);
                        Toast.makeText(getApplicationContext(), "Best Size:\n" + String.valueOf(myBestSize.width) + " : " + String.valueOf(myBestSize.height), Toast.LENGTH_LONG).show();
                    }


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
            //String img[] = {"http://wolfkeeperuniversity.com/storage/banner_images/1513602678_5.jpg"};

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



            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.row_surface, container, false);



            image = (ImageView) itemView.findViewById(R.id.image);


            //image.setBackgroundResource(Integer.parseInt(arrActionWeek.get(position).img));

            Picasso.with(context)
                    .load(Integer.parseInt(arrActionWeek.get(position).img))
                    //.load(arrActionWeek.get(position).img)
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

        try{
            //showCameraInSurfaceView();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }



    /*-----width: 1440 height: 1080--------*/
    public static Camera.Size previewSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        return determineBestSize(sizes);
    }

    /*----width: 4128 height: 3096-----*/
    public static Camera.Size pictureSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        return determineBestSize(sizes);
    }

    protected static Camera.Size determineBestSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = null;
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long availableMemory = Runtime.getRuntime().maxMemory() - used;
        for (Camera.Size currentSize : sizes) {
            int newArea = currentSize.width * currentSize.height;
            long neededMemory = newArea * 4 * 4; // newArea * 4 Bytes/pixel * 4 needed copies of the bitmap (for safety :) )
            boolean isDesiredRatio = (currentSize.width / 4) == (currentSize.height / 3);
            boolean isBetterSize = (bestSize == null || currentSize.width > bestSize.width);
            boolean isSafe = neededMemory < availableMemory;
            if (isDesiredRatio && isBetterSize && isSafe) {
                bestSize = currentSize;
            }
        }
        if (bestSize == null) {
            return sizes.get(0);
        }
        return bestSize;
    }


    public void showCropDialog() {
        {
            final Dialog dialog = new Dialog(ActCameraSurfaceView.this);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_crop);


            TextView tvNo = (TextView) dialog.findViewById(R.id.tvNo);
            TextView tvYes = (TextView) dialog.findViewById(R.id.tvYes);


            dialog.show();

            tvNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    Intent i1 = new Intent(ActCameraSurfaceView.this, ActCrop.class);
                    i1.putExtra(App.strCropFreely, "No");
                    startActivity(i1);
                }
            });

            tvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    Intent i1 = new Intent(ActCameraSurfaceView.this, ActCrop.class);
                    i1.putExtra(App.strCropFreely, "Yes");
                    startActivity(i1);


                }
            });

        }
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
