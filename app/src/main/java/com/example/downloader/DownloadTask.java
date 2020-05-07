package com.example.downloader;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

/**
 * @author Willi Hollatz
 * SMSB4, 17952
 *
 * Quellen / Hilfen:
 * https://www.youtube.com/watch?v=rd6m-6l2xQQ
 */

public class DownloadTask extends AsyncTask<URL, Integer, Double> {

    public static MainActivity mainActivity;

    File file;

    @Override
    protected Double doInBackground(URL... urls) {

        File pathName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        URL url = urls[0];
        String[] urlSplit = url.toString().split("/");
        final String filename = urlSplit[urlSplit.length - 1];
        File fileNameWithPath = new File((pathName + "/" + filename));
        file = fileNameWithPath;

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

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainActivity, "Download startet", Toast.LENGTH_SHORT).show();
                }
            });

            while ((read = in.read(buffer, 0, 1024)) >= 0) {
                bout.write(buffer, 0, read);
                downloaded += read;
                percentDownloaded = (downloaded * 100) / fileSize;
                publishProgress((int) percentDownloaded);
            }
            bout.close();
            in.close();

        } catch (MalformedInputException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainActivity, "Error :(", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return percentDownloaded;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mainActivity.progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Double aDouble) {
            Toast.makeText(mainActivity,
//                    "Fertig"
                    "Download Abgeschlossen! \nSpeicherort: " + file
                    , Toast.LENGTH_LONG).show();
    }

}
