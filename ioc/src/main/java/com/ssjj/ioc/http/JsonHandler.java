package com.ssjj.ioc.http;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.ssjj.ioc.log.L;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by GZ1581 on 2016/5/31
 */

public abstract class JsonHandler<T extends Object> implements HttpClient.ResultHandler {
    public static final int JsonParseError = -1010;
    public static final int HttpResponseNull = -1011;

    @Override
    public void onSuccess(int code, Map<String, String> respHeaders, byte[] responseData) {
        String data = null == responseData ? null : new String(responseData);
        if (null == data) {
            onFailed(HttpResponseNull, null);
            L.error("JsonHandler", "get response success but data null %d", code);
            return;
        }

        T json;
        try {
            json = new Gson().fromJson(data, getSuperclassTypeParameter(getClass()));
        } catch (Exception e) {
            onFailed(JsonParseError, e);
            return;
        }

        if (null != json) {
            onSuccess(code, json);
        } else {
            onFailed(JsonParseError, null);
        }
    }

    @Override
    public void onFailed(int code, Map<String, String> respHeaders, byte[] responseData, Throwable throwable) {
        onFailed(code, throwable);
    }

    private Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        Type type = parameterized.getRawType();
        if (type.equals(JsonHandler.class)) {
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        } else {
            return getSuperclassTypeParameter(subclass.getSuperclass());
        }
    }

    public abstract void onSuccess(int code, T jsonObject);

    public abstract void onFailed(int code, Throwable throwable);
}
