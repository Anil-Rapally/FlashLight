package com.example.flashlightapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 200;
    private ImageButton toggleFlashlight;
    private boolean hasFlash;
    private boolean isFlashOn;
    private CameraManager cameraManager;
    private String cameraId;
    private Animation pulseAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleFlashlight = findViewById(R.id.toggleFlashlight);
        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);

        if (!hasFlash) {
            Toast.makeText(this, "Your device doesn't support flashlight", Toast.LENGTH_SHORT).show();
            toggleFlashlight.setEnabled(false);
        } else {
            toggleFlashlight.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                } else {
                    toggleFlashlight();
                }
            });
        }
    }

    private void toggleFlashlight() {
        try {
            if (cameraId == null) {
                cameraId = cameraManager.getCameraIdList()[0];
            }
            isFlashOn = !isFlashOn;
            cameraManager.setTorchMode(cameraId, isFlashOn);
            updateButtonUI();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to access the camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateButtonUI() {
        if (isFlashOn) {
            toggleFlashlight.setImageResource(R.drawable.flashlight_button);
            toggleFlashlight.startAnimation(pulseAnimation);
        } else {
            toggleFlashlight.setImageResource(R.drawable.flashlight_button);
            toggleFlashlight.clearAnimation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleFlashlight();
            } else {
                Toast.makeText(this, "Camera permission is required to use the flashlight", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFlashOn) {
            toggleFlashlight();
        }
    }
}

