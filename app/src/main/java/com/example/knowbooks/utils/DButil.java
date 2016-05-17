package com.example.knowbooks.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.example.knowbooks.entity.User;

/**
 * Created by qq on 2016/5/11.
 */
public class DButil {

    private static final String[] COLS=new String[]{"PhoneNumber","Password","UserName","Sex","x","y","UserImageUrl","QQ","Weixin","ConnectPhone","token"};

    private static String DB="knowBookDB";

    private static String TABLE_USER="userDB";
    public static String create_table_user="create table "+TABLE_USER+
            "(PhoneNumber text,Password text,UserName text,Sex text,x text,y text,UserImageUrl text,QQ text,Weixin text,ConnectPhone text,token text);";

    private Context ctx;
    private SQLiteDatabase read_sqlite;
    private SQLiteOpenHelper helper;
    private SQLiteDatabase write_sqlite;

    private int version=5;

    public DButil(Context context){
        this.ctx=context;
        init();
    }

    private void init(){
        if(helper==null){
            helper=new MySQLiteHelper(ctx,DB,null,version);
            write_sqlite=helper.getWritableDatabase();
            read_sqlite=helper.getReadableDatabase();
        }
    }
    public long addUser(User user){

        Cursor cursor=read_sqlite.query(TABLE_USER,COLS, null, null, null, null, null);
        cursor.moveToFirst();
        String phoneNumber;
        int count=cursor.getCount();
        for(int i=0;i<count;i++){
            phoneNumber=cursor.getString(0);

            if(phoneNumber.equals(user.getPhoneNumber()))
            {
                ContentValues value=new ContentValues();
                if(!TextUtils.isEmpty(user.getPassword())){
                    value.put("Password",user.getPassword());
                }
                if(!TextUtils.isEmpty(user.getX())){
                    value.put("x",user.getX());
                    value.put("y",user.getY());
                }
                if(!TextUtils.isEmpty(user.getImageUrl())){
                    value.put("UserImageUrl",user.getImageUrl());
                }
                if(!TextUtils.isEmpty(user.getUserName())){
                    value.put("UserName",user.getUserName());
                }
                if(!TextUtils.isEmpty(user.getSex())){
                    value.put("Sex",user.getSex());
                }
                if(!TextUtils.isEmpty(user.getQQ())){
                    value.put("QQ",user.getQQ());
                }
                if(!TextUtils.isEmpty(user.getWeixin())){
                    value.put("Weixin",user.getWeixin());
                }
                if(!TextUtils.isEmpty(user.getConnectPhone())){
                    value.put("ConnectPhone",user.getConnectPhone());
                }
                if(!TextUtils.isEmpty(user.getToken())){
                    value.put("token",user.getToken());
                }
                Log.i("LoginAdd1","更新数据库的数据时:"+user.toString());
                return write_sqlite.update(TABLE_USER,value,"PhoneNumber="+phoneNumber,null);
            }

            cursor.moveToNext();
        }
        ContentValues values=new ContentValues();
        values.put("PhoneNumber",user.getPhoneNumber());
        if(!TextUtils.isEmpty(user.getPassword())){
            values.put("Password",user.getPassword());
        }
        if(!TextUtils.isEmpty(user.getX())){
            values.put("x",user.getX());
            values.put("y",user.getY());
        }
        if(!TextUtils.isEmpty(user.getImageUrl())){
            values.put("UserImageUrl",user.getImageUrl());
        }
        if(!TextUtils.isEmpty(user.getUserName())){
            values.put("UserName",user.getUserName());
        }
        if(!TextUtils.isEmpty(user.getSex())){
            values.put("Sex",user.getSex());
        }
        if(!TextUtils.isEmpty(user.getQQ())){
            values.put("QQ",user.getQQ());
        }
        if(!TextUtils.isEmpty(user.getWeixin())){
            values.put("Weixin",user.getWeixin());
        }
        if(!TextUtils.isEmpty(user.getConnectPhone())){
            values.put("ConnectPhone",user.getConnectPhone());
        }
        if(!TextUtils.isEmpty(user.getToken())){
            values.put("token",user.getToken());
        }

        Log.i("LoginAdd1","插入到数据库的数据时:"+user.toString());
        return write_sqlite.insert(TABLE_USER,null,values);
    }


    public User getUser(String phoneNumber){
        User user=new User();
        Cursor cursor=read_sqlite.query(TABLE_USER,COLS,"phoneNumber="+phoneNumber,null,null,null,null);
        cursor.moveToFirst();
        if(cursor!=null&&cursor.getCount()>0){
            user.setPhoneNumber(cursor.getString(0));
            if(!TextUtils.isEmpty(cursor.getString(1)))
            {
                user.setPassword(cursor.getString(1));
            }
            if(!TextUtils.isEmpty(cursor.getString(2)))
            {
                user.setUserName(cursor.getString(2));
            }
            if(!TextUtils.isEmpty(cursor.getString(3)))
            {
                user.setSex(cursor.getString(3));
            }
            if(!TextUtils.isEmpty(cursor.getString(4)))
            {
                user.setX(cursor.getString(4));
            }
            if(!TextUtils.isEmpty(cursor.getString(5)))
            {
                user.setY(cursor.getString(5));
            }
            if(!TextUtils.isEmpty(cursor.getString(6)))
            {
                user.setImageUrl(cursor.getString(6));
            }
            if(!TextUtils.isEmpty(cursor.getString(7)))
            {
                user.setQQ(cursor.getString(7));
            }
            if(!TextUtils.isEmpty(cursor.getString(8)))
            {
                user.setWeixin(cursor.getString(8));
            }
            if(!TextUtils.isEmpty(cursor.getString(9)))
            {
                user.setConnectPhone(cursor.getString(9));
            }
            if(!TextUtils.isEmpty(cursor.getString(10)))
            {
                user.setToken(cursor.getString(10));
            }
            Log.i("LoginAdd1","读取到的数据库的数据时:"+user.toString());
            return user;
        }
        return null;
    }

    class MySQLiteHelper extends SQLiteOpenHelper{

        public MySQLiteHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(create_table_user);
            Log.i("LoginAdd1","创建user数据库");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
            this.onCreate(db);
        }
    }

}
