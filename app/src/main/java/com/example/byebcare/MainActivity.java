package com.example.byebcare;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Intent serviceIntent = new Intent();

    @BindView(R.id.button) Button button;
    @BindView(R.id.textView) TextView textView;

    @OnClick(R.id.button)
    public void onClick(Button button) {
        button.setText("받음!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        button.setText("동기화");
        startBackgroundService();
        askForCallPermission();
    }
/*
    @Override
    protected void onStart() {
        super.onStart();
        if (serviceIntent == null) {
            serviceIntent = new Intent();
            serviceIntent.setClass(getApplication(), BackgroundService.class);
            startService(serviceIntent);
        }
    }*/

    private void startBackgroundService() {
        serviceIntent.setClass(getApplication(), BackgroundService.class);
        startService(serviceIntent);
    }

    private void askForCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = checkSelfPermission(Manifest.permission.CALL_PHONE);

            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("Permission needed")
                            .setMessage("\"Phone Call\" Permission needed to use a Emergency Call feature. Continue?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(MainActivity.this, "Emergency Call Feature Canceled.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();
                }

                else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                }
            }
        }
    }
}
