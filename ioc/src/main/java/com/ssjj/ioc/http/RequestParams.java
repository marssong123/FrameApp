package com.ssjj.ioc.http;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by GZ1581 on 2016/5/30
 */

public class RequestParams {
    private String mCacheKey = "";
    private Map<String, String> mHeaders = new HashMap<>();
    private int mTimeOut = (int) TimeUnit.SECONDS.toMillis(60);
    private Request.Priority mPriority = Request.Priority.NORMAL;
    private String mBodyContentType = "";
    private Map<String, String> mBodyParams = new HashMap<>();
    private byte[] mBody;
    private CacheType mCacheType = null;
    private long mTtl = 0L;
    private long mSoftTtl = 0L;
    private Map<String, String> mUrlParams = new HashMap<>();

    public enum CacheType {
        CACHE_ONLY,
        NET_ONLY,
        CACHE_THEN_NET,
        AS_CONFIG
    }

    public RequestParams() {

    }

    public void setCacheKey(String key) {
        mCacheKey = key;
    }

    public String getCacheKey() {
        return mCacheKey;
    }

    public void setHeaders(Map<String, String> headers) {
        mHeaders = headers;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public void putHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    public String getHeader(String key) {
        return mHeaders.get(key);
    }

    public void setTimeOut(int seconds) {
        mTimeOut = (int) TimeUnit.SECONDS.toMillis(seconds);
    }

    public int getTimeOut() {
        return mTimeOut;
    }

    public void setPriority(Request.Priority priority) {
        mPriority = priority;
    }

    public Request.Priority getPriority() {
        return mPriority;
    }

    public void setBodyContentType(String bodyContentType) {
        mBodyContentType = bodyContentType;
    }

    public String getBodyContentType() {
        return mBodyContentType;
    }

    public void setBodyParams(Map<String, String> bodyParams) {
        mBodyParams = bodyParams;
    }

    public Map<String, String> getBodyParams() {
        return mBodyParams;
    }

    public void putBodyParams(String key, String value) {
        mBodyParams.put(key, value);
    }

    public String getBodyParam(String key) {
        return mBodyParams.get(key);
    }

    public void setBody(byte[] body) {
        mBody = body;
    }

    public byte[] getBody() {
        return mBody;
    }

    public void setCacheType(CacheType type) {
        mCacheType = type;
    }

    public CacheType getCacheType() {
        return mCacheType;
    }

    public void setTtl(long ttl) {
        mTtl = ttl;
    }

    public long getTtl() {
        return mTtl;
    }

    public void setSoftTtl(long softTtl) {
        mSoftTtl = softTtl;
    }

    public long getSoftTtl() {
        return mSoftTtl;
    }

    public void setUrlParams(Map<String, String> params) {
        mUrlParams = params;
    }

    public Map<String, String> getUrlParams() {
        return mUrlParams;
    }

    public void addUrlParam(String key, String value) {
        mUrlParams.put(key, value);
    }

    public String getUrlParam(String key) {
        return mUrlParams.get(key);
    }
}
