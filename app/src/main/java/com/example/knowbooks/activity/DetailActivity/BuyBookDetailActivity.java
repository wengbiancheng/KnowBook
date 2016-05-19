package com.example.knowbooks.activity.DetailActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.example.knowbooks.entity.response.DetailComment;
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
public class BuyBookDetailActivity extends Activity implements View.OnClickListener{

    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    private DetailBuyBook detailBuyBook=new DetailBuyBook();
    private BuyBook buyBook;

    private ImageView bookPicture;
    private TextView bookName,bookAuthor,bookClass,bookLocation,bookPrice,bookTime,bookContent,qq,weixin,phoneNumber,SellType;
    private Button SentBtn;

    private TextView userName;
    private CircleImageView userImage;

    private ImageLoader imageLoader;

    private String PhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_buybook);
        buyBook=getIntent().getParcelableExtra("BuyBook");
        Log.i("Log1","buyBook传过来的值为:"+buyBook.toString());

        //进行PhoneNumber的传输
        if (!TextUtils.isEmpty(getIntent().getStringExtra("PhoneNumber"))) {
            PhoneNumber = getIntent().getStringExtra("PhoneNumber");
        }
        initView();
        SendToServlet();
    }
    private void initView(){
        title_left= (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle= (TextView) findViewById(R.id.title_middleTextView);
        title_right= (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_middle.setText("售卖书籍详情");

        bookPicture= (ImageView) findViewById(R.id.detail_buy_bookpicture);
        bookName= (TextView) findViewById(R.id.detail_buy_bookname);
        bookAuthor= (TextView) findViewById(R.id.detail_buy_bookAuthor);
        bookClass= (TextView) findViewById(R.id.detail_buy_type);
        bookLocation= (TextView) findViewById(R.id.detail_buy_location);
        bookPrice= (TextView) findViewById(R.id.detail_buy_bookPrice);
        bookTime= (TextView) findViewById(R.id.detail_buy_time);
        bookContent= (TextView) findViewById(R.id.detail_buy_content);
        qq= (TextView) findViewById(R.id.detail_buy_qq);
        weixin= (TextView) findViewById(R.id.detail_buy_weixin);
        phoneNumber= (TextView) findViewById(R.id.detail_buy_phone);
        SentBtn= (Button) findViewById(R.id.detail_want_btn);
        SentBtn.setOnClickListener(this);

        SellType= (TextView) findViewById(R.id.detail_buy_sellType);
        userImage= (CircleImageView) findViewById(R.id.detail_buy_UserPicture);
        userName= (TextView) findViewById(R.id.detail_buy_userName);

        imageLoader=ImageLoader.getInstance();
    }
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==200){
                Log.i("detailbuy", "书本want详情界面的Url是:" + UrlConstant.url + detailBuyBook.getBook().getBookPicture());
                imageLoader.displayImage(UrlConstant.url + detailBuyBook.getBook().getBookPicture(), bookPicture);
                bookName.setText(detailBuyBook.getBook().getBookName());
                bookAuthor.setText("作者/"+detailBuyBook.getBook().getBookAuthor());
                bookClass.setText("类型/"+detailBuyBook.getBook().getBookClass());

                String locationRange=detailBuyBook.getBook().getLocationRange();
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

                bookPrice.setText("￥ "+detailBuyBook.getBook().getBookPrice());
                bookTime.setText(DateUtils.getShortTime(detailBuyBook.getBook().getCreateDate()));
                bookContent.setText(detailBuyBook.getBookDescript());
                SellType.setText("售卖类型/"+detailBuyBook.getSellingWay());
                qq.setText(detailBuyBook.getQq());
                weixin.setText(detailBuyBook.getWeixin());
                phoneNumber.setText(detailBuyBook.getPhoneNumber());

                imageLoader.displayImage(UrlConstant.url+detailBuyBook.getUserPicture(),userImage);
                userName.setText(detailBuyBook.getBook().getBuyBookUser());
            }
            return false;
        }
    });

    private void SendToServlet(){
        RequestParams requestParams=new RequestParams();
        requestParams.put("BuyBookId",buyBook.getId());
        HttpUtil.getInstance(this).get(this, UrlConstant.DetailBuyUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1", "buyBook详情界面请求的结果是:" + response.toString());

                try {
                    String result = (String) response.get("result");
                    if (result.equals("success")) {

                        JSONArray jsonObject= (JSONArray) response.get("resultSet");
                        JSONObject jsonObject1= (JSONObject) jsonObject.get(0);
                        Log.i("Log1", "buyBook详情界面的数组数据resultSet是:" + jsonObject1.toString());
                        detailBuyBook=new Gson().fromJson(jsonObject1.toString(),DetailBuyBook.class);
                        detailBuyBook.setBook(buyBook);
                        Log.i("Log1", "buyBook详情界面请求的数据为:" + detailBuyBook.toString());
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
                Log.i("Log1", "buyBook详情界面请求失败：" + responseString);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                Intent intent=new Intent(BuyBookDetailActivity.this, BookActivity.class);
                intent.putExtra("onStart","2");
                startActivity(intent);
                BuyBookDetailActivity.this.finish();
                break;
            case R.id.title_rightBtn:
                Intent intent1=new Intent(BuyBookDetailActivity.this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.detail_want_btn://聊天
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startPrivateChat(this,detailBuyBook.getPhoneNumber(),"title");
                break;
        }
    }
}
