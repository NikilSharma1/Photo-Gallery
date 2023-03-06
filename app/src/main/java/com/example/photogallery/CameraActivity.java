package com.example.photogallery;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.photogallery.databinding.ActivityMainBinding;
import com.example.photogallery.databinding.CameraActivityBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 1;
    private CameraActivityBinding binding;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private Preview preview;
    private Camera camera;
    private ImageCapture imageCapture=null;
    private CameraSelector cameraSelector;
    private ImageAnalysis imageAnalysis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=CameraActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                //We can show our custom dialog here

            }
            else {
                //It will show android's default dialog box
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST);
                //This will show a permission dialog and onRequestPermissionResult function will be called
            }
        }
        else {
            startCamera();
        }
        binding.imageCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("info","came here1");
                String name = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                ContentValues contentValues = new ContentValues();

                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {

                    contentValues.put(MediaStore.Images.Media.DATE_TAKEN, name);

                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,
                            "Pictures/Camera-X");


                }
                Log.i("info","came here2");
                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions.Builder(getContentResolver(),MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues).build();
                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(CameraActivity.this),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                                Toast.makeText(CameraActivity.this, "Photo Captured", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(ImageCaptureException error) {
                                Toast.makeText(CameraActivity.this, "Failed to Capture", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                Log.i("info","came here3");
            }
        });
    }

    public void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                //bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
            // Preview
            preview = new Preview.Builder()
                    .build();
            preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());


            imageCapture = new ImageCapture.Builder()
                    .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                    .build();

            imageAnalysis = new ImageAnalysis.Builder()
                    // enable the following line if RGBA output is needed.
                    //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setTargetResolution(new Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();

            imageAnalysis.setAnalyzer(getMainExecutor(), new ImageAnalysis.Analyzer() {
                @Override
                public void analyze(@NonNull ImageProxy imageProxy) {
                    int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                    // insert your code here.

                    // after done, release the ImageProxy object
                    imageProxy.close();
                }
            });
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalysis);

            } catch(Exception e) {
                Log.i( "Use case binding failed", "error");
            }
        }, ContextCompat.getMainExecutor(this));


    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                        //previewfunction();
                        //imageAnaylsisSetup();
                        //imageCaptureSetup();
                        startCamera();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}
