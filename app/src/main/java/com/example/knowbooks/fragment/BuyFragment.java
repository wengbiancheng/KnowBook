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
    private String bookTypeData="";
    private String SellTypeData="";
    private String item1[]={"全部","卖书","换书","借书","送书"};
    private String item2[]={"全部","校园教材","杂志","小说","考研材料","托福材料"};

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
        if (flag.equals("booktype")){
            new AlertDialog.Builder(Baseactivity)
                    .setItems(item2, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            switch (which) {
                                case 0:
                                    bookTypeData = "";
                                    BookType.setText("全部");
                                    loadData(0);
                                    break;
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                    bookTypeData = item2[which];
                                    BookType.setText(bookTypeData);
                                    loadData(0);
                                default:
                                    break;
                            }
                        }
                    })
                    .show();
        }else{
            new AlertDialog.Builder(Baseactivity)
                    .setItems(item1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            switch (which) {
                                case 0:
                                    SellTypeData = "";
                                    SellType.setText("全部");
                                    loadData(0);
                                    break;
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    SellTypeData = item1[which];
                                    SellType.setText(SellTypeData);
                                    loadData(0);
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
        HttpUtil.getInstance(Baseactivity).get(Baseactivity, UrlConstant.DetailWantUrl, requestParams, new JsonHttpResponseHandler() {
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
        String url=UrlConstant.FragmentBuyUrl;
        if(!TextUtils.isEmpty(bookTypeData)||!TextUtils.isEmpty(SellTypeData)){
            requestParams.put("Type",bookTypeData);
            requestParams.put("sellType",SellTypeData);
            url=UrlConstant.FragmentBuySomeUrl;
            Log.i("Log1", "进行buyFragment界面的筛选活动:书本的类型是"+bookTypeData+";售卖的类型是:"+SellTypeData);
        }

        HttpUtil.getInstance(Baseactivity).get(Baseactivity, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1", "进行BuyFragment数据加载的结果是:" + response.toString());
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
                            JSONObject json11 = (JSONObject) json1.get("user_info");

                            BuyBook buyBook = new Gson().fromJson(json1.toString(), BuyBook.class);
                            JSONObject json2 = (JSONObject) resultSet.get(i + 1);
                            String UserName = json2.getString("BuyBookUser");
                            String UserSex = json2.getString("BuyBookUserSex");
                            buyBook.setBuyBookUser(UserName);
                            buyBook.setBuyBookUserSex(UserSex);

                            Log.i("Log1", "进行BuyFragment数据加载的子结果是:" + json1.toString());
                            Log.i("Log1", "进行BuyFragment数据加载的子结果是:" + json2.toString());

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
                Log.i("Log1", "进行BuyFragment数据加载失败:原因是" + responseString.toString());
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
                list.remove(msg.arg1);
                adapter.notifyDataSetChanged();
            }else if(msg.what==201) {
                ShowAlertDialogSome((String) msg.obj);
            }else{Toast.makeText(Baseactivity, "错误信息为:" + msg.obj, Toast.LENGTH_SHORT).show();
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
