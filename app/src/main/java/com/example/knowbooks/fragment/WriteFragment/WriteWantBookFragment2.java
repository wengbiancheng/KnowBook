package com.example.knowbooks.fragment.WriteFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.WriteActivity.WriteBookAty;
import com.example.knowbooks.activity.WriteActivity.WriteWantBookAty;

/**
 * Created by qq on 2016/4/23.
 */
public class WriteWantBookFragment2 extends Fragment implements View.OnClickListener{

    private WriteWantBookAty BaseActivity;

    private EditText bookAuthor;
    private Spinner bookType;

    private Button rightBtn;
    private Button leftBtn;

    private ArrayAdapter<String> adapter;
    private static final String[] m = {"杂志", "小说","校园教材","其他"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseActivity = (WriteWantBookAty) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_book2, null);
        bookAuthor = (EditText) view.findViewById(R.id.write_book_bookAuthor);
        bookType = (Spinner) view.findViewById(R.id.write_book_Type);


        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(BaseActivity, android.R.layout.simple_spinner_item, m);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        bookType.setAdapter(adapter);

        //添加事件Spinner事件监听
        bookType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BaseActivity.getBook().setBookClass(m[(int) id]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                BaseActivity.getBook().setBookClass(m[0]);
            }
        });


        //设置默认值
        bookType.setVisibility(View.VISIBLE);
        bookType.setSelection(0);

        leftBtn = (Button) view.findViewById(R.id.write_book_leftBtn);
        rightBtn = (Button) view.findViewById(R.id.write_book_rightBtn);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        CheckIsBack();
        return view;
    }

    private void CheckIsBack() {
        if (!TextUtils.isEmpty(BaseActivity.getBook().getWantBookAuthor())) {
            bookAuthor.setText(BaseActivity.getBook().getWantBookAuthor());
        }
        if (!TextUtils.isEmpty(BaseActivity.getBook().getBookClass())) {
            int t = 0;
            for (int i = 0; i < m.length; i++) {
                if (BaseActivity.getBook().getBookClass().equals(m[i])) {
                    t = i;
                    break;
                }
            }
            bookType.setSelection(t);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write_book_leftBtn:
                BaseActivity.getFragmentControl().showFragment(0);
                break;
            case R.id.write_book_rightBtn:
                if (!TextUtils.isEmpty(bookAuthor.getText().toString()) &&
                        !TextUtils.isEmpty(BaseActivity.getBook().getBookClass())) {
                    BaseActivity.getBook().setWantBookAuthor(bookAuthor.getText().toString());
                    BaseActivity.getFragmentControl().showFragment(2);
                } else {
                    Toast.makeText(BaseActivity, "请补全信息", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
