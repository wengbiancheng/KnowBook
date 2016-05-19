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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.knowbooks.BaseFragment;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.BuyBook;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.DetailActivity.BuyBookDetailActivity;
import com.example.knowbooks.adapter.BuyFragmentAdapter;
import com.example.knowbooks.adapter.spinnerAdapter;
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
public class BuyFragment extends BaseFragment {

    private Button BookType;
    private Button SellType;
    private ArrayAdapter<CharSequence> BookTypeAdapter;
    private ArrayAdapter<CharSequence> SellTypeAdapter;


    private PullToRefreshListView listView;
    private BuyFragmentAdapter adapter;

    private List<BuyBook> list = new ArrayList<>();
    private spinnerAdapter sadapter, s2adapter;

    private int curPage = 0;

    private View footView;

    private List<String> listType = new ArrayList<>();
    private List<String> listSellType = new ArrayList<>();
    private String bookTypeData = "";
    private String SellTypeData = "";
    private String item1[] = {"全部", "卖书", "换书", "借书", "送书"};
    private String item2[] = {"全部", "校园教材", "杂志", "小说", "考研材料", "托福材料"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, null);

        BookType = (Button) view.findViewById(R.id.fragment_buy_spinner1);
        SellType = (Button) view.findViewById(R.id.fragment_buy_spinner2);
        BookType.setText("全部");
        SellType.setText("全部");

        BookType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialogSome("booktype");
            }
        });
        SellType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialogSome("selltype");
            }
        });

        listView = (PullToRefreshListView) view.findViewById(R.id.fragment_buy_listView);
        adapter = new BuyFragmentAdapter(Baseactivity, list);
        listView.setAdapter(adapter);

        footView = View.inflate(Baseactivity, R.layout.footview_loading, null);

        loadData(0);
        initListener();

        return view;
    }

    /**
     * 进行购买书籍的选择操作
     */
