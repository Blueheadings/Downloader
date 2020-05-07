package com.example.downloader;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

public class DownloadTask extends AsyncTask<URL, Integer, Double> {

//    @SuppressLint("StaticFieldLeak")
    public static MainActivity mainActivity;
/*
    @Override
    protected void onPreExecute() {
        //Setup Progress Bar Hauptthread

    }

 */

    @Override
    protected Double doInBackground(URL... urls) {

        File pathName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        URL url = urls[0];
        String[] urlSplit = url.toString().split("/");
        final String filename = urlSplit[urlSplit.length - 1];
        File fileNameWithPath = new File((pathName + filename));

        double fileSize = 0;
        double percentDownloaded = 0;
        try {


            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            fileSize = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                fileSize = (double) http.getContentLengthLong();
            }
            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            FileOutputStream fos = new FileOutputStream(fileNameWithPath);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] buffer = new byte[1024];
            double downloaded = 0.00;
            int read = 0;
            percentDownloaded = 0.00;
//            Toast.makeText(mainActivity, "Download startet", Toast.LENGTH_SHORT).show();
            while ((read = in.read(buffer, 0, 1024)) >= 0) {
                bout.write(buffer, 0, read);
                downloaded += read;
                percentDownloaded = (downloaded * 100) / fileSize;
                publishProgress((int) percentDownloaded);
//                percent = String.format("%.4f", percentDownloaded);
/*
                mainActivity.textView.post(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.textView.setText(percent);
                    }
                });

 */

            }
            bout.close();
            in.close();
//            Toast.makeText(mainActivity, "Finished", Toast.LENGTH_SHORT).show();
        } catch (MalformedInputException e) {
            e.printStackTrace();
            /*
            mainActivity.textView.post(new Runnable() {
                @Override
                public void run() {
                    mainActivity.textView.setText("Malformed URL!");
                }
            });

             */
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(mainActivity, "Exception", Toast.LENGTH_SHORT).show();
        }
        return percentDownloaded;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mainActivity.textView.setText(values[0]);
    }
/*
    @Override
    protected void onPostExecute(Double aDouble) {
            Toast.makeText(mainActivity, "Fertig", Toast.LENGTH_SHORT).show();
    }

 */


}
