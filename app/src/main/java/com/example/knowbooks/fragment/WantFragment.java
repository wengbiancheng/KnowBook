package com.example.knowbooks.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.knowbooks.BaseFragment;
import com.example.knowbooks.activity.DetailActivity.BuyBookDetailActivity;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.BuyBook;
import com.example.knowbooks.entity.response.WantBook;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.DetailActivity.WantDetailActivity;
import com.example.knowbooks.adapter.WantFragmentAdapter;
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
 * Created by qq on 2016/4/19.
 */
public class WantFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener{


    private RadioGroup rg;
    private RadioButton RBtn_all, RBtn_magazine, RBtn_novel, RBtn_other, RBtn_textBook;

    private PullToRefreshListView listView;
    private WantFragmentAdapter adapter;

    private WantBook wantBook=new WantBook();
    private List<WantBook> list=new ArrayList<>();

    private View footView;
    private int curPage = 0;

    private String WantBookType="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_want, null);

        rg = (RadioGroup) view.findViewById(R.id.fragment_want_rg);
        RBtn_all = (RadioButton) view.findViewById(R.id.fragment_want_rbtn_all);
        RBtn_magazine = (RadioButton) view.findViewById(R.id.fragment_want_rbtn_magazine);
        RBtn_novel = (RadioButton) view.findViewById(R.id.fragment_want_rbtn_novel);
        RBtn_other = (RadioButton) view.findViewById(R.id.fragment_want_rbtn_other);
        RBtn_textBook = (RadioButton) view.findViewById(R.id.fragment_want_rbtn_textbook);
        rg.setOnCheckedChangeListener(this);

        listView = (PullToRefreshListView) view.findViewById(R.id.fragment_want_listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Baseactivity, WantDetailActivity.class);
                Baseactivity.startActivity(intent);
                Baseactivity.finish();
            }
        });
        adapter = new WantFragmentAdapter(Baseactivity, list);
        listView.setAdapter(adapter);

        footView = View.inflate(Baseactivity, R.layout.footview_loading, null);

        initListener();
        loadData(0);
        return view;
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Baseactivity, WantDetailActivity.class);
                intent.putExtra("WantBook", list.get(position-1));
                startActivity(intent);
                Baseactivity.finish();
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
        AlertDialog alertDialog = new AlertDialog.Builder(Baseactivity).setMessage("您真的要删除这条信息吗?").setPositiveButton("取消",
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
        requestParams.put("id", list.get(position).getId());
        HttpUtil.getInstance(Baseactivity).post(Baseactivity, UrlConstant.DetailWantUrl, requestParams, new JsonHttpResponseHandler() {
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
        String url=UrlConstant.FragmentWantUrl;

        if(!TextUtils.isEmpty(WantBookType)){
            requestParams.put("type",WantBookType);
            url=UrlConstant.FragmentWantSomeUrl;
        }

        HttpUtil.getInstance(Baseactivity).get(Baseactivity, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1", "WantFrgment界面加载的数据为:" + response.toString());
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
                            Log.i("Log1", "WantFragment加载后的数据为:" + wantBook.toString());
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
                list.remove(msg.arg1);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(Baseactivity, "错误信息为:" + msg.obj, Toast.LENGTH_SHORT).show();
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.fragment_want_rbtn_all:
                WantBookType="";
                loadData(0);
                break;
            case R.id.fragment_want_rbtn_magazine:
                WantBookType=RBtn_magazine.getText().toString();
                loadData(0);
                break;
            case R.id.fragment_want_rbtn_novel:
                WantBookType=RBtn_novel.getText().toString();
                loadData(0);
                break;
            case R.id.fragment_want_rbtn_other:
                WantBookType=RBtn_other.getText().toString();
                loadData(0);
                break;
            case R.id.fragment_want_rbtn_textbook:
                WantBookType=RBtn_textBook.getText().toString();
                loadData(0);
                break;
            default:
                break;
        }
    }
}
