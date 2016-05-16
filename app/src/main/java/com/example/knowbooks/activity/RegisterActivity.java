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

import com.example.knowbooks.constants.SMSSDKConstant;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by qq on 2016/4/19.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {

    //初始化标题栏三个控件
    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;
    //初始化其他控件
    private EditText PhoneNumber;
    private EditText PassWord;
    private Button getCode;
    private EditText code;
    private Button send;

    //为点击忘记密码界面的进入做准备
    private String flag_forgetPassWord;

    //获取验证码的按钮计时器操作
    private static int timeNumber = 60;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        //初始化控件
        initView();
        //初始化监听事件
        initListener();
        initSDK();

        //使用注册界面作为忘记密码重置的界面，进行界面的多次利用
        flag_forgetPassWord = getIntent().getStringExtra("forget");
        if (!TextUtils.isEmpty(flag_forgetPassWord)) {
            if (flag_forgetPassWord.equals("1")) {
                title_middle.setText("重置密码");
            }
        }
    }

    private void initView() {
        PhoneNumber = (EditText) findViewById(R.id.register_phoneNumber);
        PhoneNumber.setFocusable(true);
        PassWord = (EditText) findViewById(R.id.register_password);
        getCode = (Button) findViewById(R.id.register_btn);
        send = (Button) findViewById(R.id.register_send);
        send.setOnClickListener(this);
        code = (EditText) findViewById(R.id.register_code);
        title_left = (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle = (TextView) findViewById(R.id.title_middleTextView);
        title_right = (Button) findViewById(R.id.title_rightBtn);
        title_right.setVisibility(View.GONE);

        //标题栏的文本的设置
        title_middle.setText("欢迎注册知书");
    }

    private void initListener() {
        getCode.setOnClickListener(this);
        title_left.setOnClickListener(this);
    }

    /**
     * 初始化短信验证的SDK
     */
    private void initSDK() {
        SMSSDK.initSDK(RegisterActivity.this, SMSSDKConstant.AppKey, SMSSDKConstant.APPSecret);
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }



    /**
     *  初始化定时器的操作
     */
    private void initTimeListener() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timeNumber--;
                Message message = new Message();
                if (timeNumber >= 0) {
                    message.what = -9;
                } else {
                    message.what = -8;
                }
                handler.sendMessage(message);
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                RegisterActivity.this.finish();
                break;
            case R.id.register_btn:
                //获取验证码
                if (!TextUtils.isEmpty(PhoneNumber.getText().toString())) {
                    // 1. 通过规则判断手机号
                    if (!judgePhoneNums(PhoneNumber.getText().toString())) {
                        return;
                    } // 2. 通过sdk发送短信验证
                    SMSSDK.initSDK(RegisterActivity.this, SMSSDKConstant.AppKey, SMSSDKConstant.APPSecret);
                    SMSSDK.getVerificationCode("86", PhoneNumber.getText().toString(), new OnSendMessageHandler() {
                        @Override
                        public boolean onSendMessage(String s, String s1) {
                            return false;
                        }
                    });
                    timeNumber = 60;
                    initTimeListener();
                }
                break;
            case R.id.register_send:
                //进行验证码的校验
                if (!TextUtils.isEmpty(PhoneNumber.getText().toString())
                        && !TextUtils.isEmpty(code.getText().toString())) {
                    SMSSDK.initSDK(RegisterActivity.this, SMSSDKConstant.AppKey, SMSSDKConstant.APPSecret);
                    SMSSDK.submitVerificationCode("86", PhoneNumber.getText().toString(), code.getText().toString());
                }
                break;
        }
    }
    /**
     *  手机验证码的handler类，主要是进行获取验证码按钮的时间监控和验证后的返回操作
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == -9) {
                String TimeNumber = timeNumber + "";
                getCode.setText(TimeNumber + "秒后重新获取验证码");
                getCode.setEnabled(false);
            } else if (msg.what == -8) {
                getCode.setText("获取验证码");
                getCode.setEnabled(true);
                timer.cancel();
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 与服务器连接成功
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功

                        SendToServlet(PhoneNumber.getText().toString(), PassWord.getText().toString());
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送到手机上",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
            return false;
        }
    });

    /**
     *注册类的handler，主要进行注册的操作
     */
    private Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int code = msg.what;
            String result = (String) msg.obj;
            if (code == 200) {
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            } else {
                Toast.makeText(RegisterActivity.this, "错误信息为：" + result, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });


    /**
     * 与服务器进行注册的连接
     * @param phone
     * @param password
     */
    private void SendToServlet(final String phone, final String password) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    HttpClient client = new DefaultHttpClient(); // 建立一个客户端
//                    HttpPost httpPost = new HttpPost(UrlConstant.RegisterUrl); // 包装POST请求
//                    // 设置发送的实体参数
//                    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//                    parameters.add(new BasicNameValuePair("phoneNumber", phone));
//                    parameters.add(new BasicNameValuePair("password", password));
//                    httpPost.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
//                    HttpResponse response = client.execute(httpPost); // 执行POST请求
//                    int code = response.getStatusLine().getStatusCode();
//                    String result= EntityUtils.toString(response.getEntity());
//                    Log.i("Register1",code+"");
//                    Message message=new Message();
//                    message.what=code;
//                    message.obj=result;
//                    handler1.sendMessage(message);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

        RequestParams requestParams=new RequestParams();
        requestParams.put("phoneNumber", phone);
        requestParams.put("password", password);
        AsyncHttpClient httpClient=new AsyncHttpClient();
        httpClient.post(RegisterActivity.this, UrlConstant.RegisterUrl, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Register1","succeed:"+response.toString());
                try {
                    Message message=new Message();
                    String result=response.getString("result");
                    if(result.equals("success"))
                    {
                        Log.i("Register1","result:"+result);
                        message.what=200;
                        handler1.sendMessage(message);
                    }else{
                        message.what=-1;
                        message.obj=response.toString();
                        handler1.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("Register2", responseString);
            }
        });

    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

}
