package com.example.knowbooks.activity.MyActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.DetailActivity.BookDetailActivity;
import com.example.knowbooks.adapter.book_show_adapter;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.Book;
import com.example.knowbooks.entity.response.CollectBookList;
import com.example.knowbooks.utils.HttpUtil;
import com.google.gson.Gson;
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
public class MyShowBookAty  extends Activity implements View.OnClickListener{

    private PullToRefreshListView listView;
    private book_show_adapter adapter;
    private List<Book> list = new ArrayList<>();

    private JSONArray resultSet;

    private View footView;
    private int curPage = 0;

    //标题栏控件初始化
    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_show);
        listView = (PullToRefreshListView)findViewById(R.id.show_listView);
        adapter = new book_show_adapter(this, list, handler);
        listView.setAdapter(adapter);
        footView = View.inflate(this, R.layout.footview_loading, null);
        LoadData(0);

        initListener();
    }



    private void initListener() {

        title_left = (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle = (TextView) findViewById(R.id.title_middleTextView);
        title_right = (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_middle.setText("我推荐的书籍");

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                LoadData(0);
            }
        });
        //点击进行书籍详情界面的展示
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyShowBookAty.this, BookDetailActivity.class);
                intent.putExtra("id", list.get(position - 1).getId());
                startActivity(intent);

                finish();
            }
        });
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                LoadData(curPage + 1);
            }
        });

        listView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ShowAlertDialogDelete(position);
                return true;
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
        requestParams.put("bookId", list.get(position-1).getId());
        Log.i("del","要删除的书本的书名是："+list.get(position-1).getBookName());
        HttpUtil.getInstance(this).get(this, UrlConstant.DeleteShowBook, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("del", "删除mySHowBook的成功信息是：" + response.toString());
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
                Log.i("del","删除mySHowBook的错误信息是："+responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
                Message message = new Message();
                message.what = -2;
                message.obj = "删除失败" + responseString;
                handler.sendMessage(message);
            }
        });
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int code = msg.what;
            if (code == 200) {
                listView.onRefreshComplete();
                adapter.notifyDataSetChanged();
            } else if (code == -1) {
                listView.onRefreshComplete();
            } else if (code == 300) {
                Toast.makeText(MyShowBookAty.this,"删除成功",Toast.LENGTH_SHORT).show();
                list.remove(msg.arg1-1);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MyShowBookAty.this, "错误信息为:" + msg.obj, Toast.LENGTH_SHORT).show();
            }

            return false;
        }
    });


    /**
     * 与服务器进行showfragment数据的传输设置
     *
     * @param page
     */
    private void LoadData(final int page) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("page", page + "");
        HttpUtil.getInstance(this).get(this, UrlConstant.FragmentshowUrl, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Message message = new Message();
                    String result = (String) response.get("result");
                    if (result.equals("success")) {
                        curPage = page;
                        message.what = 200;
                        resultSet = (JSONArray) response.get("resultSet");
                        if (page == 0) {
                            list.clear();
                        } else {
                            if (resultSet.length() == 0) {
                                removeFootView(listView, footView);
                            } else {
                                addFootView(listView, footView);
                            }
                        }
                        for (int i = 0; i < resultSet.length(); i = i + 2) {
                            JSONObject json1 = (JSONObject) resultSet.get(i);
                            JSONObject json2 = (JSONObject) resultSet.get(i + 1);

                            Book book = new Book();
                            book = new Gson().fromJson(json1.toString(), Book.class);
                            book.setIsCollect((int) json2.get("isCollect"));
                            book.setNumOfComments((int) json2.get("numOfComments"));
                            book.setUserName((String) json2.get("userName"));
                            book.setUserSex((String) json2.get("sex"));
                            list.add(book);
                        }
                        handler.sendMessage(message);
                    } else {
                        message.what = -1;
                        message.obj = result;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("Log1", responseString);
                Message message = new Message();
                message.what = -1;
                handler.sendMessage(message);
            }
        });

    }

    private void addFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() == 1) {
            lv.addFooterView(footView);
        }
    }

    private void removeFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if (lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
    }

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
