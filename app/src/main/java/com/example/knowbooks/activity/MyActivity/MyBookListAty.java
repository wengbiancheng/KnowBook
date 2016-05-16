package com.example.knowbooks.activity.MyActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.DetailActivity.BookListDetailActivity;
import com.example.knowbooks.adapter.BLSonFragmentAdapter;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.BookList;
import com.example.knowbooks.utils.HttpUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq on 2016/5/10.
 */
public class MyBookListAty extends Activity implements View.OnClickListener{

    private PullToRefreshListView listView;
    private BLSonFragmentAdapter adapter;


    private View footView;
    private int curPage=0;

    private List<BookList> list=new ArrayList<>();

    //标题栏控件初始化
    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_booklist);
        listView= (PullToRefreshListView)findViewById(R.id.sonfragment_listView);
        adapter=new BLSonFragmentAdapter(this,list,handler);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(0);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyBookListAty.this, BookListDetailActivity.class);
                intent.putExtra("bookListId", list.get(position - 1).getId());
                startActivity(intent);
                finish();
            }
        });
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData(curPage + 1);
            }
        });
        loadData(0);
        initListener();
    }
    private void initListener(){

        title_left = (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle = (TextView) findViewById(R.id.title_middleTextView);
        title_right = (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_middle.setText("我创建的书单");
        listView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ShowAlertDialogDelete(position);
                return false;
            }
        });
    }

    private void ShowAlertDialogDelete(final int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage("您真的要删除这条信息吗?").setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendToDelete(position);
            }
        }).create();
        alertDialog.show();
    }

    private void SendToDelete(final int position) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("booklistId", list.get(position-1).getId());
        HttpUtil.getInstance(this).get(this, UrlConstant.DeleteBookList, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String result = (String) response.get("result");
                    if (result.equals("success")) {
                        Message message = new Message();
                        message.what = 300;
                        message.arg1 = position;
                        handler.sendMessage(message);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Message message = new Message();
                message.what = -2;
                message.obj = "删除失败" + responseString;
                handler.sendMessage(message);
            }
        });
    }
    private void loadData(final int page){
        RequestParams requestParams=new RequestParams();
        requestParams.put("page", page);

        HttpUtil.getInstance(this).get(this, UrlConstant.FragmentbooklistHotUrl,requestParams,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("HotFragment1", response.toString());
                Message message=new Message();
                try {
                    String result= (String) response.get("result");
                    message.obj=result;
                    if(result.equals("success"))
                    {
                        message.what=200;

                        JSONArray json= (JSONArray) response.get("resultSet");
                        if (page == 0) {
                            list.clear();
                        }

                        curPage = page;
                        for(int i=0;i<json.length();i=i+2)
                        {
                            JSONObject jsonObject= (JSONObject) json.get(i);
                            BookList bookList=new BookList();
                            bookList.setId(jsonObject.getLong("id"));
                            bookList.setBooklistPicture(jsonObject.getString("booklistPicture"));
                            bookList.setCreateDate(jsonObject.getLong("createDate"));
                            bookList.setBookListName(jsonObject.getString("bookListName"));
                            bookList.setCreaterId(jsonObject.getString("createrId"));

                            JSONObject jsonObject1= (JSONObject) json.get(i+1);
                            bookList.setIsCollect(jsonObject1.getInt("isCollect"));
                            bookList.setPeopleCount(jsonObject1.getInt("peopleCount"));
                            bookList.setBookCount(jsonObject1.getInt("bookCount"));
                            list.add(bookList);
                        }
                        handler.sendMessage(message);
                    }else{
                        message.what=-1;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("HotFragment1", responseString.toString());
                Message message=new Message();
                message.what=-1;
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
                Log.i("WorthFrgment1", "fail的原因:"+msg.obj);
            }else if(msg.what==300){
                Toast.makeText(MyBookListAty.this,"删除成功",Toast.LENGTH_SHORT).show();
                list.remove(msg.arg1-1);
                adapter.notifyDataSetChanged();
            }else if(msg.what==-2){
                Toast.makeText(MyBookListAty.this, "错误信息为:" + msg.obj, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_leftImageBtn:
                Intent intent=new Intent(this, BookActivity.class);
                startActivity(intent);
                break;
            case R.id.title_rightBtn:
                break;
            default:
                break;
        }
    }
}
