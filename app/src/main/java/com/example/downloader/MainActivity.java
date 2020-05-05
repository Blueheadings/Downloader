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

    private TextView textView;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        file = new File(folder, filename);


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

    private void startDownload(String link, File out){
        /*Download download = new Download(link, out, this);
        download.run();

//        textView.setText((int) download.run());

        return download.run();

         */

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

    private void startDownload2() {

            String url = editText.getText().toString().trim();
            //create download request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            //allow types of networks to download files
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle("Download"); //set title in download notification
            request.setDescription("Downloading file...");

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + System.currentTimeMillis()); //get current timestamp as file name

            //get download service and enqueue
            DownloadManager manager= (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);



//            double downloaded = Double.parseDouble(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
//            double totalSize = Double.parseDouble(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
//
//            double percent = (totalSize*100)/downloaded;

//            String percenString = String.format("%.4f",percent) + " %";
//            editText.setText(percenString);


            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(this.getTaskId());

            Cursor c = manager.query(query);
            if (c.moveToFirst()) {
                int sizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                long size = c.getInt(sizeIndex);
                long downloaded = c.getInt(downloadedIndex);
                double progress = 0.0;
                if (size != -1) progress = downloaded * 100.0 / size;
                // At this point you have the progress as a percentage.

                String percenString = String.format("%.4f", progress);
                textView.setText(percenString.toString());
            }

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                        Toast.makeText(context, "Download Completed!", Toast.LENGTH_SHORT).show();
                    }

                }
            };

            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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
