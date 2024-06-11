package com.example.comera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoFragment extends Fragment {
    private static final String TAG = "VideoFragment";
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private PreviewView viewFinder;
    private ExecutorService cameraExecutor;
    private boolean isRecording = false;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        viewFinder = view.findViewById(R.id.viewFinder);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        ShutterButton shutterButton = view.findViewById(R.id.shutterButton);
        shutterButton.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
                shutterButton.setRecording(false);
            } else {
                startRecording();
                shutterButton.setRecording(true);
            }
            isRecording = !isRecording;
        });

        return view;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .setTargetRotation(viewFinder.getDisplay().getRotation())
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                cameraProvider.bindToLifecycle(this, cameraSelector, preview);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void startRecording() {
        // 在这里实现视频录制逻辑
        Log.d(TAG, "Video recording started");
    }

    private void stopRecording() {
        // 在这里实现停止录制逻辑
        Log.d(TAG, "Video recording stopped");
    }

    private boolean allPermissionsGranted() {
        for (String permission : new String[]{Manifest.permission.CAMERA}) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
