package com.example.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.Nullable;

/**
 * @author Willi Hollatz
 * SMSB4, 17952
 *
 * Quellen / Hilfen:
 * https://stackoverflow.com/questions/40447421/how-can-download-file-in-service-in-android
 * https://www.vogella.com/tutorials/AndroidServices/article.html
 */

public class MyDownloadService extends Service {

    static MainActivity mainActivity;
    String link;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        link = intent.getExtras().getString("link");
        DownloadTask downloadTask = new DownloadTask();
        try {
            downloadTask.execute(new URL(link));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
