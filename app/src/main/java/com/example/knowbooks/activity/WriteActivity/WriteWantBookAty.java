package com.example.knowbooks.activity.WriteActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.knowbooks.entity.response.DetailWantBook;
import com.example.knowbooks.entity.response.WantBook;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.MainActivity;
import com.example.knowbooks.fragment.FragmentControl.BookAtyFragmentController;
import com.example.knowbooks.fragment.FragmentControl.WriteWantAtyFragmentController;

/**
 * Created by qq on 2016/4/25.
 */
public class WriteWantBookAty  extends FragmentActivity implements View.OnClickListener {

    private WantBook book=new WantBook();
    private DetailWantBook detailWantBook=new DetailWantBook();

    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    private Fragment mMainActivity;
    private static String phoneNumber;
    public static String getPhoneNumber(){
        return phoneNumber;
    }

    //    private Button rightBtn;
//    private Button leftBtn;
    private WriteWantAtyFragmentController controller=null;

    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write_book);
        initView();
        phoneNumber=getIntent().getStringExtra("phoneNumber");

        controller= WriteWantAtyFragmentController.getInstance(WriteWantBookAty.this, R.id.write_book_framelayout);
        controller.showFragment(0);

    }
    public WriteWantAtyFragmentController getFragmentControl(){
        return controller;
    }
    private void initView(){

        title_left= (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle= (TextView) findViewById(R.id.title_middleTextView);
        title_right= (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);

        title_middle.setText("创建心愿");
//        leftBtn= (Button) findViewById(R.id.write_book_leftBtn);
//        rightBtn= (Button) findViewById(R.id.write_book_rightBtn);
    }

    public WantBook getBook(){
        return book;
    }

    public DetailWantBook getDetaiWant(){return detailWantBook;}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BookAtyFragmentController.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                Intent intent=new Intent(WriteWantBookAty.this, BookActivity.class);
                intent.putExtra("onStart","3");
                startActivity(intent);
                WriteWantBookAty.this.finish();
                break;
            case R.id.title_rightBtn:
                Intent intent1=new Intent(WriteWantBookAty.this, MainActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        controller.getFragment(0).onActivityResult(requestCode, resultCode, data);
    }
}
