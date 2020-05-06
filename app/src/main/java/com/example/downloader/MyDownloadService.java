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

public class MyDownloadService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        downloadFile();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void downloadFile(String link, String out){
        try {
            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            double fileSize = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                fileSize = (double)http.getContentLengthLong();
            }
            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            FileOutputStream fos = new FileOutputStream(out);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] buffer = new byte[1024];
            double downloaded = 0.00;
            int read = 0;
            double percentDownloaded = 0.00;
            while((read = in.read(buffer, 0, 1024)) >= 0){
                bout.write(buffer, 0, read);
                downloaded += read;
                percentDownloaded = (downloaded*100)/fileSize;
                String percent = String.format("%.4f", percentDownloaded);
//                System.out.println("Downloaded " + percent + "%");
//                return percentDownloaded;
                textView.setText(percent);
            }
            bout.close();
            in.close();
//            System.out.println("Finished");
//            Toast.makeText(context, "Finished", Toast.LENGTH_SHORT).show();
//            return 100;
            Toast.makeText(this, "Finished", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
//            return -1;
            Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show();
        }
    }





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
