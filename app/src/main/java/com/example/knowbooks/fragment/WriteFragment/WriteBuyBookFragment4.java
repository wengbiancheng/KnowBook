package com.example.knowbooks.fragment.WriteFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.WriteActivity.WriteBookAty;
import com.example.knowbooks.activity.WriteActivity.WriteBuyBookAty;
import com.example.knowbooks.activity.WriteActivity.WriteWantBookAty;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.Book;
import com.example.knowbooks.utils.HttpUtil;
import com.example.knowbooks.utils.ImageUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by qq on 2016/4/23.
 */
public class WriteBuyBookFragment4 extends Fragment implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{

    private WriteBuyBookAty BaseActivity;


    private Button rightBtn;
    private Button leftBtn;


    private RadioGroup rg;
    private RadioButton sellType_sell,sellType_change,sellType_lend,sellType_give;

    private EditText price1,price2;
    private Button priceBtn;

    private EditText bookNewOrOld;
    private Button bookNewOrOld1,bookNewOrOld2,bookNewOrOld3,bookNewOrOld4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseActivity= (WriteBuyBookAty) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_write_buybook4,null);

        //初始化公共的上一页和下一页按钮
        leftBtn= (Button) view.findViewById(R.id.write_book_leftBtn);
        rightBtn= (Button) view.findViewById(R.id.write_book_rightBtn);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        rg= (RadioGroup) view.findViewById(R.id.write_buybook_rg);
        sellType_lend= (RadioButton) view.findViewById(R.id.write_buybook_rbtn1);
        sellType_change= (RadioButton) view.findViewById(R.id.write_buybook_rbtn2);
        sellType_sell= (RadioButton) view.findViewById(R.id.write_buybook_rbtn3);
        sellType_give= (RadioButton) view.findViewById(R.id.write_buybook_rbtn4);
        rg.setOnCheckedChangeListener(this);

        price1= (EditText) view.findViewById(R.id.write_buybook_price_one);
        price2= (EditText) view.findViewById(R.id.write_buybook_price_two);
        priceBtn= (Button) view.findViewById(R.id.write_buybook_priceBtn);
        priceBtn.setOnClickListener(this);

        bookNewOrOld= (EditText) view.findViewById(R.id.write_buybook_newold);
        bookNewOrOld1= (Button) view.findViewById(R.id.write_buybook_newold1);
        bookNewOrOld2= (Button) view.findViewById(R.id.write_buybook_newold2);
        bookNewOrOld3= (Button) view.findViewById(R.id.write_buybook_newold3);
        bookNewOrOld4= (Button) view.findViewById(R.id.write_buybook_newold4);
        bookNewOrOld1.setOnClickListener(this);
        bookNewOrOld2.setOnClickListener(this);
        bookNewOrOld3.setOnClickListener(this);
        bookNewOrOld4.setOnClickListener(this);
        bookNewOrOld.setClickable(false);
        bookNewOrOld.setEnabled(false);

        BaseActivity.getBuyBook().setSellingWay(sellType_sell.getText().toString());
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.write_book_leftBtn:
                BaseActivity.getFragmentControl().showFragment(2);
                break;
            case R.id.write_book_rightBtn:
                if(!TextUtils.isEmpty(BaseActivity.getBook().getBookSituation())&&!TextUtils.isEmpty(BaseActivity.getBook().getBookClass())){
                    Double price=Integer.parseInt(price1.getText().toString())+Integer.parseInt(price2.getText().toString())*0.01;
                    String price1=String.valueOf(price);
                    BaseActivity.getBook().setBookPrice(price1);
                    Log.i("Log1","BuyBook的价格是:"+price1);
                    SendToServlet();
                }else{
                    Toast.makeText(BaseActivity,"请补全信息",Toast.LENGTH_SHORT).show();
                }
//                BaseActivity.getFragmentControl().showFragment(4);
                break;
            case R.id.write_buybook_priceBtn://价格后面的按钮
                price1.setText("0");
                price2.setText("00");
                break;
            case R.id.write_buybook_newold1:
                bookNewOrOld.setText(bookNewOrOld1.getText().toString());
                BaseActivity.getBook().setBookSituation(bookNewOrOld1.getText().toString());
                break;
            case R.id.write_buybook_newold2:
                bookNewOrOld.setText(bookNewOrOld2.getText().toString());
                BaseActivity.getBook().setBookSituation(bookNewOrOld2.getText().toString());
                break;
            case R.id.write_buybook_newold3:
                bookNewOrOld.setText(bookNewOrOld3.getText().toString());
                BaseActivity.getBook().setBookSituation(bookNewOrOld3.getText().toString());
                break;
            case R.id.write_buybook_newold4:
                bookNewOrOld.setText(bookNewOrOld4.getText().toString());
                BaseActivity.getBook().setBookSituation(bookNewOrOld4.getText().toString());
                break;
        }
    }

    private void SendToServlet(){
        RequestParams requestParams=new RequestParams();
        requestParams.put("BuyBookName",BaseActivity.getBook().getBookName());
//        requestParams.put("BuyBookPicture",BaseActivity.getBook().getBookPicture());
        requestParams.put("BuyBookAuthor",BaseActivity.getBook().getBookAuthor());
        requestParams.put("Type",BaseActivity.getBook().getBookClass());
        requestParams.put("BuyBookDescript",BaseActivity.getBuyBook().getBookDescript());
        requestParams.put("SellType",BaseActivity.getBuyBook().getSellingWay());
        requestParams.put("price",BaseActivity.getBook().getBookPrice());
        requestParams.put("newOrold",BaseActivity.getBook().getBookSituation());



        Uri uri=Uri.parse(BaseActivity.getBook().getBookPicture());

        String img_path= ImageUtils.getImageAbsolutePath19(BaseActivity, uri);
        File file=new File(img_path);
        if(file.exists()){
            Log.i("Log1","file is exist");
        }else{
            Log.i("Log1","file not exist");
        }
        try {
            requestParams.put("BuyBookPicture",file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.i("Log1","创建书籍传递的数据为："+BaseActivity.getBuyBook().toString());
        HttpUtil.getInstance(BaseActivity).post(BaseActivity, UrlConstant.CreateBuyUrl,requestParams,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1","创建buyBook的结果是:"+response.toString());
                try {
                    String result= (String) response.get("result");
                    if(result.equals("success"))
                    {
                        Toast.makeText(BaseActivity,"创建书籍成功",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(BaseActivity, BookActivity.class);
                        intent.putExtra("onStart",2);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("Log1", "创建购买书籍失败的原因是:" + responseString.toString());
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.write_buybook_rbtn1:
                BaseActivity.getBuyBook().setSellingWay(sellType_lend.getText().toString());
                sellType_lend.setChecked(true);
                break;
            case R.id.write_buybook_rbtn2:
                BaseActivity.getBuyBook().setSellingWay(sellType_change.getText().toString());
                sellType_change.setChecked(true);
                break;
            case R.id.write_buybook_rbtn3:
                BaseActivity.getBuyBook().setSellingWay(sellType_sell.getText().toString());
                sellType_sell.setChecked(true);
                break;
            case R.id.write_buybook_rbtn4:
                BaseActivity.getBuyBook().setSellingWay(sellType_give.getText().toString());
                sellType_give.setChecked(true);
                break;
            default:
                break;
        }
    }
}
