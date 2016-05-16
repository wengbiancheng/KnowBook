package com.example.knowbooks.fragment.WriteFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.WriteActivity.WriteBookAty;

/**
 * Created by qq on 2016/4/23.
 */
public class WriteBookFragment3 extends Fragment implements View.OnClickListener{

    private WriteBookAty BaseActivity;


    private Button rightBtn;
    private Button leftBtn;

    private TextView textView_book_introduct;
    private EditText book_introduct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseActivity= (WriteBookAty) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_write_book3,null);

        leftBtn= (Button) view.findViewById(R.id.write_book_leftBtn);
        rightBtn= (Button) view.findViewById(R.id.write_book_rightBtn);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        textView_book_introduct= (TextView) view.findViewById(R.id.write_book_IntroductText);
        book_introduct= (EditText) view.findViewById(R.id.write_book_Introduct);
        textView_book_introduct.setText("书籍的简介：");
        book_introduct.setHint("请输入书籍的简单介绍");
        checkisBack();
        return view;
    }

    private void checkisBack(){
        if(!TextUtils.isEmpty(BaseActivity.getBook().getBookSummary())){
            book_introduct.setText(BaseActivity.getBook().getBookSummary());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.write_book_leftBtn:
                BaseActivity.getFragmentControl().showFragment(1);
                break;
            case R.id.write_book_rightBtn:
                if(!TextUtils.isEmpty(book_introduct.getText().toString())){
                    BaseActivity.getBook().setBookSummary(book_introduct.getText().toString());
                    BaseActivity.getFragmentControl().showFragment(3);
                }else{
                    Toast.makeText(BaseActivity, "请补全信息", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
