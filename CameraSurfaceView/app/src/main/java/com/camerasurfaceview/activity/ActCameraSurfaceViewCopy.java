package com.camerasurfaceview.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.camerasurfaceview.R;
import com.camerasurfaceview.api.model.SurfaceViewModel;
import com.camerasurfaceview.common.App;
import com.camerasurfaceview.common.CustomProgressDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ActCameraSurfaceViewCopy extends Activity implements SurfaceHolder.Callback {


    /*------  For Camera & Gallery Img Pick start  ------*/
    static final int PICK_FROM_CAMERA = 1;
    static final int CROP_FROM_CAMERA = 2;
    //ImageView ivUserPic ;

    Uri mImageCaptureUri;
    String ImageUplodationPath = "";
    Bitmap photoBitmap;
    /*------  For Camera & Gallery Img Pick  Finish ------*/


    FrameLayout framelayTop;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    ImageView ivCaptuer, ivCameraSwitch;

    boolean isBackCameraOn = false;
    PictureCallback jpegCallback;

    ViewPager viewPager;
    ViewPagerAdapterAction adapter;
    ArrayList<SurfaceViewModel> arrActionWeek = new ArrayList<SurfaceViewModel>();
    CustomProgressDialog customProgressDialog;

    String strFolderPath = "";

    /* -------- All References -------------
    * surfaceview - http://android-er.blogspot.in/2010/12/camera-preview-on-surfaceview.html
    * overlay - https://stackoverflow.com/questions/21877525/save-overlay-image-camerapreview
    * overlay2 - https://stackoverflow.com/questions/25721628/android-drawing-multiple-different-size-bitmaps-to-same-size-canvas
    * viewToBitmap - https://stackoverflow.com/questions/10674570/how-to-convert-linearlayout-to-image
    *https://stackoverflow.com/questions/2801116/converting-a-view-to-bitmap-without-displaying-it-in-android
    * */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_camera);

        initialize();
        clickEvent();
        setViewPager();


    }

    @Override
    protected void onResume() {
        super.onResume();

        ivCameraSwitch.performClick();
    }

    public void initialize() {
        try {

            customProgressDialog = new CustomProgressDialog(this);
            ivCaptuer = (ImageView) findViewById(R.id.ivCaptuer);
            ivCameraSwitch = (ImageView) findViewById(R.id.ivCameraSwitch);

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


    public Camera getCamera() {
        return camera;
    }

    public void clickEvent() {
        try {


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
                        customProgressDialog.show();
                        strFolderPath = App.DB_PATH + "Img_" + App.getCurrentTimeStamp() + ".png";
                        captureImage2();

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

                        int width;
                        int height;
                        Matrix matrix = new Matrix();

                        Bitmap drawableSelectedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.surface_cat);
                        Bitmap cameraPic = BitmapFactory.decodeByteArray(data, 0, data.length);
                        width = cameraPic.getWidth();
                        height = cameraPic.getHeight();

                        if(!isBackCameraOn)
                        {
                            System.out.println("isBackCameraOn: "+isBackCameraOn);
                            float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
                            Matrix matrixMirrorY = new Matrix();
                            matrixMirrorY.setValues(mirrorY);

                            matrix.postConcat(matrixMirrorY);
                            matrix.setRotate(270);
                        }
                        else
                        {
                            System.out.println("isBackCameraOn: "+isBackCameraOn);
                            matrix.setRotate(90);
                        }



                        Bitmap bitmapDrawableFinal = Bitmap.createScaledBitmap(drawableSelectedIcon,  width, height,   true);
                        Bitmap bitmapCameraPicFinal = Bitmap.createBitmap(cameraPic, 0, 0, width, height, matrix, true);



                        Bitmap bitmap = Bitmap.createBitmap(bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(bitmap);


                        Drawable drawable1 = new BitmapDrawable(bitmapCameraPicFinal);
                        Drawable drawable2 = new BitmapDrawable(drawableSelectedIcon);


                        drawable1.setBounds(0, 0, bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight());
                        drawable2.setBounds(0, 0, bitmapCameraPicFinal.getWidth(), bitmapCameraPicFinal.getHeight());
                        drawable1.draw(c);
                        drawable2.draw(c);




                        FileOutputStream outStream = new FileOutputStream(strFolderPath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.close();








                        /*
                        * Marging both images
                        * */
                        /*Bitmap finalBitmap = overlay(bitmapDrawableFinal, bitmapCameraPicFinal);

                        System.out.println("save image and camera stop ");
                        camera.stopPreview();

                        FileOutputStream outStream = new FileOutputStream(strFolderPath);
                        finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.close();*/




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
                        openScreenshot(imageFile);


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


    public void captureImage2() throws IOException {
        try {
            camera.takePicture(null, null, jpegCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    * reference - https://stackoverflow.com/questions/21877525/save-overlay-image-camerapreview
    * */
    public Bitmap overlay(Bitmap drawableImg, Bitmap cameraImg) {
        try {

            Bitmap bmOverlay = Bitmap.createBitmap(cameraImg.getWidth(), cameraImg.getHeight(), cameraImg.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(cameraImg, 0, 0, null);
            canvas.drawBitmap(drawableImg, 0, 0, null);
            return bmOverlay;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    /*
    * reference - https://stackoverflow.com/questions/25721628/android-drawing-multiple-different-size-bitmaps-to-same-size-canvas
    * */
    public Bitmap overlay2(Bitmap bmp1, Bitmap bmp2) {
        try {

            int size = Math.round(64 * getResources().getDisplayMetrics().density);
            int imagesLength = 2;
            Bitmap bmp = Bitmap.createBitmap(bmp2.getWidth(), bmp2.getWidth(), Bitmap.Config.ARGB_8888); // the bitmap you paint to
            Canvas canvas = new Canvas(bmp);

            for (int i = 0; i < imagesLength; i++) {
                Bitmap mImage;// = getImage(i);
                if(i==0){
                    mImage = bmp2;
                }else {
                    mImage = bmp1;
                }

                int width = mImage.getWidth();
                int height= mImage.getHeight();
                float ratio = width/(float)height;
                if(ratio>1)
                {
                    mImage = Bitmap.createScaledBitmap(mImage , size, (int) (size/ratio), true);
                }
                else
                {
                    mImage = Bitmap.createScaledBitmap(mImage , (int) (size*ratio), size, true);
                }

                canvas.drawBitmap(mImage,0, 0, null);
            }


            return bmp;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }


    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    /*
    * reference - https://stackoverflow.com/questions/10674570/how-to-convert-linearlayout-to-image
    * */
    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
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


            adapter = new ViewPagerAdapterAction(ActCameraSurfaceViewCopy.this, arrActionWeek);
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


    /*--------------    Camera Start  ----------------------*/
    /*private void getImageFromCamera() {
        try {

            App.showLog("GalaNotes", "From CAMERA Setting Capture TRUE");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mImageCaptureUri = Uri.fromFile(new File(App.DB_PATH, "tmp_profile" + String.valueOf(System.currentTimeMillis()) + ".jpg")); //Environment.getExternalStorageDirectory()

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            try {
                intent.putExtra("return-data", true);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {


            switch (requestCode) {

                case Crop.REQUEST_PICK:
                    try {
                        beginCrop(data.getData());
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;

                case Crop.REQUEST_CROP:
                    handleCrop(resultCode, data);
                    break;

                case PICK_FROM_CAMERA:
                    try {
                        beginCrop(mImageCaptureUri);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;


                case CROP_FROM_CAMERA:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        photoBitmap = extras.getParcelable("data");
                    }
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beginCrop(Uri source) throws IOException {
        try {
            File f = new File(App.DB_PATH, "profile.jpg");//Environment.getExternalStorageDirectory()

            Uri outputUri = Uri.fromFile(f);
            mImageCaptureUri = outputUri;
            Crop.of(source, outputUri).asSquare().start(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        try {
            if (resultCode == RESULT_OK) {

                photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(result));
                ImageUplodationPath = getRealPathFromURI(Crop.getOutput(result));

                App.showLog("ImageUplodationPath", ImageUplodationPath);


                //ivUserPic.setImageURI("");
                //ivUserPic.setImageURI(Crop.getOutput(result));

            } else if (resultCode == Crop.RESULT_ERROR) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getRealPathFromURI(Uri contentURI) {

        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) {
            // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }*/
    /*--------------       Camera Finish         ----------------------*/


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
