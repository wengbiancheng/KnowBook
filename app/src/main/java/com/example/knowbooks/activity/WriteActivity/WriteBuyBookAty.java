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
import com.example.knowbooks.entity.response.BuyBook;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.MainActivity;
import com.example.knowbooks.entity.response.DetailBuyBook;
import com.example.knowbooks.fragment.FragmentControl.BookAtyFragmentController;
import com.example.knowbooks.fragment.FragmentControl.WriteBuyAtyFragmentController;

/**
 * Created by qq on 2016/4/25.
 */
public class WriteBuyBookAty extends FragmentActivity implements View.OnClickListener {


    private DetailBuyBook book=new DetailBuyBook();

    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    private Fragment mMainActivity;



    //    private Button rightBtn;
//    private Button leftBtn;
    private WriteBuyAtyFragmentController controller=null;

    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write_book);
        initView();

        controller= WriteBuyAtyFragmentController.getInstance(WriteBuyBookAty.this, R.id.write_book_framelayout);
        controller.showFragment(0);

    }
    public WriteBuyAtyFragmentController getFragmentControl(){
        return controller;
    }
    private void initView(){


        title_left= (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle= (TextView) findViewById(R.id.title_middleTextView);
        title_right= (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_middle.setText("创建售卖书籍");

//        leftBtn= (Button) findViewById(R.id.write_book_leftBtn);
//        rightBtn= (Button) findViewById(R.id.write_book_rightBtn);
    }


    public DetailBuyBook getBuyBook(){
        return book;
    }
    public BuyBook getBook(){
        return book.getBook();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WriteBuyAtyFragmentController.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                Intent intent=new Intent(WriteBuyBookAty.this, BookActivity.class);
                intent.putExtra("onStart","2");
                startActivity(intent);
                WriteBuyBookAty.this.finish();
                break;
            case R.id.title_rightBtn:
                Intent intent1=new Intent(WriteBuyBookAty.this, MainActivity.class);
                startActivity(intent1);
                break;
//            case R.id.write_book_leftBtn:
//                position--;
//                controller.showFragment(position);
//                break;
//            case R.id.write_book_rightBtn:
//                if(position==0){
//                    book.setBookName();
//                }else if(position==1){
//
//                }else if(position==2){
//
//                }
//                position++;
//                controller.showFragment(position);
//                break;
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
