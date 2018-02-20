package com.camerasurfaceview.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.camerasurfaceview.R;
import com.camerasurfaceview.common.App;
import com.camerasurfaceview.common.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActHidePic extends AppCompatActivity {

    String TAG = "ActHidePic";

    @BindView(R.id.btnOpenGallery)
    Button btnOpenGallery;

    /*------  For Camera & Gallery Img Pick start  ------*/
    String selectedFilePath;
    String selectedFileName;
    static final int PICK_FROM_GALLERY = 1;
    private Dialog dialogpicker;
    /*------  For Camera & Gallery Img Pick  Finish ------*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_hidepic);

        App.setStatusBarGradiant(ActHidePic.this);
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

            btnOpenGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callCaptureInit();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /*--------------    Camera and Gallery Img  Start  ----------------------*/
    private void callCaptureInit() {
        try {
            captureImageInitialization();
            if (!dialogpicker.isShowing()) {
                dialogpicker.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void captureImageInitialization() {

        try {
            final String[] items;
            items = new String[]{"Select from gallery"};

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActHidePic.this, android.R.layout.select_dialog_item, items);
            AlertDialog.Builder builder = new AlertDialog.Builder(ActHidePic.this);

            builder.setTitle("Select Image");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int item) {
                    d.dismiss();

                    if(item == 0)
                        getImageFromGallery();


                }
            });

            dialogpicker = builder.create();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void getImageFromGallery() {
        try {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/* video/*");
            startActivityForResult(pickIntent, PICK_FROM_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode != RESULT_OK)
                return;

            switch (requestCode) {

                case PICK_FROM_GALLERY:
                    Uri selectedMediaUri = data.getData();
                    App.showLog(TAG, "Uri selectedMediaUri: "+selectedMediaUri);

                    selectedFilePath = FileUtils.getPath(ActHidePic.this, selectedMediaUri);
                    selectedFileName = selectedFilePath.substring(selectedFilePath.lastIndexOf("/")+1);
                    selectedFilePath = selectedFilePath.replace(selectedFileName, "");

                    App.showLog(TAG, "selectedFilePath: "+selectedFilePath);
                    App.showLog(TAG, "selectedFileName: "+selectedFileName);
                    App.showLog(TAG, "App.strDicFullPath: "+App.strDicFullPath);

                    moveFile(selectedFilePath, selectedFileName, App.strDicFullPath);

                    break;


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*--------------       Camera and Gallery Img Finish         ----------------------*/




    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + File.separator + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;


            encrypt(inputPath + inputFile, outputPath);


            /*------------- delete the original file -------------*/
            //new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            App.showLog(TAG, "catch FileNotFoundException: "+fnfe1.getMessage());
        }
        catch (Exception e) {
            App.showLog(TAG, "catch Exception: "+e.getMessage());
        }

    }

    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();
        }
        catch (Exception e) {
            App.showLog(TAG, e.getMessage());
        }
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            App.showLog(TAG, fnfe1.getMessage());
        }
        catch (Exception e) {
            App.showLog(TAG, e.getMessage());
        }

    }


    /*
    * Encrypt File
    * */
    static void encrypt(String inputPath, String outputPath) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream(inputPath);
        FileOutputStream fos = new FileOutputStream(outputPath);

        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);

        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }


    static void decrypt() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream("data/encrypted");

        FileOutputStream fos = new FileOutputStream("data/decrypted");
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }

}
