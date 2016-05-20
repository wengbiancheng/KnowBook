package com.example.knowbooks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.knowbooks.BaseFragment;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.MyActivity.ChatMessage;
import com.example.knowbooks.activity.MyActivity.My;
import com.example.knowbooks.activity.MyActivity.MyBookListAty;
import com.example.knowbooks.activity.MyActivity.MyBuyBookAty;
import com.example.knowbooks.activity.MyActivity.MyShowBookAty;
import com.example.knowbooks.activity.MyActivity.MyWantBookAty;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.User;
import com.example.knowbooks.utils.DButil;
import com.example.knowbooks.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by qq on 2016/4/19.
 */
public class LeftSlideMenu extends BaseFragment implements View.OnClickListener {


    private View view;
    private LinearLayout myBook;
    private LinearLayout myBookList;
    private LinearLayout myBuyBook;
    private LinearLayout myWantBook;
    private LinearLayout my;

    private LinearLayout myMessage;
    private BookActivity BaseActivity;

    private CircleImageView userImage;
    private ImageLoader imageLoader;

    private TextView myName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Baseactivity = (BookActivity) getActivity();
        this.imageLoader = ImageLoader.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();

        return view;
    }

    private void initView() {
        view = View.inflate(Baseactivity, R.layout.fragment_left_menu, null);
        myBook = (LinearLayout) view.findViewById(R.id.left_menu_myShowBook);
        myBookList = (LinearLayout) view.findViewById(R.id.left_menu_myBookList);
        myBuyBook = (LinearLayout) view.findViewById(R.id.left_menu_myBuyBook);
        myWantBook = (LinearLayout) view.findViewById(R.id.left_menu_myWantBook);

        my = (LinearLayout) view.findViewById(R.id.left_menu_my);

        myMessage = (LinearLayout) view.findViewById(R.id.left_menu_myMessage);
        Log.i("Test1","phone:"+BookActivity.getPhoneNumber());
        userImage = (CircleImageView) view.findViewById(R.id.left_menu_myImage);
        myName = (TextView) view.findViewById(R.id.self_name);

        BookActivity bookAty= (BookActivity) getActivity();
        User user=bookAty.getUser();
        String phone=bookAty.getPhoneNumber();
        if ((user == null) || !user.getPhoneNumber().equals(phone)){

        }else {
            String imageUrl = user.getImageUrl();

            if (!TextUtils.isEmpty(imageUrl)) {
                Log.i("Test1", imageUrl);
                imageLoader.displayImage(UrlConstant.url + imageUrl, userImage);
            }
            if (!TextUtils.isEmpty(user.getUserName())) {
                myName.setText(user.getUserName());
            }
        }

        myMessage.setOnClickListener(this);
        myBook.setOnClickListener(this);
        myBookList.setOnClickListener(this);
        myBuyBook.setOnClickListener(this);
        myWantBook.setOnClickListener(this);

        my.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_menu_myShowBook:
                Intent intent = new Intent(getActivity(), MyShowBookAty.class);
                startActivity(intent);
                Baseactivity.finish();
                break;
            case R.id.left_menu_myBookList:
                Intent intent1 = new Intent(getActivity(), MyBookListAty.class);
                intent1.putExtra("phoneNumber", BaseActivity.getPhoneNumber());
                startActivity(intent1);
                Baseactivity.finish();
                break;
            case R.id.left_menu_myBuyBook:
                Intent intent2 = new Intent(getActivity(), MyBuyBookAty.class);
                startActivity(intent2);
                Baseactivity.finish();
                break;
            case R.id.left_menu_myWantBook:
                Intent intent3 = new Intent(getActivity(), MyWantBookAty.class);
                startActivity(intent3);
                Baseactivity.finish();
                break;
            case R.id.left_menu_myMessage:
                Intent intent4 = new Intent(getActivity(), ChatMessage.class);
                intent4.putExtra("phone", BaseActivity.getPhoneNumber());
                startActivity(intent4);
                Baseactivity.finish();
                break;
            case R.id.left_menu_my:
                Intent intent6 = new Intent(getActivity(), My.class);
                intent6.putExtra("phoneNumber", BaseActivity.getPhoneNumber());
                startActivity(intent6);
                Baseactivity.finish();
                break;
        }
    }
}
