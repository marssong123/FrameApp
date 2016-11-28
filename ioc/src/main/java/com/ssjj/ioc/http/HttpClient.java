package com.ssjj.ioc.http;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ssjj.ioc.application.IocApplication;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by GZ1581 on 2016/5/30
 */
public final class HttpClient {

    static final RequestQueue gQueue;
    static final ThreadPool gResultThreadPool;

    static {
        gResultThreadPool = new ThreadPool();
        gQueue = Volley.newRequestQueue(IocApplication.gContext);
    }

    public static void get(String url, ResultHandler handler) {
        get(url, new RequestParams(), handler);
    }

    public static void get(String url, RequestParams params, ResultHandler handler) {
        HttpTask task = new HttpTask(Request.Method.GET, url, params, handler);
        execute(task, params);
    }

    public static void post(String url, RequestParams params, ResultHandler handler) {
        PostTask task = new PostTask(url, params, handler);
        execute(task, params);
    }

    public static void put(String url, RequestParams params, ResultHandler handler) {
        PutTask task = new PutTask(url, params, handler);
        execute(task, params);
    }

    private static void execute(HttpTask task, RequestParams params) {
        task.updateCache(gQueue.getCache(), params.getCacheType());
        gQueue.add(task);
    }


    public interface ResultHandler {
        void onSuccess(int code, Map<String, String> respHeaders, byte[] responseData);

        void onFailed(int code, Map<String, String> respHeaders, byte[] responseData, Throwable throwable);
    }

    static class ThreadPool implements Executor {
        private java.util.concurrent.ThreadPoolExecutor mExecutor;
        private static final BlockingQueue<Runnable> WorkQueue = new LinkedBlockingQueue<>(64);
        private static final java.util.concurrent.ThreadFactory ThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, String.format("HttpClientThreadPoolExecutor %d", mCount.getAndIncrement()));
            }
        };

        public ThreadPool() {
            mExecutor = new java.util.concurrent.ThreadPoolExecutor(2, 2,
                    60L, TimeUnit.SECONDS, WorkQueue, ThreadFactory);
        }

        @Override
        public void execute(Runnable command) {
            mExecutor.execute(command);
        }
    }
}
