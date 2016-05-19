package com.example.knowbooks.activity.MyActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.DetailActivity.BuyBookDetailActivity;
import com.example.knowbooks.adapter.BuyFragmentAdapter;
import com.example.knowbooks.adapter.spinnerAdapter;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.BuyBook;
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
public class MyBuyBookAty extends Activity implements View.OnClickListener {



    private PullToRefreshListView listView;
    private BuyFragmentAdapter adapter;

    private List<BuyBook> list = new ArrayList<>();

    private int curPage = 0;

    private View footView;

    //标题栏控件初始化
    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_show);
        listView = (PullToRefreshListView) findViewById(R.id.show_listView);
        adapter = new BuyFragmentAdapter(this, list);
        listView.setAdapter(adapter);

        footView = View.inflate(this, R.layout.footview_loading, null);

        loadData(0);
        initListener();
    }


    private void initListener() {


        title_left = (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle = (TextView) findViewById(R.id.title_middleTextView);
        title_right = (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_middle.setText("我的售卖书籍");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyBuyBookAty.this, BuyBookDetailActivity.class);
                intent.putExtra("BuyBook", list.get(position - 1));
                startActivity(intent);
                finish();
            }
        });
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(0);
            }
        });
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData(curPage + 1);
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
        requestParams.put("BuyBookId", list.get(position-1).getId());
        HttpUtil.getInstance(this).get(this, UrlConstant.DeleteBuyBook, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1", "删除数据buyBook后返回的参数是:" + response.toString());
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
                message.what = -1;
                message.obj = "删除失败" + responseString;
                handler.sendMessage(message);
            }
        });
    }

    private void loadData(final int page) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("page", page);
        requestParams.put("pageSize", 10);


        HttpUtil.getInstance(this).get(this, UrlConstant.MyBuyBookUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("mybuybook1", "MyBuyBook数据加载的结果是:" + response.toString());
                try {
                    String result = (String) response.get("result");
                    Message message = new Message();
                    if (result.equals("success")) {
                        message.what = 200;
                        curPage = page;
                        JSONArray resultSet = (JSONArray) response.get("resultSet");

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
                            JSONObject json11 = (JSONObject) json1.get("userinfo");

                            BuyBook buyBook = new Gson().fromJson(json1.toString(), BuyBook.class);
                            JSONObject json2 = (JSONObject) resultSet.get(i + 1);
                            String UserName = json2.getString("BuyBookUser");
                            String UserSex = json2.getString("BuyBookUserSex");
                            buyBook.setBuyBookUser(UserName);
                            buyBook.setBuyBookUserSex(UserSex);

                            Log.i("mybuybook1","MyBuyBook界面最终获得的子数据为:"+buyBook.toString());

                            list.add(buyBook);
                        }
                        message.what = 200;
                        handler.sendMessage(message);
                    } else {
                        message.obj = result;
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
                Log.i("mybuybook1", "进行BuyFragment数据加载失败:原因是" + responseString.toString());
                Message message = new Message();
                message.obj = responseString;
                message.what = -1;
                handleMessage(message);
            }
        });
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 200) {
                adapter.notifyDataSetChanged();
                listView.onRefreshComplete();
            } else if (msg.what == 300) {
                Toast.makeText(MyBuyBookAty.this,"删除成功",Toast.LENGTH_SHORT).show();
                list.remove(msg.arg1-1);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MyBuyBookAty.this, "错误信息为:" + msg.obj, Toast.LENGTH_SHORT).show();
            }

            return false;
        }
    });

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
