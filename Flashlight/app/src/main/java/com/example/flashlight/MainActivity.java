package com.example.flashlight;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    ImageButton flashlightOff, flashlightOn;
    ConstraintLayout mainLayout;
    boolean isFlashlightOn = false;
    CameraManager cameraManager;
    String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashlightOn = findViewById(R.id.FlashlightOn);
        flashlightOff = findViewById(R.id.FlashlightOff);
        mainLayout = findViewById(R.id.main);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        flashlightOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlashlight();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFlashlightOn) {
            turnOffFlashlight();
            isFlashlightOn = false;
        }
    }

    private void toggleFlashlight() {
        if (!isFlashlightOn) {
            turnOnFlashlight();
            isFlashlightOn = true;
            mainLayout.setBackgroundResource(R.drawable.light_green_round);
        } else {
            turnOffFlashlight();
            isFlashlightOn = false;
            mainLayout.setBackgroundResource(R.drawable.gray_round);
        }
    }

    private void turnOnFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOffFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
