package com.example.knowbooks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.knowbooks.BaseFragment;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.MyActivity.ChatMessage;
import com.example.knowbooks.activity.MyActivity.MyBookListAty;
import com.example.knowbooks.activity.MyActivity.MyBuyBookAty;
import com.example.knowbooks.activity.MyActivity.MyShowBookAty;
import com.example.knowbooks.activity.MyActivity.MyWantBookAty;

/**
 * Created by qq on 2016/4/19.
 */
public class LeftSlideMenu extends BaseFragment implements View.OnClickListener{


    private View view;
    private LinearLayout myBook;
    private LinearLayout myBookList;
    private LinearLayout myBuyBook;
    private LinearLayout myWantBook;

    private LinearLayout myMessage;
    private BookActivity BaseActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Baseactivity= (BookActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();

        return view;
    }

    private void initView(){
        view=View.inflate(Baseactivity, R.layout.fragment_left_menu,null);
        myBook= (LinearLayout) view.findViewById(R.id.left_menu_myShowBook);
        myBookList= (LinearLayout) view.findViewById(R.id.left_menu_myBookList);
        myBuyBook= (LinearLayout) view.findViewById(R.id.left_menu_myBuyBook);
        myWantBook= (LinearLayout) view.findViewById(R.id.left_menu_myWantBook);

        myMessage= (LinearLayout) view.findViewById(R.id.left_menu_myMessage);
        myMessage.setOnClickListener(this);
        myBook.setOnClickListener(this);
        myBookList.setOnClickListener(this);
        myBuyBook.setOnClickListener(this);
        myWantBook.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_menu_myShowBook:
                Intent intent=new Intent(getActivity(), MyShowBookAty.class);
                startActivity(intent);
                Baseactivity.finish();
                break;
            case R.id.left_menu_myBookList:
                Intent intent1=new Intent(getActivity(), MyBookListAty.class);
                startActivity(intent1);
                Baseactivity.finish();
                break;
            case R.id.left_menu_myBuyBook:
                Intent intent2=new Intent(getActivity(), MyBuyBookAty.class);
                startActivity(intent2);
                Baseactivity.finish();
                break;
            case R.id.left_menu_myWantBook:
                Intent intent3=new Intent(getActivity(), MyWantBookAty.class);
                startActivity(intent3);
                Baseactivity.finish();
                break;
            case R.id.left_menu_myMessage:
                Intent intent4=new Intent(getActivity(), ChatMessage.class);
                intent4.putExtra("phone",BaseActivity.getPhone());
                startActivity(intent4);
                Baseactivity.finish();
        }
    }
}
