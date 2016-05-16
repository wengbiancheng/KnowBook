package com.example.knowbooks.activity.DetailActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.activity.LoginActivity;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.DetailComment;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.WriteActivity.WriteSonCommentAty;
import com.example.knowbooks.adapter.DetailCommentAdapter;
import com.example.knowbooks.entity.response.SonComment;
import com.example.knowbooks.utils.DateUtils;
import com.example.knowbooks.utils.HttpUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq on 2016/4/22.
 */
public class CommentDetailActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    //标题栏
    private ImageButton titleLeft;
    private TextView titleMiddle;
    private Button titleRight;

    //listView的headerView部分
    private View view;
    private ImageView top_image;
    private TextView top_name;
    private TextView top_bookName;
    private RatingBar score;
    private TextView time;
    private TextView content;
    private LinearLayout rg;
    private RadioGroup radioGroup;
    private RadioButton leftRadioButton;
    private RadioButton rightRadioButton;
    //子评论部分
    private PullToRefreshListView listView;
    //listView的footView部分
    private View footView;

    //滑动评论时才出现的RadioGroup
    private LinearLayout rg1;
    private RadioGroup radioGroup1;
    private RadioButton leftRadioButton1;
    private RadioButton rightRadioButton1;

    private int RadioFlag = 0;

    private DetailCommentAdapter adapter;
    private List<SonComment> list = new ArrayList<>();

    //Intent传过来的数据
    private String bookName;
    private DetailComment detailComment;
    private Boolean scroll2Comment;

    //图片加载
    private ImageLoader imageLoader;

    private int curPage;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_comment);

        bundle = this.getIntent().getExtras();
        detailComment = bundle.getParcelable("Comment");
        bookName = bundle.getString("bookName");
        scroll2Comment = getIntent().getBooleanExtra("scroll2Comment", false);

        initView();
        initListener();

        setData();//对评论上面的tab部分进行赋值
        loadSonComment(0);
    }

    private void initView() {
        //标题栏的初始化
        titleLeft = (ImageButton) findViewById(R.id.title_leftImageBtn);
        titleMiddle = (TextView) findViewById(R.id.title_middleTextView);
        titleRight = (Button) findViewById(R.id.title_rightBtn);
        titleMiddle.setText("评论详情");
        titleRight.setText("写评论");
        titleLeft.setOnClickListener(this);
        titleRight.setOnClickListener(this);

        view = View.inflate(this, R.layout.detail_comment_tab, null);
        top_image = (ImageView) view.findViewById(R.id.detail_comment_main_image);
        top_name = (TextView) view.findViewById(R.id.detail_comment_userName);
        top_bookName = (TextView) view.findViewById(R.id.detail_comment_bookName);
        score = (RatingBar) view.findViewById(R.id.detail_comment_Score);

        LayerDrawable starts = (LayerDrawable) score.getProgressDrawable();
        starts.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        time = (TextView) view.findViewById(R.id.detail_comment_time);
        content = (TextView) view.findViewById(R.id.detail_comment_content);
        rg = (LinearLayout) view.findViewById(R.id.detail_comment_linear);
        radioGroup = (RadioGroup) view.findViewById(R.id.detail_comment_rg);
        leftRadioButton = (RadioButton) view.findViewById(R.id.detail_comment_LeftRadioBtn);
        rightRadioButton = (RadioButton) view.findViewById(R.id.detail_comment_RightRadioBtn);
        radioGroup.setOnCheckedChangeListener(this);


        rg1 = (LinearLayout) findViewById(R.id.detail_comment_linear1);
        radioGroup1 = (RadioGroup) findViewById(R.id.detail_comment_rg1);
        leftRadioButton1 = (RadioButton) findViewById(R.id.detail_comment_LeftRadioBtn1);
        rightRadioButton1 = (RadioButton) findViewById(R.id.detail_comment_RightRadioBtn1);
        listView = (PullToRefreshListView) findViewById(R.id.detail_comment_sonCommentListView);
        radioGroup1.setOnCheckedChangeListener(this);

        footView = View.inflate(this, R.layout.footview_loading, null);

        final ListView lv = listView.getRefreshableView();
        lv.addHeaderView(view);


        adapter = new DetailCommentAdapter(this, list, handler);
        listView.setAdapter(adapter);

        imageLoader = ImageLoader.getInstance();
    }

    private void initListener() {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                rg1.setVisibility(firstVisibleItem >= 2 ? View.VISIBLE : View.GONE);
            }
        });
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadSonComment(0);
            }
        });
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadSonComment(curPage + 1);
                Log.i("CommentDetailAty1", "OnLastItemVisibleListener");
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
        requestParams.put("sonCommentId", list.get(position).getId());
        Log.i("del","要删除的子评论是："+list.get(position).getCommentContent());
        HttpUtil.getInstance(this).get(this, UrlConstant.SonCommentDel, requestParams, new JsonHttpResponseHandler() {
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


    private void setData() {
//        imageLoader.displayImage(UrlConstant.url+detailComment.getHeadPicture(),top_image);
        top_name.setText(detailComment.getCommentUser());
        top_bookName.setText(bookName);

        score.setRating(Float.parseFloat(String.valueOf(detailComment.getCommentScore())));
        time.setText(DateUtils.getShortTime(detailComment.getCreateDate()));
        content.setText(detailComment.getCommentContent());

        leftRadioButton.setText(detailComment.getNumOfLike() + "");
        rightRadioButton.setText(detailComment.getNumOfDislike() + "");
        leftRadioButton1.setText(detailComment.getNumOfLike() + "");
        rightRadioButton1.setText(detailComment.getNumOfDislike() + "");

        Log.i("CommentDetailAty1", detailComment.toString());
    }

    /**
     * @param Requestpage
     */
    private void loadSonComment(final int Requestpage) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("commentId", detailComment.getId());
        requestParams.put("page", Requestpage);

        HttpUtil.getInstance(this).get(this, UrlConstant.DetailshowbookCommentUrl, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("CommentDetailAty1", response.toString());
                String result = null;
                JSONArray jsonArray = new JSONArray();
                Message message = new Message();
                try {
                    result = (String) response.get("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (result.equals("success")) {
                    message.what = 200;
                    try {
                        jsonArray = (JSONArray) response.get("resultSet");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (Requestpage == 0) {
                        list.clear();
                    }
                    curPage = Requestpage;

                    for (int i = 0; i < jsonArray.length(); i = i + 2) {
                        SonComment sonComment = new SonComment();
                        try {
                            JSONObject json1 = (JSONObject) jsonArray.get(i);
                            JSONObject json2 = (JSONObject) jsonArray.get(i + 1);
                            sonComment.setCommentContent((String) json1.get("commentContent"));
                            sonComment.setCreateDate(json1.getLong("createDate"));
                            sonComment.setId(json1.getLong("id"));
                            sonComment.setSonCommentUserName(json2.getString("sonCommentUser"));
                            sonComment.setSosnCommentUserPicture(json2.getString("headPicture"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list.add(sonComment);
                    }
                    handler.sendMessage(message);
                }
                if (scroll2Comment) {
                    listView.getRefreshableView().setSelection(2);
                    scroll2Comment = false;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Message message = new Message();
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
            } else if (msg.what == -1) {
                Toast.makeText(CommentDetailActivity.this,"操作失败",Toast.LENGTH_SHORT).show();
            } else if (msg.what == -100) {
                SendToDelete(msg.arg1);
            }else if(msg.what==300){
                list.remove(msg.arg1);
                adapter.notifyDataSetChanged();
                Toast.makeText(CommentDetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                if (leftRadioButton.isChecked()) {
                    SendToServlet(1);
                } else if (rightRadioButton.isChecked()) {
                    SendToServlet(2);
                }else{
                    CommentDetailActivity.this.finish();
                }
                break;
            case R.id.title_rightBtn:
                Intent intent1 = new Intent(CommentDetailActivity.this, WriteSonCommentAty.class);
                intent1.putExtras(bundle);
                startActivity(intent1);
                CommentDetailActivity.this.finish();
                break;
        }
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 更新tab菜单栏某个选项时,注意header的菜单栏和shadow菜单栏的选中状态同步
        switch (checkedId) {
            case R.id.detail_comment_LeftRadioBtn:
                if (!leftRadioButton1.isChecked()) {
                    leftRadioButton.setChecked(true);
                    leftRadioButton1.setChecked(true);
                    String text = leftRadioButton.getText().toString();
                    int textInt = Integer.parseInt(text);
                    textInt++;
                    Log.i("Log1", "leftRadioButton is:" + text + "," + textInt);
                    leftRadioButton.setText(textInt + "");
                    leftRadioButton1.setText(textInt + "");
                    if (RadioFlag == 1) {
                        String text2 = rightRadioButton.getText().toString();
                        int textInt2 = Integer.parseInt(text2);
                        textInt2--;
                        rightRadioButton.setText(textInt2 + "");
                        rightRadioButton1.setText(textInt2 + "");
                    }
                    RadioFlag = 1;
//                SendToServlet(1,null);
                }
                break;
            case R.id.detail_comment_RightRadioBtn:
                if (!rightRadioButton1.isChecked()) {
                    rightRadioButton.setChecked(true);
                    rightRadioButton1.setChecked(true);
                    String text1 = rightRadioButton.getText().toString();
                    int textInt1 = Integer.parseInt(text1);
                    textInt1++;
                    Log.i("Log1", "rightRadioButton is:" + text1 + "," + textInt1);
                    rightRadioButton.setText(textInt1 + "");
                    rightRadioButton1.setText(textInt1 + "");
                    if (RadioFlag == 1) {
                        String text2 = leftRadioButton.getText().toString();
                        int textInt2 = Integer.parseInt(text2);
                        textInt2--;
                        leftRadioButton.setText(textInt2 + "");
                        leftRadioButton1.setText(textInt2 + "");
                    }
                    RadioFlag = 1;
//                SendToServlet(2,null);
                }
                break;
            case R.id.detail_comment_LeftRadioBtn1:
                if (!leftRadioButton.isChecked()) {
                    leftRadioButton1.setChecked(true);
                    leftRadioButton.setChecked(true);
                    String text3 = leftRadioButton.getText().toString();
                    int textInt3 = Integer.parseInt(text3);
                    textInt3++;
                    Log.i("Log1", "leftRadioButton1 ---is:" + text3 + "," + textInt3);
                    leftRadioButton.setText(textInt3 + "");
                    leftRadioButton1.setText(textInt3 + "");
                    if (RadioFlag == 1) {
                        String text2 = rightRadioButton.getText().toString();
                        int textInt2 = Integer.parseInt(text2);
                        textInt2--;
                        rightRadioButton.setText(textInt2 + "");
                        rightRadioButton1.setText(textInt2 + "");
                    }
                    RadioFlag = 1;
//                SendToServlet(1,null);
                }
                break;
            case R.id.detail_comment_RightRadioBtn1:
                if (!rightRadioButton.isChecked()) {
                    rightRadioButton1.setChecked(true);
                    rightRadioButton.setChecked(true);
                    String text4 = rightRadioButton.getText().toString();
                    int textInt4 = Integer.parseInt(text4);
                    textInt4++;
                    Log.i("Log1", "rightRadioButton1 ---is:" + text4 + "," + textInt4 + "left is:" + leftRadioButton.getText().toString());

                    rightRadioButton.setText(textInt4 + "");
                    rightRadioButton1.setText(textInt4 + "");
                    if (RadioFlag == 1) {
                        String text2 = leftRadioButton.getText().toString();
                        int textInt2 = Integer.parseInt(text2);
                        textInt2--;
                        leftRadioButton.setText(textInt2 + "");
                        leftRadioButton1.setText(textInt2 + "");
                    }
                    RadioFlag = 1;
//                SendToServlet(2,null);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 进行点赞，踩和删除子评论的操作
     * @param flag
     *
     */
    private void SendToServlet(final int flag) {
        RequestParams requestParams = new RequestParams();
        if (flag == 1) {
            requestParams.put("likeOrDisLike", true);
        } else if (flag == 2) {
            requestParams.put("likeOrDisLike", false);
        }
        requestParams.put("commentId", detailComment.getId());
        HttpUtil.getInstance(CommentDetailActivity.this).get(CommentDetailActivity.this, UrlConstant.DetailCommentClickOneUrl, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1", "进行点赞和踩操作的结果是:" + response.toString());
                try {
                    String result = (String) response.get("result");
                    if (!result.equals("success")) {
                        Toast.makeText(CommentDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        if (flag == 1) {
                            leftRadioButton.setChecked(false);
                            leftRadioButton1.setChecked(false);
                            CommentDetailActivity.this.finish();
                        } else if (flag == 2) {
                            rightRadioButton.setChecked(false);
                            rightRadioButton1.setChecked(false);
                            CommentDetailActivity.this.finish();
                        }
                    } else {
                        Toast.makeText(CommentDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                        CommentDetailActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("Log1", "进行点赞和踩操作失败:" + responseString.toString());
                Toast.makeText(CommentDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                if (flag == 1) {
                    leftRadioButton.setChecked(false);
                    leftRadioButton1.setChecked(false);
                    CommentDetailActivity.this.finish();

                } else if (flag == 2) {
                    rightRadioButton.setChecked(false);
                    rightRadioButton1.setChecked(false);
                    CommentDetailActivity.this.finish();
                }
            }
        });
    }

}
