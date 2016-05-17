package com.example.knowbooks.activity.MyActivity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.knowbooks.entity.User;
import com.example.knowbooks.utils.DButil;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by qq on 2016/5/10.
 */
public class ChatMessage extends Activity{


    private User user;
    private DButil db;
    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db=new DButil(this);
        phoneNumber=getIntent().getStringExtra("phone");
        user=db.getUser(phoneNumber);
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                if (s.equals(user.getPhoneNumber())) {
                    return new UserInfo(s, user.getUserName(), Uri.parse(user.getImageUrl()));
                }
                return null;
            }
        }, true);
        RongIM.getInstance().setMessageAttachedUserInfo(true);
        //启动会话列表界面
        if (RongIM.getInstance() != null)
            RongIM.getInstance().startConversationList(this);
    }
}
