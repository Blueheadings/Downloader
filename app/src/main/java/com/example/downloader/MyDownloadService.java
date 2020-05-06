package com.example.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.Nullable;

/**
 * Quellen / Hilfen: https://stackoverflow.com/questions/40447421/how-can-download-file-in-service-in-android
 * https://www.vogella.com/tutorials/AndroidServices/article.html
 */

public class MyDownloadService extends Service {

    static MainActivity mainActivity;

    String link;
    String out;

    @Override
    public void onCreate() {
        super.onCreate();

        Thread downloadThread = new DownloadThread(link, out, mainActivity);
        downloadThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
