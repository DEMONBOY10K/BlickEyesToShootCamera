package com.mact.B2SCam;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/* loaded from: classes4.dex */
public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, CameraSource.PictureCallback {
    public static final int CAMERA_REQUEST = 101;
    public static Bitmap bitmap;
    private CameraSource cameraSource;
    private FaceDetector detector;
    private String[] neededPermissions = {"android.permission.CAMERA"};
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    Button home,save,back;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        this.surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        FaceDetector build = new FaceDetector.Builder(this).setProminentFaceOnly(true).setTrackingEnabled(true).setClassificationType(1).setMode(0).build();
        this.detector = build;
        if (!build.isOperational()) {
            Log.w("MainActivity", "Detector Dependencies are not yet available");
            return;
        }
        Log.w("MainActivity", "Detector Dependencies are available");
        if (this.surfaceView != null) {
            boolean result = checkPermission();
            if (result) {
                setViewVisibility(R.id.tv_capture);
                setViewVisibility(R.id.surfaceView);
                setupSurfaceHolder();
            }
        }
        home=(Button)findViewById(R.id.home);
        home.setOnClickListener(view -> {
            Intent i = new Intent(CameraActivity.this,Home.class);
            startActivity(i);
            finish();
        });
        back=(Button)findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent i = new Intent(CameraActivity.this,CameraActivity.class);
            startActivity(i);
            finish();
        });
        save=(Button)findViewById(R.id.saveToGallery);
        save.setOnClickListener(view -> {
            SaveImage(bitmap);
            Toast.makeText(this, "Image saved to Storage", Toast.LENGTH_LONG).show();




            back.performClick();
        });
    }

    private boolean checkPermission() {
        String[] strArr;
        ArrayList<String> permissionsNotGranted = new ArrayList<>();
        for (String permission : this.neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != 0) {
                permissionsNotGranted.add(permission);
            }
        }
        if (!permissionsNotGranted.isEmpty()) {
            boolean shouldShowAlert = false;
            Iterator<String> it = permissionsNotGranted.iterator();
            while (it.hasNext()) {
                shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, it.next());
            }
            if (shouldShowAlert) {
                showPermissionAlert((String[]) permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
            } else {
                requestPermissions((String[]) permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
            }
            return false;
        }
        return true;
    }

    @SuppressLint("ResourceType")
    private void showPermissionAlert(final String[] permissions) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.permission_required);
        alertBuilder.setMessage(R.string.permission_message);
        alertBuilder.setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: com.b2scam.camerademo.ui.MainActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                CameraActivity.this.requestPermissions(permissions);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, 101);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            for (int result : grantResults) {
                if (result == -1) {
                    Toast.makeText(this, (int) R.string.permission_warning, Toast.LENGTH_LONG).show();
                    setViewVisibility(R.string.permission_message);
                    checkPermission();
                    return;
                }
            }
            setViewVisibility(R.id.tv_capture);
            setViewVisibility(R.id.surfaceView);
            setupSurfaceHolder();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setViewVisibility(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void setupSurfaceHolder() {
        this.cameraSource = new CameraSource.Builder(this, this.detector).setFacing(1).setRequestedFps(2.0f).setAutoFocusEnabled(true).build();
        SurfaceHolder holder = this.surfaceView.getHolder();
        this.surfaceHolder = holder;
        holder.addCallback(this);
    }

    public void captureImage() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.b2scam.camerademo.ui.MainActivity.2
            @Override // java.lang.Runnable
            public void run() {
                CameraActivity.this.runOnUiThread(new Runnable() { // from class: com.b2scam.camerademo.ui.MainActivity.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CameraActivity.this.clickImage();
                    }
                });
            }
        }, 200L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clickImage() {
        CameraSource cameraSource = this.cameraSource;
        if (cameraSource != null) {
            cameraSource.takePicture(null, this);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startCamera();
    }

    private void startCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
                return;
            }
            this.cameraSource.start(this.surfaceHolder);
            this.detector.setProcessor(new LargestFaceFocusingProcessor(this.detector, new com.mact.B2SCam.GraphicFaceTracker(this)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.cameraSource.stop();
    }

    @Override // com.google.android.gms.vision.CameraSource.PictureCallback
    public void onPictureTaken(byte[] bytes) {
        Bitmap bitmap2 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        ((ImageView) findViewById(R.id.iv_picture)).setImageBitmap(bitmap2);
        setViewVisibility(R.id.iv_picture);
        bitmap = bitmap2;
        findViewById(R.id.surfaceView).setVisibility(View.GONE);
        findViewById(R.id.tv_capture).setVisibility(View.GONE);
        findViewById(R.id.saveToGallery).setVisibility(View.VISIBLE);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
    }




    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/DCIM/Camera");
        myDir.mkdirs();
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
//        String fname = "Image-"+".jpg";
        String fname = timeStamp+"_b2scam.jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            Log.d("failed_to_save", "Saved file");
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    private void storeImage(Bitmap image) {
//        File pictureFile = getOutputMediaFile();
//        if (pictureFile == null) {
//            Log.d("failed_to_save",
//                    "Error creating media file, check storage permissions: ");// e.getMessage());
//            return;
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(pictureFile);
//            Log.d("failed_to_save", "Saved file");
//            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
//            fos.close();
//        } catch (FileNotFoundException e) {
//            Log.d("failed_to_save", "File not found: " + e.getMessage());
//        } catch (IOException e) {
//            Log.d("failed_to_save", "Error accessing file: " + e.getMessage());
//        }
//    }
//    private  File getOutputMediaFile(){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + getApplicationContext().getPackageName()
//                + "/Files");
//
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                return null;
//            }
//        }
//        // Create a media file name
//        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
//        File mediaFile;
//        String mImageName="MI_"+ timeStamp +".jpg";
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
//        return mediaFile;
//    }
}
