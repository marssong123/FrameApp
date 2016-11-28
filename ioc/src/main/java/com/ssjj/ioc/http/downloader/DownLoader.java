package com.ssjj.ioc.http.downloader;

import android.os.AsyncTask;

import java.io.File;

/**
 * Created by GZ1581 on 2016/6/6
 */

public final class DownLoader {

    public static DownLoadTask down(String url, File file, DownLoaderListener listener) {
        DownLoadTask task = new DownLoadTask(file, listener);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        return task;
    }

    public interface DownLoaderListener {
        void onSuccess(File file);

        void onFailed(int code, File file);

        void onProgress(int current, int total);

        void onStart(int total);
    }
}
