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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.Book;
import com.example.knowbooks.entity.response.DetailComment;
import com.example.knowbooks.entity.response.ShowBookDetail;
import com.example.knowbooks.entity.response.ShowDetailSimpleComment;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.WriteActivity.WriteBookCommentAty;
import com.example.knowbooks.adapter.ShowDetailCommentAdapter;
import com.example.knowbooks.utils.HttpUtil;
import com.google.gson.Gson;
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
import java.util.Date;
import java.util.List;

/**
 * Created by qq on 2016/4/20.
 */
public class BookDetailActivity extends Activity implements View.OnClickListener {

    private PullToRefreshListView listView;
    private TextView BookName;
    private RatingBar RB_Score;
    private TextView BookScore;
    private TextView CommentCount;
    private ImageView BookImage;
    private TextView BookAuthor;
    private TextView BookType;
    private TextView SimpleIntroduct;
    private TextView Reason;
    private ImageButton two_Image_up;
    private ImageButton two_Image_down;
    private ImageButton three_Image_up;
    private ImageButton three_Image_down;
    private GridView GvImageSection;
    private ImageView ImageSection;
    private List<DetailComment> list=new ArrayList<>();

    private View tab;

    private ShowDetailCommentAdapter adapter;
    //从服务器下载的数据放在ShowBookDetail中，包括List<评论类>
    private ShowBookDetail showDetail;

    private View footView;


    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    private Book book;
    private boolean scroll2Comment;

    //评论当前加载的页数
    private int curPage = 0;

    private ImageLoader imageLoader;

    private Long bookid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_book);
        //获取Intent传入的数据
