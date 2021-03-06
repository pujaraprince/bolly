package com.android.bolly.television.update;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.android.bolly.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class UpdateActivityTv extends AppCompatActivity {

    TextView tvProgress;
    MaterialButton btnDownload;
    String downloadUrl;
    DownloadFileFromURL downloadFileFromURL = null;


    @Override
    public void onStop() {
        super.onStop();
        if(downloadFileFromURL != null && !downloadFileFromURL.isCancelled()){
            downloadFileFromURL.cancel(true);
            downloadFileFromURL = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tv);

        downloadUrl = getIntent().getStringExtra("downloadUrl");
        tvProgress = findViewById(R.id.tv_progress);
        btnDownload = findViewById(R.id.btn_startdownload);




        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFileFromURL = new DownloadFileFromURL();
                downloadFileFromURL.execute(downloadUrl);
            }
        });


    }



    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvProgress.setVisibility(View.VISIBLE);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();
                Log.d("FILE", "file length : "+ lenghtOfFile);

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ "/bolly.apk");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ "/bolly.apk";
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            tvProgress.setText(progress[0] + "%");
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            Uri apkUri = null;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                apkUri = FileProvider.getUriForFile(UpdateActivityTv.this, getOpPackageName() + ".provider", new File(file_url));
            else
                apkUri = Uri.fromFile(new File(file_url));
            installAPK(apkUri);
        }

    }



    private void installAPK(Uri apkFile) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(apkFile, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
