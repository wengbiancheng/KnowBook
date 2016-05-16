package com.example.knowbooks.constants;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.knowbooks.entity.User;
import com.example.knowbooks.entity.response.Book;

/**
 * Created by qq on 2016/5/4.
 */
public class AccessTokenKeeper {

    private static final String USER_DATA="data_username_sex";
    private static final String KEY_USERNAME="key_userName";
    private static final String KEY_SEX="key_sex";
    private static final String KEY_PHONE="key_phone";
    private static final String KEY_X="key_x";
    private static final String KEY_Y="key_y";

    /**
     * 保存电话号码(标识符)+昵称+性别到手机中
     * @param context
     * @param PhoneNumber
     * @param UserName
     * @param sex
     */
    public static void writeAccessData(Context context,String PhoneNumber,String UserName,String sex,String x,String y){
        if(context==null){
            return;
        }

        SharedPreferences pref=context.getSharedPreferences(USER_DATA,Context.MODE_APPEND);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString(KEY_PHONE,PhoneNumber);
        editor.putString(KEY_USERNAME,UserName);
        editor.putString(KEY_SEX,sex);
        editor.putString(KEY_X,x);
        editor.putString(KEY_Y,y);
        editor.commit();
    }

    /**
     * 从SharedPreference中读取出数据
     * @param context
     * @return
     */
    public static User readAccessDate(Context context){
        if(context==null){
            return null;
        }
        User user=new User();
        SharedPreferences pref=context.getSharedPreferences(USER_DATA, Context.MODE_APPEND);
        user.setPhoneNumber(pref.getString(KEY_PHONE, ""));
        user.setUserName(pref.getString(KEY_USERNAME, ""));
        user.setSex(pref.getString(KEY_SEX, ""));
        user.setX(pref.getString(KEY_X,""));
        user.setY(pref.getString(KEY_Y,""));
        return user;
    }

    /**
     * 清楚所有的信息
     * @param context
     */
    public static void clear(Context context){
        if(context==null){
            return;
        }

        SharedPreferences pref=context.getSharedPreferences(USER_DATA,Context.MODE_APPEND);
        SharedPreferences.Editor editor=pref.edit();
        editor.clear();
        editor.commit();
    }
}
