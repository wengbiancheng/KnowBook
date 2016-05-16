package com.example.knowbooks.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;


public class HttpUtil {

    private static AsyncHttpClient client=null; // 实例话对象


    // 用一个完整url获取一个string对象
    public static void get(String urlString, AsyncHttpResponseHandler res) {
        client.get(urlString, res);
    }

    // url里面带参数
    public static void get(String urlString, RequestParams params,
                           AsyncHttpResponseHandler res){
        client.get(urlString, params, res);

    }

    // 不带参数，获取json对象或者数组
    public static void get(String urlString, JsonHttpResponseHandler res) {
        client.get(urlString, res);
    }

    // 带参数，获取json对象或者数组
    public static void get(String urlString, RequestParams params,
                           JsonHttpResponseHandler res) {
        client.get(urlString, params, res);
    }

    // 下载数据使用，会返回byte数据
    public static void get(String uString, BinaryHttpResponseHandler bHandler) {
        client.get(uString, bHandler);
    }

    public static AsyncHttpClient getInstance(Context context){
        if(client==null){
            client=new AsyncHttpClient();
            client.setTimeout(10000);
            PersistentCookieStore cookieStore=new PersistentCookieStore(context);
            client.setCookieStore(cookieStore);
        }
        return client;
    }
}

