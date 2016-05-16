package com.example.knowbooks.fragment.WriteFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.WriteActivity.WriteBuyBookAty;

/**
 * Created by qq on 2016/4/23.
 */
public class WriteBuyBookFragment5 extends Fragment implements View.OnClickListener{

    private WriteBuyBookAty BaseActivity;


    private Button rightBtn;
    private Button leftBtn;

    private EditText phoneNumber;
    private EditText QQNumber;
    private EditText WeiXinNumber;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseActivity= (WriteBuyBookAty) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_write_buybook5,null);

        leftBtn= (Button) view.findViewById(R.id.write_book_leftBtn);
        rightBtn= (Button) view.findViewById(R.id.write_book_rightBtn);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        phoneNumber= (EditText) view.findViewById(R.id.write_buyBook_phoneNumber);
        QQNumber= (EditText) view.findViewById(R.id.write_buyBook_QQ);
        WeiXinNumber= (EditText) view.findViewById(R.id.write_buyBook_weixin);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.write_book_leftBtn:
                BaseActivity.getFragmentControl().showFragment(1);
                break;
            case R.id.write_book_rightBtn:

        }
    }
}