//    private AlertDialog alert;
    private void ShowAlertDialogSome(final String flag) {
        if (flag.equals("booktype")) {
            new AlertDialog.Builder(Baseactivity)
                    .setItems(item2, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            switch (which) {
                                case 0:
                                    bookTypeData = "";
                                    BookType.setText("全部");
                                    if(!TextUtils.isEmpty(SellTypeData)){
                                        loadDataSome(0);
                                    }else {
                                        loadData(0);
                                    }
                                    break;
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                    bookTypeData = item2[which];
                                    BookType.setText(bookTypeData);
                                    loadDataSome(0);
                                default:
                                    break;
                            }
                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(Baseactivity)
                    .setItems(item1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            switch (which) {
                                case 0:
                                    SellTypeData = "";
                                    SellType.setText("全部");
                                    if(!TextUtils.isEmpty(bookTypeData)){
                                        loadDataSome(0);
                                    }else {
                                        loadData(0);
                                    }
                                    break;
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    SellTypeData = item1[which];
                                    SellType.setText(SellTypeData);
                                    loadDataSome(0);
                                default:
                                    break;
                            }
                        }
                    })
                    .show();
        }

    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Baseactivity, BuyBookDetailActivity.class);
                intent.putExtra("BuyBook", list.get(position - 1));
                intent.putExtra("PhoneNumber",Baseactivity.getPhoneNumber());
                startActivity(intent);
                Baseactivity.finish();
            }
        });
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!TextUtils.isEmpty(bookTypeData) || !TextUtils.isEmpty(SellTypeData)) {
                    loadDataSome(curPage + 1);
                } else {
                    loadData(0);
                }
            }
        });
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (!TextUtils.isEmpty(bookTypeData) || !TextUtils.isEmpty(SellTypeData)) {
                    loadDataSome(curPage + 1);
                } else {
                    loadData(curPage + 1);
                }
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
                list.remove(position-1);
                adapter.notifyDataSetChanged();
            }
        }).create();
        alertDialog.show();
    }

    private void loadDataSome(final int page) {
        Log.i("Log11","进行buyfragment的筛选数据操作");
        RequestParams requestParams = new RequestParams();
        requestParams.put("page", page);
        requestParams.put("pageSize", 10);
        String url = "";
        if (!TextUtils.isEmpty(bookTypeData) || !TextUtils.isEmpty(SellTypeData)) {
            requestParams.put("Type", bookTypeData);
            requestParams.put("sellType", SellTypeData);
            url = UrlConstant.FragmentBuySomeUrl;
            Log.i("Log11", "进行buyFragment界面的筛选活动:书本的类型是" + bookTypeData + ";售卖的类型是:" + SellTypeData);
        }

        HttpUtil.getInstance(Baseactivity).get(Baseactivity, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log11", "进行BuyFragment数据加载的结果是:" + response.toString());
                try {
                    String result = response.get("result").toString();
                    Message message = new Message();
                    Log.i("Log11", "进行BuyFragment数据加载的数组结果是:" + result);
                    if (result.equals("success")) {
                        message.what = 200;
                        curPage = page;
                        JSONArray resultSet = (JSONArray) response.get("resultSet");


                        Log.i("Log11", "进行BuyFragment数据加载的数组结果是:" + resultSet.toString());
                        Log.i("Log11", "进行BuyFragment数据加载的数组的----长度是:" + resultSet.length());
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
                            String locationRange=json2.getString("locationRange");
                            buyBook.setLocationRange(locationRange);
                            buyBook.setBuyBookUser(UserName);
                            buyBook.setBuyBookUserSex(UserSex);

                            Log.i("Log11", "进行BuyFragment数据加载的数据是:" + buyBook.toString());

                            list.add(buyBook);
                        }
                        message.what = 200;
                        handler.sendMessage(message);
                    } else {
                        Log.i("Log11", "loadData(curpage+1)");
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
                Log.i("Log11", "进行BuyFragment数据加载失败:原因是" + responseString.toString());
                Message message = new Message();
                message.obj = responseString;
                message.what = -1;
                handleMessage(message);
            }
        });
    }


    private void loadData(final int page) {
        Log.i("Log11","进行buyfragment的全部数据操作:"+page);
        RequestParams requestParams = new RequestParams();
        int locationRange = 8 - page;
        if (locationRange >= 1) {
            requestParams.put("locationRange", locationRange);
            requestParams.put("page", page);
            requestParams.put("pageSize", 10);
            Log.i("Log11", "locationRange:" + locationRange);
            String url = UrlConstant.FragmentBuyUrl;
//            if (!TextUtils.isEmpty(bookTypeData) || !TextUtils.isEmpty(SellTypeData)) {
//                requestParams.put("Type", bookTypeData);
//                requestParams.put("sellType", SellTypeData);
//                url = UrlConstant.FragmentBuySomeUrl;
//                Log.i("Log11", "进行buyFragment界面的筛选活动:书本的类型是" + bookTypeData + ";售卖的类型是:" + SellTypeData);
//            }

            HttpUtil.getInstance(Baseactivity).get(Baseactivity, url, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.i("Log11", "进行BuyFragment数据加载的结果是:" + response.toString());
                    try {
                        String result = response.get("result").toString();
                        Message message = new Message();
                        Log.i("Log11", "进行BuyFragment数据加载的数组结果是:" + result);
                        if (result.equals("success")) {
                            message.what = 200;
                            curPage = page;
                            JSONArray resultSet = (JSONArray) response.get("resultSet");


                            Log.i("Log11", "进行BuyFragment数据加载的数组结果是:" + resultSet.toString());
                            Log.i("Log11", "进行BuyFragment数据加载的数组的----长度是:" + resultSet.length());
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
                                String locationRange=json2.getString("locationRange");
                                buyBook.setLocationRange(locationRange);
                                buyBook.setBuyBookUser(UserName);
                                buyBook.setBuyBookUserSex(UserSex);

                                Log.i("Log11", "进行BuyFragment数据加载的id是:" + buyBook.getId());

                                list.add(buyBook);
                            }
                            if (list.size() < 10) {
                                loadData(curPage + 1);
                            } else {
                                message.what = 200;
                                handler.sendMessage(message);
                            }
                        } else if (result.equals("nodata")) {
                            Log.i("Log11", "loadData(curpage+1)" + curPage);
                            curPage = page;
                            loadData(curPage + 1);
                        } else {
                            Log.i("Log11", "loadData(curpage+1)");
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
                    Log.i("Log11", "进行BuyFragment数据加载失败:原因是" + responseString.toString());
                    Message message = new Message();
                    message.obj = responseString;
                    message.what = -1;
                    handleMessage(message);
                }
            });
        } else {
            listView.onRefreshComplete();
        }
        removeFootView(listView, footView);
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
            } else if (msg.what == 201) {
                ShowAlertDialogSome((String) msg.obj);
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
}
