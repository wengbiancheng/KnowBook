package com.example.knowbooks.activity.WriteActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.DetailActivity.BookDetailActivity;
import com.example.knowbooks.activity.MainActivity;
import com.example.knowbooks.constants.AccessTokenKeeper;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.User;
import com.example.knowbooks.entity.response.Book;
import com.example.knowbooks.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qq on 2016/4/23.
 */
public class WriteBookCommentAty extends Activity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {


    //标题栏
    private ImageButton titleLeft;
    private TextView titleMiddle;
    private Button titleRight;


    private RatingBar Score;
    private EditText content;
    private Button sendBtn;

    private Long Bookid;
    private String bookScore="0";
    private String CommentContent;
    private String phoneNumber;

    private Book book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);
        initView();
        initListener();

        book = getIntent().getParcelableExtra("book");
        Bookid = book.getId();
    }

    private void initView() {

        //标题栏的初始化
        titleLeft = (ImageButton) findViewById(R.id.title_leftImageBtn);
        titleMiddle = (TextView) findViewById(R.id.title_middleTextView);
        titleRight = (Button) findViewById(R.id.title_rightBtn);

        titleMiddle.setText("写评论");

        Score = (RatingBar) findViewById(R.id.write_comment_score);
        content = (EditText) findViewById(R.id.write_comment_content);
        sendBtn = (Button) findViewById(R.id.write_comment_Sendbtn);

    }

    private void initListener() {
        titleLeft.setOnClickListener(this);
        titleRight.setOnClickListener(this);
        Score.setOnRatingBarChangeListener(this);
        Score.setOnClickListener(this);
        LayerDrawable starts = (LayerDrawable) Score.getProgressDrawable();
        starts.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        Score.setRating(0);
        sendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                WriteBookCommentAty.this.finish();
                break;
            case R.id.title_rightBtn:
                Intent intent1 = new Intent(WriteBookCommentAty.this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.write_comment_Sendbtn:
                SendToServlet();
                break;
        }
    }

    private void SendToServlet() {

        RequestParams requestParams = new RequestParams();
        requestParams.put("bookId", Bookid);
        if (bookScore.equals("0")) {
            Toast.makeText(this, "请进行评分操作", Toast.LENGTH_SHORT).show();
        } else {
            requestParams.put("commentScore", Double.parseDouble(bookScore));
            requestParams.put("commentContent", content.getText().toString());
            User user = AccessTokenKeeper.readAccessDate(this);

            Log.i("WriteBookCommentAty1", "data:" + Bookid + "--" + Double.parseDouble(bookScore) + "--" + content.getText().toString() + "--" + user.getPhoneNumber());

            HttpUtil.getInstance(this).post(this, UrlConstant.WriteshowbookCommentUrl, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.i("WriteBookCommentAty1", response.toString());
                    try {
                        String result = (String) response.get("result");
                        Log.i("WriteBookCommentAty1", "result:" + result);
                        Message message = new Message();
                        Log.i("WriteBookCommentAty1", "response:" + response.toString());
                        if (result.equals("success")) {
                            message.what = 200;
                        } else {
                            message.what = -1;
                            message.obj = result;
                        }
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("WriteBookCommentAty1", "connect fail:" + responseString);
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Message message = new Message();
                    message.what = -1;
                    handler.sendMessage(message);
                }
            });
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 200) {
                Toast.makeText(WriteBookCommentAty.this, "评论发表成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WriteBookCommentAty.this, BookDetailActivity.class);
                intent.putExtra("id", book.getId());
                intent.putExtra("scroll2Comment", true);
                startActivity(intent);
            } else {
                Toast.makeText(WriteBookCommentAty.this, "评论发表失败,原因为:" + msg.obj, Toast.LENGTH_SHORT).show();
                Score.setNumStars(0);
                content.setText("");
            }
            return false;
        }
    });

    //这个函数还不太熟悉,需要查查
    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        bookScore = String.valueOf(rating);
    }
}
