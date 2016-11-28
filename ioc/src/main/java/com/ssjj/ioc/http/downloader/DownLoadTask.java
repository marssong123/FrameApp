package com.ssjj.ioc.http.downloader;

import android.os.AsyncTask;

import com.ssjj.ioc.log.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by GZ1581 on 2016/6/6
 */
public final class DownLoadTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "DownLoadTask";

    public static final int DownLoadCanceled = -202;
    public static final int DownLoadSizeInvalid = -203;

    private static final int TimeOutConnect = 60000;
    private static final int TimeOutRead = 60000;
    private static final int BufferSize = 4096;

    private File mFile;
    private DownLoader.DownLoaderListener mListener;

    public DownLoadTask(File file, DownLoader.DownLoaderListener listener) {
        mFile = file;
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        int respCode = -1;
        HttpURLConnection connection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TimeOutConnect);
            connection.setReadTimeout(TimeOutRead);

            connection.connect();

            respCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK != respCode) {
                L.error(TAG, "connected failed code:%d url:%s", respCode, params[0]);
                mListener.onFailed(respCode, mFile);
                return params[0];
            }

            int totalLength = connection.getContentLength();
            if (0 > totalLength) {
                L.error(TAG, "down load size invalid size: %d, url: %s", totalLength, params[0]);
                mListener.onFailed(DownLoadSizeInvalid, mFile);
                return params[0];
            }

            is = connection.getInputStream();
            fos = new FileOutputStream(mFile, false);

            byte[] buffer = new byte[BufferSize];
            int count = 0;
            int written = 0;

            mListener.onStart(totalLength);

            while (-1 != (count = is.read(buffer))) {
                if (isCancelled()) {
                    respCode = DownLoadCanceled;
                    throw new Exception("down load cancel");
                }

                fos.write(buffer, 0, count);

                written += count;
                mListener.onProgress(written, totalLength);
            }

            mListener.onSuccess(mFile);
        } catch (Throwable e) {
            L.error(TAG, "down load exception resp code: %d, url: %s", respCode, params[0]);
            mListener.onFailed(respCode, mFile);
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    L.error(TAG, "down load close file output stream error: %s, url: %s", e.toString(), params[0]);
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    L.error(TAG, "down load close input stream error: %s, url: %s", e.toString(), params[0]);
                }
            }

            if (null != connection) {
                connection.disconnect();
            }
        }

        return params[0];
    }
}
