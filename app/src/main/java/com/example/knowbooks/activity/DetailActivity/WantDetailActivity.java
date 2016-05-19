package com.example.knowbooks.activity.DetailActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.MainActivity;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.BuyBook;
import com.example.knowbooks.entity.response.DetailBuyBook;
import com.example.knowbooks.entity.response.DetailWantBook;
import com.example.knowbooks.entity.response.WantBook;
import com.example.knowbooks.utils.DateUtils;
import com.example.knowbooks.utils.HttpUtil;
import com.example.knowbooks.widget.CircleImageView;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;

/**
 * Created by qq on 2016/4/25.
 */
public class WantDetailActivity extends Activity implements View.OnClickListener{

    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    private ImageView bookPicture;
    private TextView bookName,bookAuthor,bookClass,bookLocation,bookTime,bookContent,qq,weixin,phoneNumber,bookRepay;
    private Button SentBtn;

    private CircleImageView userImage;
    private TextView userName;

    private ImageLoader imageLoader;

    private DetailWantBook detailWantBook=new DetailWantBook();
    private WantBook wantBook=new WantBook();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_want);
        wantBook=getIntent().getParcelableExtra("WantBook");
        Log.i("Log1","getIntent传过来的wantBook是："+wantBook.toString());
        initView();
        SendToServlet();
    }

    private void initView(){
        title_left= (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle= (TextView) findViewById(R.id.title_middleTextView);
        title_right= (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);

        title_middle.setText("心愿详情");

        bookPicture= (ImageView) findViewById(R.id.detail_want_bookpicture);
        bookName= (TextView) findViewById(R.id.detail_want_bookname);
        bookAuthor= (TextView) findViewById(R.id.detail_want_bookAuthor);
        bookClass= (TextView) findViewById(R.id.detail_want_type);
        bookLocation= (TextView) findViewById(R.id.detail_want_location);
        bookTime= (TextView) findViewById(R.id.detail_want_time);

        bookContent= (TextView) findViewById(R.id.detail_want_content);
        bookRepay= (TextView) findViewById(R.id.detail_want_repay);

        qq= (TextView) findViewById(R.id.detail_want_qq);
        weixin= (TextView) findViewById(R.id.detail_want_weixin);
        phoneNumber= (TextView) findViewById(R.id.detail_want_phoneNumber);
        SentBtn= (Button) findViewById(R.id.detail_want_btn);
        SentBtn.setOnClickListener(this);

        userImage= (CircleImageView) findViewById(R.id.detail_want_userImage);
        userName= (TextView) findViewById(R.id.detail_want_userName);

        imageLoader=ImageLoader.getInstance();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                Intent intent=new Intent(WantDetailActivity.this, BookActivity.class);
                intent.putExtra("onStart","3");
                startActivity(intent);
                WantDetailActivity.this.finish();
                break;
            case R.id.title_rightBtn:
                Intent intent1=new Intent(WantDetailActivity.this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.detail_want_btn://聊天
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startPrivateChat(this,detailWantBook.getPhoneNumber(),"title");
                break;
            default:
                break;
        }
    }
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if(msg.what==200){
                //第一部分
                imageLoader.displayImage(UrlConstant.url+wantBook.getWantBookPicture(),bookPicture);
                bookName.setText(wantBook.getWantBookName());
                bookAuthor.setText("作者/"+wantBook.getWantBookAuthor());
                bookClass.setText("类型/"+wantBook.getBookClass());

                String locationRange=wantBook.getLocationRange();
                if(locationRange.equals("8")){
                    bookLocation.setText("距离您约20m内");
                }else if(locationRange.equals("7")){
                    bookLocation.setText("距离您约80m内");
                }else if(locationRange.equals("6")){
                    bookLocation.setText("距离您约610m内");
                }else if(locationRange.equals("5")){
                    bookLocation.setText("距离您约2.4km内");
                }else if(locationRange.equals("4")){
                    bookLocation.setText("距离您约20km内");
                }else if(locationRange.equals("3")){
                    bookLocation.setText("距离您约80km内");
                }else if(locationRange.equals("2")){
                    bookLocation.setText("距离您约630km内");
                }else if(locationRange.equals("1")){
                    bookLocation.setText("距离您约2500km内");
                }

                bookTime.setText(DateUtils.getShortTime(wantBook.getCreateDate()));
                //第三部分
                imageLoader.displayImage(UrlConstant.url+detailWantBook.getUserPicture(),userImage);
                userName.setText(detailWantBook.getUserName());
                qq.setText(detailWantBook.getQq());
                weixin.setText(detailWantBook.getWeixin());
                phoneNumber.setText(detailWantBook.getPhoneNumber());
                //第二部分
                bookContent.setText(wantBook.getWishContent());
                bookRepay.setText(wantBook.getWishPay());


            }
            return false;
        }
    });
    private void SendToServlet(){
        RequestParams requestParams=new RequestParams();
        requestParams.put("WantBookId", wantBook.getId());
        Log.i("Log1", "wantDetail界面请求的心愿的id是:" + wantBook.getId());
        HttpUtil.getInstance(this).get(this, UrlConstant.DetailWantUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1", "wantDetail界面接收到的反应为:" + response.toString());

                try {
                    String result = (String) response.get("result");
                    if (result.equals("success")) {

                        JSONArray jsonObject = (JSONArray) response.get("resultSet");
                        JSONObject json= (JSONObject) jsonObject.get(0);
                        detailWantBook = new Gson().fromJson(json.toString(), DetailWantBook.class);
                        detailWantBook.setWantBook(wantBook);
                        Log.i("Log1","最终的detailWantBook数据为:"+detailWantBook);
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
                Log.i("Log1", "wantDetail界面请求失败的原因是:" + responseString);
            }
        });
    }

}
