package com.example.downloader;


import android.content.Context;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.NonNull;

/**
 * Quelle: https://www.youtube.com/watch?v=rd6m-6l2xQQ
 */

public class Download {

    String link;
    String out;
    Context context;

    public Download(String link, String out, Context context){
        this.link = link;
        this.out = out;
        this.context = context;
    }


    public double run(){

        try {
            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            double fileSize = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                fileSize = (double)http.getContentLengthLong();
            }
            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            FileOutputStream fos = new FileOutputStream(this.out);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] buffer = new byte[1024];
            double downloaded = 0.00;
            int read = 0;
            double percentDownloaded = 0.00;
            while((read = in.read(buffer, 0, 1024)) >= 0){
                bout.write(buffer, 0, read);
                downloaded += read;
                percentDownloaded = (downloaded*100)/fileSize;
//                String percent = String.format("%.4f", percentDownloaded);
//                System.out.println("Downloaded " + percent + "%");
                return percentDownloaded;
            }
            bout.close();
            in.close();
//            System.out.println("Finished");
            Toast.makeText(context, "Finished", Toast.LENGTH_SHORT).show();
            return 100;

        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }catch (Exception e){
            return -2;
        }
    }

}
