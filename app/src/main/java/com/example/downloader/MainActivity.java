package com.example.downloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;

    private EditText editText;
    private Button button;

    public TextView textView;

    String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyDownloadService.mainActivity = this;
        DownloadThread.mainActivity = this;

        editText = findViewById(R.id.editText);
        button = findViewById(R.id.downloadBtn);
        textView = findViewById(R.id.txtVw);

        final String URL = editText.getText().toString();
//        final String Path = Environment.DIRECTORY_DOWNLOADS;
//                + "" + System.currentTimeMillis();
//        File Path = Environment.getExternalStorageDirectory(Environment.DIRECTORY_DOWNLOADS);
        File folder = Environment.getExternalStorageDirectory();
        String[] url = URL.trim().toString().split("/");
        final String filename = url[url.length - 1];

        file = (folder + filename);


        /**
         * Quelle: https://www.youtube.com/watch?v=c-SDbITS_R4
         */

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        //Permission denied, request it
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_STORAGE_CODE);
                    }else{
                        //Permission alredy granted, perform download
                        //startDownload(URL, Environment.getExternalStorageDirectory(Environment.DIRECTORY_DOWNLOADS));
                        startDownload(URL, file);
                    }
                }else{
                    //System os is less than marshmallow, perform download
                    startDownload(URL, file);
                }
            }
        });
    }

    private void startDownload(String link, String out){
        Intent intent = new Intent(MainActivity.this, MyDownloadService.class);
        intent.putExtra("link", link);
        intent.putExtra("out", out);
        startService(intent);
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted from popup, perform download
                    startDownload(editText.getText().toString(), file);
                }else{
                    //permission denied from popup, show error message
                    Toast.makeText(this, "Permission denied :/", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
