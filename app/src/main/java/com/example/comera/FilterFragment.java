package com.example.comera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.opengl.GLSurfaceView;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FilterFragment extends Fragment {
    private static final String TAG = "FilterFragment";
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private ExecutorService cameraExecutor;
    private CameraSelector cameraSelector;
    private GLSurfaceView glSurfaceView;
    private OpenGLRenderer renderer;

    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        viewFinder = view.findViewById(R.id.viewFinder);
        glSurfaceView = view.findViewById(R.id.glSurfaceView);
        cameraExecutor = Executors.newSingleThreadExecutor();

        renderer = new OpenGLRenderer();
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        setupFilterButtons(view);

        ShutterButton shutterButton = view.findViewById(R.id.shutterButton);
        shutterButton.setOnClickListener(v -> takePicture());

        ImageButton switchCameraButton = view.findViewById(R.id.switchCameraButton);
        switchCameraButton.setOnClickListener(v -> switchCamera());

        return view;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .setTargetRotation(viewFinder.getDisplay().getRotation())
                        .build();

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(viewFinder.getDisplay().getRotation())
                        .build();

                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void switchCamera() {
        if (cameraProvider == null) return;

        int newLensFacing = (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) ?
                CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(newLensFacing)
                .build();

        startCamera();
    }

    private void setupFilterButtons(View view) {
        ImageButton filter1Button = view.findViewById(R.id.filter1Button);
        ImageButton filter2Button = view.findViewById(R.id.filter2Button);
        ImageButton filter3Button = view.findViewById(R.id.filter3Button);

        filter1Button.setOnClickListener(v -> applyFilter(1));
        filter2Button.setOnClickListener(v -> applyFilter(2));
        filter3Button.setOnClickListener(v -> applyFilter(3));
    }

    private void applyFilter(int filter) {
        Toast.makeText(getContext(), "Applied filter " + filter, Toast.LENGTH_SHORT).show();
        renderer.setFilterType(filter);
        glSurfaceView.requestRender();
    }

    private void takePicture() {
        if (imageCapture == null) {
            return;
        }

        File photoFile = new File(getActivity().getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(getContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Log.d(TAG, "Photo capture succeeded: " + photoFile.getAbsolutePath());
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
            }
        });
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
