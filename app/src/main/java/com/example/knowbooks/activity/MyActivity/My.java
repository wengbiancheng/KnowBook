package com.example.knowbooks.activity.MyActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.knowbooks.activity.BookActivity;

/**
 * Created by qq on 2016/5/18.
 */
public class My extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent(this, BookActivity.class);
        intent.putExtra("UpUser","yes");
        startActivity(intent);
        this.finish();
    }
}
