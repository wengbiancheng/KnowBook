package com.example.knowbooks.fragment;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.knowbooks.BaseApplication;
import com.example.knowbooks.BaseFragment;
import com.example.knowbooks.entity.response.Book;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.DetailActivity.BookDetailActivity;
import com.example.knowbooks.adapter.book_show_adapter;
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
 * Created by qq on 2016/4/19.
 */

public class ShowFragment extends BaseFragment {

    private PullToRefreshListView listView;
    private book_show_adapter adapter;
    private List<Book> list = new ArrayList<>();

    private JSONArray resultSet;

    private View footView;
    private int curPage = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show, null);

        listView = (PullToRefreshListView) view.findViewById(R.id.show_listView);
        adapter = new book_show_adapter(Baseactivity, list, handler);
        listView.setAdapter(adapter);
        footView = View.inflate(Baseactivity, R.layout.footview_loading, null);
        LoadData(0);

        initListener();
        return view;
    }

    private void initListener() {
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
                Intent intent = new Intent(Baseactivity, BookDetailActivity.class);
                intent.putExtra("id", list.get(position - 1).getId());
                startActivity(intent);

                Baseactivity.finish();
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
            } else if (code == 5) {//点击收藏按钮触发的操作
                Log.i("show1", "CollectButton:is click");
                int OldIsCollect = msg.arg1;
                CollectBookid = (Long) msg.obj;//书籍的id
                if (OldIsCollect == 0) {
                    //进行收藏书籍的操作,需要跳转出该书籍所在的全部书籍
                    Log.i("show1", "进行收藏书籍操作");
                    SendToServlet(1, 0);
                } else if (OldIsCollect == 1) {
                    //取消收藏书籍的操作,需要跳转出该书籍所在的全部书籍
                    Log.i("show1", "进行取消收藏书籍操作");
                    SendToServlet(0, 1);
                }
            } else if (code == 300) {
                list.remove(msg.arg1);
                adapter.notifyDataSetChanged();
            } else if (code == 400) {
                if (msg.arg1 == 1) {
                    SelectBookList(msg.arg1);
                }
            } else if (code == 500) {
                LoadData(0);
                if (msg.arg1 == 1) {
                    Toast.makeText(Baseactivity, "收藏该书籍成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Baseactivity, "取消收藏该书籍成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Baseactivity, "错误信息为:" + msg.obj, Toast.LENGTH_SHORT).show();
            }

            return false;
        }
    });

    /**
     * 点击收藏按钮后触发的一系列操作
     *
     * @param NewisCollect
     * @param flag
     */
    private void SendToServlet(final int NewisCollect, int flag) {

        String url = "";
        RequestParams requestParams = new RequestParams();
        //获取相应的书单的操作,当进行收藏书单的操作的时候
        if ((NewisCollect == 1)&&(flag==0)) {
            url = UrlConstant.GetBookListView;

            HttpUtil.getInstance(Baseactivity).get(Baseactivity, url, requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.i("show1", "获取相应的书单的操作的结果是:" + response.toString());
                            //进行list1的赋值操作
                            try {
                                String result = (String) response.get("result");
                                if (result.equals("success")) {
                                    list1.clear();
                                    JSONArray jsonArray = (JSONArray) response.get("resultSet");

                                    for (int i = 0; i < jsonArray.length(); i=i+2) {
                                        JSONObject json = (JSONObject) jsonArray.get(i);

                                        CollectBookList collectBookList = new Gson().fromJson(json.toString(), CollectBookList.class);
                                        list1.add(collectBookList);
                                    }
                                    Message message = new Message();
                                    message.what = 400;
                                    message.arg1 = NewisCollect;
                                    handler.sendMessage(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Log.i("show1", "获取相应的书单的操作的结果是：" + responseString.toString());
                        }
                    }
            );
        } else if (flag == 1) {//进行相应的书单的操作
            if (NewisCollect == 0) //取消收藏书籍的操作
            {
                url = UrlConstant.NoCollectBookUrl;
                requestParams.put("bookId", CollectBookid);
                Log.i("show1", "取消收藏的书籍的id是:" + CollectBookid + ";书单id是:" + CollectbookListId);
            } else if (NewisCollect == 1)//收藏该书籍的操作
            {
                url = UrlConstant.CollectBookUrl;
                requestParams.put("bookId", CollectBookid);
                requestParams.put("booklistId", CollectbookListId);
                Log.i("show1", "要收藏的书籍的id是:" + CollectBookid + ";把书放进去的书单id是:" + CollectbookListId);
            }

            HttpUtil.getInstance(Baseactivity).get(Baseactivity, url, requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.i("show1", "最终的收藏书籍和取消书籍的操作 :" + response.toString());
                            try {
                                String result = (String) response.get("result");
                                if (result.equals("success")) {
                                    Message message = new Message();
                                    message.what = 500;
                                    message.arg1 = NewisCollect;
                                    handler.sendMessage(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Log.i("show1", "最终的收藏书籍和取消书籍的操作失败的原因是:" + responseString.toString());
                        }
                    }
            );
        }
    }

    private List<CollectBookList> list1 = new ArrayList<>();
    private AlertDialog alertDialog;
    private long CollectbookListId = 0;
    private Long CollectBookid;

    private void SelectBookList(final int flag) {
        ListView listView1 = new ListView(Baseactivity);
        List<String> list3 = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            list3.add(list1.get(i).getBookListName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Baseactivity, android.R.layout.simple_expandable_list_item_1, list3);
        listView1.setAdapter(arrayAdapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CollectbookListId = list1.get(position).getId();
                Log.i("show1", "收藏进的书单的名字是:" + list1.get(position).getBookListName() + ",书单的id是:" + list1.get(position).getId());
                SendToServlet(flag, 1);
                alertDialog.dismiss();
            }
        });
        alertDialog = new AlertDialog.Builder(Baseactivity).setTitle("请选择相应的书单").setView(listView1).create();
        alertDialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
        alertDialog.show();
    }

    /**
     * 与服务器进行showfragment数据的传输设置
     *
     * @param page
     */
    private void LoadData(final int page) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("page", page + "");
        HttpUtil.getInstance(getActivity()).get(getActivity(), UrlConstant.FragmentshowUrl, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("show1","loadData加载的数据是:"+response.toString());
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

                            Book book = new Gson().fromJson(json1.toString(), Book.class);
                            book.setIsCollect((int) json2.get("isCollect"));
                            book.setNumOfComments((int) json2.get("numOfComments"));
                            book.setUserName((String) json2.get("userName"));
                            book.setUserSex((String) json2.get("sex"));
                            list.add(book);
                            Log.i("show1", "loadData加载的数据的子集是:" + book.toString());
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
                Log.i("show1","loadData加载失败的原因是:"+responseString.toString());
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
}
