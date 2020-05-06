package com.example.downloader;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Quelle: https://www.youtube.com/watch?v=rd6m-6l2xQQ
 */

public class DownloadThread extends Thread{

    static MainActivity mainActivity;

    String link;
    String out;
    Context context;

    public DownloadThread(String link, String out, Context context){
        this.link = link;
        this.out = out;
        this.context = context;
    }

    public void run(){
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
                mainActivity.textView.setText(percent);
            }
            bout.close();
            in.close();
            Toast.makeText(mainActivity, "Finished", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mainActivity, "Exception", Toast.LENGTH_SHORT).show();
        }
    }
}