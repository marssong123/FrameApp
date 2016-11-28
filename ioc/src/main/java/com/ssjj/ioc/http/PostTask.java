package com.ssjj.ioc.http;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import java.util.Map;

/**
 * Created by GZ1581 on 2016/5/30
 */

public class PostTask extends HttpTask {
    private String mBodyContentType;
    private Map<String, String> mBodyParams;
    private byte[] mBody;

    public PostTask(String url, RequestParams params, HttpClient.ResultHandler handler) {
        super(Request.Method.POST, url, params, handler);

        mBodyContentType = params.getBodyContentType();
        mBodyParams = params.getBodyParams();
        mBody = params.getBody();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return null == mBody ? super.getBody() : mBody;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mBodyParams;
    }

    @Override
    public String getBodyContentType() {
        return null == mBodyContentType ? super.getBodyContentType() : mBodyContentType;
    }
}
