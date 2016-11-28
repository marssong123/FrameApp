package com.ssjj.ioc.http;

import java.util.Map;

/**
 * Created by GZ1581 on 2016/5/31
 */
public abstract class StringHandler implements HttpClient.ResultHandler {
    @Override
    public void onSuccess(int code, Map<String, String> respHeaders, byte[] responseData) {
        onSuccess(code, null == responseData ? null : new String(responseData));
    }

    @Override
    public void onFailed(int code, Map<String, String> respHeaders, byte[] responseData, Throwable throwable) {
        onFailed(code, throwable);
    }

    public abstract void onSuccess(int code, String response);

    public abstract void onFailed(int code, Throwable throwable);
}
