package com.example.comera;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, CameraFragment.newInstance());
            transaction.commit();
        }
        SlidingFeatureSelector featureSelector = findViewById(R.id.featureSelector);
        featureSelector.setOnFeatureSelectedListener(feature -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (feature) {
                case "Camera":
                    transaction.replace(R.id.fragment_container, CameraFragment.newInstance());
                    break;
                case "Video":
                    transaction.replace(R.id.fragment_container, VideoFragment.newInstance());
                    break;
                case "Portrait":
                    transaction.replace(R.id.fragment_container, PortraitFragment.newInstance());
                    break;
                case "Night":
                    transaction.replace(R.id.fragment_container, NightFragment.newInstance());
                    break;
                case "Filter":
                    transaction.replace(R.id.fragment_container, FilterFragment.newInstance());
                    break;
            }
            transaction.commit();
        });
    }
}