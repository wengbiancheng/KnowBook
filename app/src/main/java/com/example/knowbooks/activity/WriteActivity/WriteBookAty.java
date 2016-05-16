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

import com.example.knowbooks.entity.response.Book;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.fragment.FragmentControl.BookAtyFragmentController;
import com.example.knowbooks.fragment.FragmentControl.WriteBookAtyFragmentController;
import com.example.knowbooks.fragment.WriteFragment.WriteBookFragment1;

/**
 * Created by qq on 2016/4/23.
 */
public class WriteBookAty extends FragmentActivity implements View.OnClickListener{



    private Book book=new Book();

    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    private Fragment mMainActivity;

//    private Button rightBtn;
//    private Button leftBtn;
    private WriteBookAtyFragmentController controller=null;

    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write_book);
        initView();

        controller= WriteBookAtyFragmentController.getInstance(WriteBookAty.this, R.id.write_book_framelayout);
        controller.showFragment(0);

    }
    public WriteBookAtyFragmentController getFragmentControl(){
        return controller;
    }
    private void initView(){

        book=new Book();

        title_left= (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle= (TextView) findViewById(R.id.title_middleTextView);
        title_right= (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);

        title_middle.setText("创建推荐书籍界面");
//        leftBtn= (Button) findViewById(R.id.write_book_leftBtn);
//        rightBtn= (Button) findViewById(R.id.write_book_rightBtn);
    }


    public Book getBook(){
        return book;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BookAtyFragmentController.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                Intent intent=new Intent(WriteBookAty.this, BookActivity.class);
                startActivity(intent);
                WriteBookAty.this.finish();
                break;
            case R.id.title_rightBtn:
                Intent intent1=new Intent(WriteBookAty.this, WriteBookCommentAty.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        controller.getFragment(0).onActivityResult(requestCode,resultCode,data);
    }
}
