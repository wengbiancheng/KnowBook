package com.example.knowbooks.activity.WriteActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.DetailActivity.CommentDetailActivity;
import com.example.knowbooks.activity.MainActivity;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.DetailComment;
import com.example.knowbooks.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qq on 2016/4/23.
 */
public class WriteSonCommentAty extends Activity implements View.OnClickListener{

    //标题栏
    private ImageButton titleLeft;
    private TextView titleMiddle;
    private Button titleRight;


    private EditText content;
    private Button sendBtn;

    private Long commentId;
    private DetailComment detailComment;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_soncomment);
        initView();
        initListener();
        bundle=getIntent().getExtras();
        detailComment=bundle.getParcelable("Comment");
        commentId=detailComment.getId();
    }
    private void initView(){

        //标题栏的初始化
        titleLeft= (ImageButton) findViewById(R.id.title_leftImageBtn);
        titleMiddle= (TextView) findViewById(R.id.title_middleTextView);
        titleRight= (Button) findViewById(R.id.title_rightBtn);
        titleLeft.setOnClickListener(this);
        titleRight.setOnClickListener(this);
        titleMiddle.setText("评论详情");


        content= (EditText) findViewById(R.id.write_soncomment_conent);
        sendBtn= (Button) findViewById(R.id.write_Soncomment_Sendbtn);
        sendBtn.setOnClickListener(this);
    }
    private void initListener(){

    }
    private void SendToServlet(){
        RequestParams requestParams=new RequestParams();
        requestParams.put("commentId",commentId);
        requestParams.put("commentContent",content.getText().toString());

        HttpUtil.getInstance(this).post(this, UrlConstant.WriteshowbookSonCommentUrl,requestParams,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("WriteSonCommentAty1",response.toString());
                try {
                    String result= (String) response.get("result");
                    if(result.equals("success"))
                    {
                        Intent intent=new Intent(WriteSonCommentAty.this, CommentDetailActivity.class);
                        intent.putExtra("scroll2Comment",true);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        WriteSonCommentAty.this.finish();
                    }else{
                        Toast.makeText(WriteSonCommentAty.this,"发送评论失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(WriteSonCommentAty.this,"发送评论失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                WriteSonCommentAty.this.finish();
                break;
            case R.id.title_rightBtn:
                Intent intent1=new Intent(WriteSonCommentAty.this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.write_Soncomment_Sendbtn:
                SendToServlet();
            default:
                break;
        }
    }
}
