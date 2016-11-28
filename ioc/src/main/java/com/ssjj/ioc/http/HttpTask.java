package com.ssjj.ioc.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.ssjj.ioc.log.L;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Created by GZ1581 on 2016/5/30
 */

public class HttpTask extends Request<byte[]> {
    private HttpClient.ResultHandler mHandler;
    private Map<String, String> mReqHeaders;
    private Map<String, String> mRespHeaders;
    private String mCacheKey;
    private int mStatusCode;
    private Request.Priority mPriority;
    private long mTtl = 0L;
    private long mSoftTtl = 0L;

    private static String prepareUrl(String url, Map<String, String> params) {
        if (null == params || params.isEmpty()) {
            return url + "?";
        }

        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        boolean first = true;
        try {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();

            for (Map.Entry<String, String> entry : entrySet) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }


                if (null != entry.getValue()) {
                    sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    sb.append("=");
                    sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
            }
        } catch (Exception e) {
            return url;
        }

        return sb.toString();
    }

    public HttpTask(int method, String url, RequestParams params, HttpClient.ResultHandler handler) {
        super(method, prepareUrl(url, params.getUrlParams()), null);
        mHandler = handler;
        mReqHeaders = params.getHeaders();
        mCacheKey = params.getCacheKey();
        setShouldCache(null != params.getCacheKey() && !params.getCacheKey().isEmpty());
        setRetryPolicy(new DefaultRetryPolicy(params.getTimeOut(), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mPriority = params.getPriority();
        mTtl = params.getTtl();
        mSoftTtl = params.getSoftTtl();
    }

    void updateCache(Cache cache, RequestParams.CacheType cacheType) {
        if (null != cache && null != mCacheKey && !mCacheKey.isEmpty() && null != cacheType) {
            Cache.Entry entry = cache.get(mCacheKey);
            if (null != entry) {
                if (0 >= mTtl) {
                    mTtl = entry.ttl;
                }

                if (0 >= mSoftTtl) {
                    mSoftTtl = entry.softTtl;
                }

                switch (cacheType) {
                    case CACHE_ONLY:
                        entry.ttl = Long.MAX_VALUE;
                        entry.softTtl = Long.MAX_VALUE;
                        break;
                    case CACHE_THEN_NET:
                        entry.softTtl = 0L;
                        entry.ttl = Long.MAX_VALUE;
                        break;
                    case NET_ONLY:
                        entry.ttl = 0L;
                        entry.softTtl = 0L;
                        break;
                    case AS_CONFIG:
                        break;

                }

                cache.put(mCacheKey, entry);
            }
        }
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        mStatusCode = response.statusCode;
        mRespHeaders = response.headers;
        return Response.success(response.data, parseCacheHeader(response));
    }

    @Override
    protected void deliverResponse(final byte[] response) {
        HttpClient.gResultThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != HttpClient.gQueue) {
                        Cache tempCache = HttpClient.gQueue.getCache();
                        if (null != tempCache && null != mCacheKey && !mCacheKey.isEmpty() && null != tempCache.get(mCacheKey)) {
                            Cache.Entry entry = tempCache.get(mCacheKey);
                            if (mSoftTtl > 0) {
                                entry.softTtl = mSoftTtl;
                            } else {
                                entry.softTtl = 1000;
                            }
                            if (mTtl > 0) {
                                entry.ttl = mTtl;
                            }
                            tempCache.put(mCacheKey, entry);
                        }
                    }

                    if (null != mHandler) {
                        mHandler.onSuccess(mStatusCode, mRespHeaders, response);
                    }
                } catch (Throwable throwable) {
                    L.error(this, Log.getStackTraceString(throwable));
                }
            }
        });
    }

    @Override
    public void deliverError(final VolleyError error) {
        HttpClient.gResultThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != mHandler) {
                        mStatusCode = error.networkResponse.statusCode;
                        mRespHeaders = error.networkResponse.headers;
                        mHandler.onFailed(mStatusCode, mRespHeaders
                                , error.networkResponse.data, error);
                    }
                } catch (Throwable throwable) {
                    L.error(this, Log.getStackTraceString(throwable));
                }
            }
        });
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mReqHeaders;
    }

    @Override
    public String getCacheKey() {
        return mCacheKey;
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    private Cache.Entry parseCacheHeader(NetworkResponse response) {
        if (!shouldCache()) {
            return null;
        }

        return HttpHeaderParser.parseCacheHeaders(response);
    }
}
