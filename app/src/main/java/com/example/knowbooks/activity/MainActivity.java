package com.example.knowbooks.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.knowbooks.BaseApplication;
import com.example.knowbooks.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import io.rong.ApiHttpClient;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import io.rong.models.FormatType;
import io.rong.models.SdkHttpResult;

public class MainActivity extends Activity {


    private Button login_btn;
    private Button register_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();


    }

    private void initView() {
        login_btn = (Button) findViewById(R.id.main_login);
        register_btn = (Button) findViewById(R.id.main_register);
        initListener();
    }

    private void initListener() {
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

//        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
//            @Override
//            public UserInfo getUserInfo(String s) {
//                if (s.equals("18814122731")) {
//                    return new UserInfo(s, "小明", Uri.parse("https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2354532187,1276923376&fm=58"));
//                }
//                return null;
//            }
//        }, true);
//        RongIM.getInstance().setMessageAttachedUserInfo(true);
    }
}