package com.example.knowbooks.activity.DetailActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.Book;
import com.example.knowbooks.entity.response.DetailBookList;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.MainActivity;
import com.example.knowbooks.adapter.GridViewAdapter;
import com.example.knowbooks.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq on 2016/4/24.
 */
public class BookListDetailActivity extends Activity implements View.OnClickListener{


    private GridView gridView;

    private GridViewAdapter adapter;
    private List<DetailBookList> list=new ArrayList<>();


    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    private Long BookListId;
    private String LoginUserPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_booklist_detail);
        LoginUserPhone=getIntent().getStringExtra("phoneNumber");

        gridView= (GridView) findViewById(R.id.detail_booklist_gridview);
        adapter=new GridViewAdapter(this,list,gridView,LoginUserPhone);
        gridView.setAdapter(adapter);

        title_left = (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle = (TextView) findViewById(R.id.title_middleTextView);
        title_right = (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_middle.setText("书单详情");

        BookListId=getIntent().getLongExtra("bookListId", 0);
        loadData();
    }
    private void loadData(){
        RequestParams requestParams=new RequestParams();
        requestParams.put("booklistId", BookListId);

        Log.i("Log1","我打开的书单的id是"+ BookListId);
        HttpUtil.getInstance(BookListDetailActivity.this).get(BookListDetailActivity.this, UrlConstant.DetailbooklistUrl, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1", "书单id为"+BookListId+"的请求详情界面结果为:"+response.toString());
                Message message = new Message();
                try {
                    String result = (String) response.get("result");
                    if (result.equals("success")) {

                        JSONArray jsonArray= (JSONArray) response.get("resultSet");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                            DetailBookList bookList=new DetailBookList();
                            String bookPicture=UrlConstant.url+jsonObject.getString("titleImage");
                            String bookName=jsonObject.getString("bookName");
                            long id=jsonObject.getLong("id");
                            bookList.setBookName(bookName);
                            bookList.setBookpicture(bookPicture);
                            bookList.setId(id);

                            list.add(bookList);
                        }

                        message.what = 200;
                        handler.sendMessage(message);


                    } else {
                        message.what = -1;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("Log1","书单id为"+BookListId+"的请求详情界面失败的原因是:"+ responseString.toString());
                Message message = new Message();
                message.what = -1;
                handler.sendMessage(message);
            }
        });
    }

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==200){
                adapter.notifyDataSetChanged();
            }else if(msg.what==-1){

            }
            return false;
        }
    });
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                Intent intent=new Intent(BookListDetailActivity.this, BookActivity.class);
                intent.putExtra("onStart","1");
                intent.putExtra("PhoneNumber",LoginUserPhone);
                startActivity(intent);
                BookListDetailActivity.this.finish();
                break;
            case R.id.title_rightBtn:
                Intent intent1=new Intent(BookListDetailActivity.this, MainActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
