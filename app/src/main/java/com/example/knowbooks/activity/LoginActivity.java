package com.example.knowbooks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.BaseApplication;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.R;
import com.example.knowbooks.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qq on 2016/4/19.
 */
public class LoginActivity extends Activity implements View.OnClickListener{

    private EditText PhoneNumber;
    private EditText PassWord;
    private Button btn_login;
    //忘记密码控件
    private Button btn_ToRegister;

    //标题栏控件
    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initView();
    }


    private void initView(){
        PhoneNumber= (EditText) findViewById(R.id.login_phonenumber);
        PassWord= (EditText) findViewById(R.id.login_password);
        btn_login= (Button) findViewById(R.id.login_btn);
        btn_ToRegister= (Button) findViewById(R.id.login_ToRegister);
        title_left= (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle= (TextView) findViewById(R.id.title_middleTextView);
        title_right= (Button) findViewById(R.id.title_rightBtn);
        title_middle.setText("使用知书账号登陆");
        title_right.setText("注册");
        initListener();
    }
    private void initListener(){
        btn_login.setOnClickListener(this);
        btn_ToRegister.setOnClickListener(this);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                PhoneNumber.setText("18814122731");
                PassWord.setText("123456");
                String phone=PhoneNumber.getText().toString();
                String password=PassWord.getText().toString();
                if(!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(password)){
                    SendToServlet(phone,password);
                }else{
                    Toast.makeText(LoginActivity.this,"不能为空",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.login_ToRegister:
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                intent.putExtra("forget","1");
                startActivity(intent);
                break;
            case R.id.title_leftImageBtn:
                LoginActivity.this.finish();
                break;
            case R.id.title_rightBtn:
                Intent intent2=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent2);
                break;
        }
    }


    /**
     * 与服务器进行登录的连接
     * @param phone
     * @param password
     */
    private void SendToServlet(final String phone, final String password){
        RequestParams requestParams=new RequestParams();
        requestParams.put("phoneNumber", phone);
        requestParams.put("password", password);
        HttpUtil.getInstance(LoginActivity.this).post(LoginActivity.this, UrlConstant.LoginUrl, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Login1", "succeed:" + response.toString());
                try {
                    Message message = new Message();
                    String result = response.getString("result");
                    if (result.equals("success")) {
                        Log.i("Login1", "result:" + result);
                        message.what = 200;
                        handler.sendMessage(message);
                    } else {
                        Log.i("Login1+fail", "result:" + result);
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
                Log.i("Login2", responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
                Message message=new Message();
                message.what=-1;
                handler.sendMessage(message);
            }
        });

    }

    /**
     *
     * 登录类的handler,登录成功或者失败后的判断
     */
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int code=msg.what;
            if(code==200){
                Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                Intent intent1=new Intent(LoginActivity.this,BookActivity.class);
                intent1.putExtra("LoginToBook","1");
                intent1.putExtra("PhoneNumber",PhoneNumber.getText().toString());
                startActivity(intent1);
            }else{
                Toast.makeText(LoginActivity.this,"登陆失败："+msg.obj,Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

}