//        book = getIntent().getParcelableExtra("book");
//        Log.i("BOOKDETAILATY","getIntent:book:"+book.toString());
        scroll2Comment = getIntent().getBooleanExtra("scroll2Comment", false);
        bookid=getIntent().getLongExtra("id", 0);
        imageLoader=ImageLoader.getInstance();
        initView();
        loadDate();;//加载第一页的评论
        initListener();

        adapter = new ShowDetailCommentAdapter(BookDetailActivity.this, list);
        listView.setAdapter(adapter);

    }


    private void initView() {
        tab = View.inflate(this, R.layout.detail_tab, null);
        //第一部分
        BookName = (TextView) tab.findViewById(R.id.detail_show_one_bookName);

        RB_Score = (RatingBar) tab.findViewById(R.id.detail_show_one_RBScore);
        LayerDrawable starts = (LayerDrawable) RB_Score.getProgressDrawable();
        starts.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        BookScore = (TextView) tab.findViewById(R.id.detail_show_one_bookScore);
        CommentCount = (TextView) tab.findViewById(R.id.detail_show_one_bookCommentCount);
        BookImage = (ImageView) tab.findViewById(R.id.detail_show_one_rightImage);
        BookAuthor = (TextView) tab.findViewById(R.id.detail_show_one_bookAuthor);
        BookType = (TextView) tab.findViewById(R.id.detail_show_one_bookType);

        //第二部分
        SimpleIntroduct = (TextView) tab.findViewById(R.id.detail_show_two_content);
        two_Image_down = (ImageButton) tab.findViewById(R.id.detail_show_two_content_downIcon);
        two_Image_up = (ImageButton) tab.findViewById(R.id.detail_show_two_content_upIcon);

        //第三部分
        Reason = (TextView) tab.findViewById(R.id.detail_show_three_content);
        three_Image_down = (ImageButton) tab.findViewById(R.id.detail_show_three_bottom_downIcon);
        three_Image_up = (ImageButton) tab.findViewById(R.id.detail_show_three_upIcon);

        //第四部分
        GvImageSection = (GridView) tab.findViewById(R.id.gv_images);
        ImageSection = (ImageView) tab.findViewById(R.id.iv_image);


        //第五部分
        listView = (PullToRefreshListView) findViewById(R.id.detail_show_five_listView);
        //加载更多的控件
        footView = View.inflate(this, R.layout.footview_loading, null);

        final ListView lv = listView.getRefreshableView();
        lv.addHeaderView(tab);
    }

    @Override
    protected void onRestart() {
        loadDate();
        super.onRestart();
    }


    /**
     * 设置评论上面的部分
     * @param book
     */
    private void setData(Book book) {
        //第一部分
        BookName.setText(book.getBookName());
        if(book.getBookScore()==0){
            RB_Score.setNumStars(0);
        }else{
            RB_Score.setRating(Float.parseFloat(String.valueOf(book.getBookScore())));
        }
        BookScore.setText(book.getBookScore()+"");
        CommentCount.setText(book.getNumOfComments() + "");
        BookAuthor.setText(book.getBookAuthor() + " 著");
        BookType.setText(book.getBookClass());

        imageLoader.displayImage(UrlConstant.url + book.getTitleImage(), BookImage);
        //第二部分
        SimpleIntroduct.setText(book.getBookSummary());

        //第三部分
        Reason.setText(book.getRecommenReason());
    }

    //与服务器连接获得数据
    private void loadDate() {
        //与服务器连接获得数据

        RequestParams requestParams = new RequestParams();
        requestParams.put("id", bookid);
        HttpUtil.getInstance(BookDetailActivity.this).get(BookDetailActivity.this, UrlConstant.BookDetailShowUrl, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1", "详情界面book的初始数据:"+response.toString());

                JSONArray jsonArray = new JSONArray();
                try {
                    String result = (String) response.get("result");
                    if (result.equals("success")) {

                        jsonArray = response.getJSONArray("resultSet");

                        list.clear();
                        curPage=0;


                        JSONObject json11 = (JSONObject) jsonArray.get(0);
                        JSONObject json22 = (JSONObject) jsonArray.get(1);


                        book = new Gson().fromJson(json11.toString(), Book.class);
                        book.setNumOfComments((int) json22.get("commentCount"));

                        Log.i("Log1", "bookDetail获得的书本数据是:" + book.toString());

                        for (int i = 2; i < jsonArray.length(); i = i + 2) {
                            JSONObject json1 = (JSONObject) jsonArray.get(i);
                            JSONObject json2 = (JSONObject) jsonArray.get(i + 1);
                            Gson gson = new Gson();
                            DetailComment detailComment = gson.fromJson(json1.toString(), DetailComment.class);
                            detailComment.setCommentUser((String) json2.get("commentUser"));
                            detailComment.setSonCommentCount((Integer) json2.get("sonCommentCount"));
                            detailComment.setHeadPicture((String) json2.get("headPicture"));
                            list.add(detailComment);
                            Log.i("Log1", "bookDetail获得的初始评论是:" + detailComment.toString());
                        }
                        Message message = new Message();
                        message.what = 200;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (scroll2Comment) {
                    listView.getRefreshableView().setSelection(2);
                    scroll2Comment = false;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("Log1", "详情界面book的初始数据失败:" + responseString);
            }
        });
    }

    //初始化评论列表的刷新操作
    private void initListener() {
        title_left = (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle = (TextView) findViewById(R.id.title_middleTextView);
        title_right = (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_middle.setText("推荐书籍详情界面");
        title_right.setText("写评论");

        two_Image_down.setOnClickListener(this);
        two_Image_up.setOnClickListener(this);
        three_Image_down.setOnClickListener(this);
        three_Image_up.setOnClickListener(this);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadDate();
            }
        });
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
              loadComment(curPage);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BookDetailActivity.this, CommentDetailActivity.class);
                DetailComment detailComment = list.get(position - 2);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Comment", detailComment);
                bundle.putString("bookName", book.getBookName());
                Log.i("BOOKDETAILATY", "setIntent:detailComment:" + detailComment.toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
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
        requestParams.put("commentId", list.get(position-2).getId());
        Log.i("del","要删除的评论的名字是:"+list.get(position-2).getCommentContent());
        HttpUtil.getInstance(this).get(this, UrlConstant.CommentDel, requestParams, new JsonHttpResponseHandler() {
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

            if (msg.what == 200) {
                setData(book);
                adapter.notifyDataSetChanged();
                listView.onRefreshComplete();
                if (list.size() < book.getNumOfComments()) {
                    addFootView(listView, footView);
                } else {
                    removeFootView(listView, footView);
                }

                if (scroll2Comment) {
                    listView.getRefreshableView().setSelection(2);
                    scroll2Comment = false;
                }
            }else if (msg.what == 300) {
                list.remove(msg.arg1-2);
                adapter.notifyDataSetChanged();
                Toast.makeText(BookDetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
            }else if(msg.what==-2){
                Toast.makeText(BookDetailActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
            }
            //一系列的set操作
            return true;
        }
    });


    //加载更多评论的时候调用
    private void loadComment(final int page) {

        curPage++;
        RequestParams requestParams = new RequestParams();
        requestParams.put("id", bookid);
        requestParams.put("page",page);
        Log.i("Log1", "BookDetailAty加载更多评论开始");
        HttpUtil.getInstance(BookDetailActivity.this).get(BookDetailActivity.this, UrlConstant.ShowbookCommentUrl, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1", "BookDetailAty加载更多评论传来的数据时:"+response.toString());

                try {
                    String result = (String) response.get("result");
                    if (result.equals("success")) {

                        JSONArray jsonArray = (JSONArray) response.get("resultSet");
                        curPage=page;
                        if(curPage==0){
                            list.clear();
                        }

                        for (int i = 0; i < jsonArray.length(); i = i+2) {
                            JSONObject json1 = (JSONObject) jsonArray.get(i);
                            JSONObject json2 = (JSONObject) jsonArray.get(i + 1);
                            Gson gson = new Gson();
                            DetailComment detailComment = gson.fromJson(json1.toString(), DetailComment.class);
                            detailComment.setCommentUser((String) json2.get("commentUser"));
                            detailComment.setSonCommentCount((Integer) json2.get("sonCommentCount"));
                            detailComment.setHeadPicture((String) json2.get("headPicture"));
                            list.add(detailComment);
                        }
                        Message message = new Message();
                        message.what = 200;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("Log1", "加载更多评论传来的数据时失败的原因:" + responseString);
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
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                if (!TextUtils.isEmpty(BookDetailActivity.this.getIntent().getStringExtra("OldActivity"))) {
                    String flag = BookDetailActivity.this.getIntent().getStringExtra("OldActivity");
                    if (flag.equals("BookListDetail")) {
                        BookDetailActivity.this.finish();
                    }
                } else {
                    Intent intent = new Intent(BookDetailActivity.this, BookActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.title_rightBtn:
                Intent intent1 = new Intent(BookDetailActivity.this, WriteBookCommentAty.class);
                intent1.putExtra("book",book);
                startActivity(intent1);
                break;
            case R.id.detail_show_two_content_upIcon:
                two_Image_down.setVisibility(View.VISIBLE);
                two_Image_up.setVisibility(View.GONE);
                SimpleIntroduct.setMaxLines(3);
                break;
            case R.id.detail_show_two_content_downIcon:
                two_Image_down.setVisibility(View.GONE);
                two_Image_up.setVisibility(View.VISIBLE);
                SimpleIntroduct.setMaxLines(100);
                break;
            case R.id.detail_show_three_upIcon:
                three_Image_up.setVisibility(View.GONE);
                three_Image_down.setVisibility(View.VISIBLE);
                Reason.setMaxLines(3);
                break;
            case R.id.detail_show_three_bottom_downIcon:
                three_Image_up.setVisibility(View.VISIBLE);
                three_Image_down.setVisibility(View.GONE);
                Reason.setMaxLines(100);
                break;
        }
    }
}
