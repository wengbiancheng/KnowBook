package com.example.knowbooks.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by qq on 2016/7/22.
 */
public class NoImageRequest extends JsonRequest<JSONObject> {

    public NoImageRequest(String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, requestBody, listener, errorListener);
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded;charset=" + getParamsEncoding();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonString=new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers,PROTOCOL_CHARSET));
            return Response.success(new JSONObject(jsonString),HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
