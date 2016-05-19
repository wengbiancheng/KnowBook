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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.DetailActivity.WantDetailActivity;
import com.example.knowbooks.adapter.WantFragmentAdapter;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.WantBook;
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
public class MyWantBookAty extends Activity implements View.OnClickListener{



    private PullToRefreshListView listView;
    private WantFragmentAdapter adapter;

    private WantBook wantBook=new WantBook();
    private List<WantBook> list=new ArrayList<>();

    private View footView;
    private int curPage = 0;

    private String WantBookType="";

    //标题栏控件初始化
    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.my_show);

        listView = (PullToRefreshListView)findViewById(R.id.show_listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyWantBookAty.this, WantDetailActivity.class);
                startActivity(intent);
                finish();
            }
        });
        adapter = new WantFragmentAdapter(this, list);
        listView.setAdapter(adapter);

        footView = View.inflate(this, R.layout.footview_loading, null);

        initListener();
        loadData(0);

    }


    private void initListener() {

        title_left = (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle = (TextView) findViewById(R.id.title_middleTextView);
        title_right = (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_middle.setText("我的心愿");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyWantBookAty.this, WantDetailActivity.class);
                intent.putExtra("WantBook", list.get(position-1));
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
        requestParams.put("WantBookId", list.get(position-1).getId());
        HttpUtil.getInstance(this).get(this, UrlConstant.DeleteWantBook, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("mywantbook1", "加载mybooklistAty成功的信息是:" + response.toString());
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
                Log.i("mywantbook1", "加载mywantbookAty失败:" + responseString.toString());
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

        HttpUtil.getInstance(this).get(this, UrlConstant.MyWantBookUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("mywantbook1", "MyWantBook界面的反应为:" + response.toString());
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
                            JSONObject json = (JSONObject) resultSet.get(i);
                            WantBook wantBook = new Gson().fromJson(json.toString(), WantBook.class);

                            JSONObject json1 = (JSONObject) resultSet.get(i + 1);
                            String userSex = json1.getString("UserSex");
                            String userName = json1.getString("UserName");
                            wantBook.setUserSex(userSex);
                            wantBook.setUserName(userName);
                            Log.i("mywantbook1", "MyWantBook界面的获得的数据为:" + wantBook.toString());
                            list.add(wantBook);
                        }
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
                Log.i("mywantbook1", "MyWantBook失败:" + responseString.toString());
                Message message = new Message();
                message.obj = responseString;
                message.what = -1;
                handler.sendMessage(message);
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
                Toast.makeText(MyWantBookAty.this,"删除成功",Toast.LENGTH_SHORT).show();
                list.remove(msg.arg1-1);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MyWantBookAty.this, "错误信息为:" + msg.obj, Toast.LENGTH_SHORT).show();
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
