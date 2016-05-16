package com.example.knowbooks.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.BaseApplication;
import com.example.knowbooks.constants.AccessTokenKeeper;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.WriteActivity.WriteBookAty;
import com.example.knowbooks.activity.WriteActivity.WriteBookListAty;
import com.example.knowbooks.activity.WriteActivity.WriteBuyBookAty;
import com.example.knowbooks.activity.WriteActivity.WriteWantBookAty;
import com.example.knowbooks.entity.User;
import com.example.knowbooks.fragment.FragmentControl.BookAtyFragmentController;
import com.example.knowbooks.fragment.LeftSlideMenu;
import com.example.knowbooks.fragment.ShowFragment;
import com.example.knowbooks.fragment.WriteFragment.WriteBookFragment1;
import com.example.knowbooks.utils.DButil;
import com.example.knowbooks.utils.HttpUtil;
import com.example.knowbooks.utils.ImageUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static com.example.knowbooks.R.id.connect_image_btn;

/**
 * Created by qq on 2016/4/19.
 */
public class BookActivity extends SlidingFragmentActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, BDLocationListener {


    public LocationClient mLocationClient = null;

    //Fragment的控制类
    private BookAtyFragmentController controller;

    private Fragment mMainActivity;

    //底部类
    private RadioGroup rg_tab;
    private RadioButton rb_show, rb_list, rb_buy, rb_want;

    //标题栏控件初始化
    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    private static int status = 0;
    private static int flag = 0;

    private AlertDialog alertDialog = null;

    private String Name;
    private String Sex;
    private String x;
    private String y;

    private String PhoneNumber;
    private String LocalUserName;
    private String Localsex;
    private User user=new User();

    private DButil db;

    private String Connect_image="";
    private String Connect_phone;
    private String Connect_QQ;
    private String Connect_weixin;
    private boolean Connect_Flag=false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_book);
        controller = BookAtyFragmentController.getInstance(BookActivity.this, R.id.fl_content);

        //初始化侧滑菜单
        initSlidingMenu(savedInstanceState);
        initView();
        controller.showFragment(0);
        //由标题栏左边控件结束按钮导致的界面重新恢复到具体的fragment
        if (!TextUtils.isEmpty(getIntent().getStringExtra("onStart"))) {
            int postion = Integer.parseInt(getIntent().getStringExtra("onStart"));
            controller.showFragment(postion);
            rb_show.setChecked(false);
            if (postion == 1) {
                rb_list.setChecked(true);
            } else if (postion == 2) {
                rb_buy.setChecked(true);
            } else if (postion == 3) {
                rb_want.setChecked(true);
            }
        }

        mLocationClient = ((BaseApplication) getApplication()).getInstance();
        mLocationClient.registerLocationListener(this);


        //进行PhoneNumber的传输
        if (!TextUtils.isEmpty(getIntent().getStringExtra("PhoneNumber"))) {
            PhoneNumber = getIntent().getStringExtra("PhoneNumber");
        }

        //判断是否要进行AlertDialog的跳转
        if (!TextUtils.isEmpty(getIntent().getStringExtra("LoginToBook"))) {
            if (getIntent().getStringExtra("LoginToBook").equals("1")) {

//                user=AccessTokenKeeper.readAccessDate(this);,nbh
                //从数据库获取User，如果是首次登陆，则获取到null
                Log.i("LoginAdd1", "登陆后传来的电话号码是:---------" + PhoneNumber);
                user = db.getUser(PhoneNumber);
                Log.i("LoginAdd1", "登陆后读取到的数据时:---------" + user.toString());
                if (!user.getPhoneNumber().equals(PhoneNumber) || (user == null)) {
                    flag = 0;
                    Log.i("LoginAdd1", "数据库找不到该电话号码");
                    initAlertDialog();
                } else {
                    flag = 1;
                    Log.i("LoginAdd1", "数据库找到了该电话号码");
                }

                mLocationClient.start();
            }
        }
    }

    /**
     * 初始化侧滑菜单
     *
     * @param savedInstanceState
     */
    private void initSlidingMenu(Bundle savedInstanceState) {
        //设置左侧滑动菜单
        setBehindContentView(R.layout.menu_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new LeftSlideMenu()).commit();

        // 实例化滑动菜单对象
        SlidingMenu sm = getSlidingMenu();
        // 设置可以左右滑动的菜单
        sm.setMode(SlidingMenu.LEFT);
        // 设置滑动阴影的宽度
        sm.setShadowWidthRes(R.dimen.shadow_width);
        // 设置滑动菜单阴影的图像资源
        sm.setShadowDrawable(null);
        // 设置滑动菜单视图的宽度
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        sm.setFadeDegree(0.35f);
        // 设置触摸屏幕的模式,这里设置为全屏
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置下方视图的在滚动时的缩放比例
        sm.setBehindScrollScale(0.0f);
    }

    private void initView() {
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
        rb_show = (RadioButton) findViewById(R.id.book_rb_show);
        rb_list = (RadioButton) findViewById(R.id.book_rb_list);
        rb_buy = (RadioButton) findViewById(R.id.book_rb_buy);
        rb_want = (RadioButton) findViewById(R.id.book_rb_sell);

        rg_tab.setOnCheckedChangeListener(this);

        title_left = (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle = (TextView) findViewById(R.id.title_middleTextView);
        title_right = (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40, 40);
        params.setMargins(0, 0, 15, 5);
        title_right.setLayoutParams(params);
        title_right.setBackgroundResource(R.mipmap.add);
        title_right.setText("");
        title_middle.setText("书籍社区");

        db = new DButil(BookActivity.this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BookAtyFragmentController.onDestroy();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.book_rb_show:
                controller.showFragment(0);
                title_middle.setText("书籍社区");
                status = 0;
                break;
            case R.id.book_rb_list:
                controller.showFragment(1);
                title_middle.setText("书单社区");
                status = 1;
                break;
            case R.id.book_rb_buy:
                controller.showFragment(2);
                title_middle.setText("买书市场");
                status = 2;
                break;
            case R.id.book_rb_sell:
                controller.showFragment(3);
                title_middle.setText("心愿市场");
                status = 3;
                break;
            default:
                break;
        }
    }

    /**
     * 初始化标题栏最右边控件的监控事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
//                SendToServlet();
                toggle();
                break;
            case R.id.title_rightBtn:
                if (status == 0) {
                    Intent intent = new Intent(BookActivity.this, WriteBookAty.class);
                    startActivity(intent);
                } else if (status == 1) {
                    Intent intent = new Intent(BookActivity.this, WriteBookListAty.class);
                    startActivity(intent);
                } else if (status == 2) {
                    if(TextUtils.isEmpty(user.getConnectPhone())){
                        Log.i("Test", "启动更新联系人操作");
                        initAlertDialog();
//                        ConnectAlertDialog();
                    }else{
                        Intent intent = new Intent(BookActivity.this, WriteBuyBookAty.class);
                        startActivity(intent);
                    }
                } else if (status == 3) {
                    if(TextUtils.isEmpty(user.getConnectPhone())){
                        ConnectAlertDialog();
                    }else{
                        Intent intent = new Intent(BookActivity.this, WriteWantBookAty.class);
                        startActivity(intent);
                    }
                }
                BookActivity.this.finish();
                break;
        }
    }
    private ImageView Connect_imageView;
    private ImageView Connect_deleteImageView;
    private Button Connect_image_btn;
    private AlertDialog alertDialog1;
    private AlertDialog alertDialog2;
    private void ConnectAlertDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alertdialog_connect, null);
        alertDialog1 = new AlertDialog.Builder(BookActivity.this).setView(view)
                .setTitle("完善个人信息").create();
        alertDialog1.show();
        alertDialog1.setCanceledOnTouchOutside(false);
        Log.i("Test", "启动更新联系人操作 alertDialog1显示");

        Connect_imageView= (ImageView) view.findViewById(R.id.iv_image);
        Connect_deleteImageView= (ImageView) view.findViewById(R.id.iv_delete_image);
        Connect_image_btn= (Button) view.findViewById(connect_image_btn);

        Connect_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
                ConnectAlertDialog1();
            }
        });

        Connect_deleteImageView.setVisibility(View.GONE);

        Connect_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtils.showImagePickDialog(BookActivity.this);
            }
        });
        Connect_deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connect_image = "";
                Connect_imageView.setImageResource(R.drawable.compose_pic_add_more);
                upConnectImgs();
            }
        });

    }
    private void ConnectAlertDialog1(){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alertdialog_connect1, null);
        alertDialog2 = new AlertDialog.Builder(BookActivity.this).setView(view)
                .setTitle("完善个人信息").create();
        alertDialog2.show();
        Log.i("Test", "启动更新联系人操作 alertDialog2显示");

        final EditText Phone= (EditText) view.findViewById(R.id.connect_phoneNumber);
        final EditText QQ= (EditText) view.findViewById(R.id.connect_QQ);
        final EditText Weixin= (EditText) view.findViewById(R.id.connect_weixin);
        Button button= (Button) view.findViewById(R.id.connect_image_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connect_phone = Phone.getText().toString();
                Connect_weixin = Weixin.getText().toString();
                Connect_QQ = QQ.getText().toString();
                if (!TextUtils.isEmpty(Connect_phone) && !TextUtils.isEmpty(Connect_weixin) && !TextUtils.isEmpty(Connect_QQ)) {
                    SendToAddServlet();
                } else {
                    Toast.makeText(BookActivity.this, "请补全信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendToAddServlet(){

        RequestParams requestParams=new RequestParams();

        Uri uri=Uri.parse(Connect_image);
        String img_path= ImageUtils.getImageAbsolutePath19(this, uri);
        File file=new File(img_path);
        if(file.exists()){
            Log.i("Log1","用户自己创建的照片is exist");
        }else{
            Log.i("Log1","用户自己创建的照片 not exist");
        }
        try {
            requestParams.put("BuyBookPicture",file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        requestParams.put("ConnectPhone",Connect_phone);
        requestParams.put("ConnectPhone",Connect_QQ);
        requestParams.put("ConnectPhone",Connect_weixin);

        HttpUtil.getInstance(this).get(this, UrlConstant.UserAdd, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("LoginAdd1", "succeed:" + response.toString());
                try {
                    Message message = new Message();
                    String result = response.getString("result");
                    if (result.equals("success")) {
                        Log.i("LoginAdd1", "result:" + result);
                        message.what = 2000;
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
                Log.i("LoginAdd1", responseString);
                Message message = new Message();
                message.what = -1;
                message.obj = responseString.toString();
                handler.sendMessage(message);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if (resultCode == BookActivity.RESULT_CANCELED) {
                    Log.i("ImagePICTURE","fail");
                    return;
                }
                Uri imageUri = data.getData();

                String url=imageUri.toString();
                Log.i("ImagePICTURE",url);
                Connect_image=url;
                Connect_imageView.setImageURI(imageUri);
                upConnectImgs();
                break;
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (resultCode == BookActivity.RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);
                    Log.i("ImagePICTURE", "fail");
                } else {
                    Uri imageUriCamera = ImageUtils.imageUriFromCamera;

                    Log.i("ImagePICTURE",imageUriCamera.toString());
                    Connect_image=imageUriCamera.toString();
                    Connect_imageView.setImageURI(imageUriCamera);
                    upConnectImgs();
                }
                break;

            default:
                break;
        }
    }
    private void upConnectImgs(){
        if(TextUtils.isEmpty(Connect_image)){
            Connect_imageView.setImageResource(R.drawable.compose_pic_add_more);
            Connect_deleteImageView.setVisibility(View.GONE);
        }else{
            Connect_deleteImageView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 登陆成功后的对话框
     */
    private void initAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alertdialog_login, null);
        alertDialog = new AlertDialog.Builder(BookActivity.this).setView(view)
                .setTitle("完善个人信息").create();
        alertDialog.show();

        alertDialog.setCanceledOnTouchOutside(false);

        final EditText userName = (EditText) view.findViewById(R.id.alert_name);
        final Spinner sex = (Spinner) view.findViewById(R.id.alert_sex);
        Button button = (Button) view.findViewById(R.id.alert_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = userName.getText().toString();
                long position = sex.getSelectedItemId();
                if (position == 0) {
                    Sex = "男";
                } else {
                    Sex = "女";
                }
                if (!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Sex)) {
                    //服务器连接
                    Message message = new Message();
                    message.what = -5;
                    handler.sendMessage(message);
                } else {
                    Toast.makeText(BookActivity.this, "请填完整信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 与服务器进行用户个人信息的增加操作
     */
    private void SendToServlet() {
        RequestParams requestParams = new RequestParams();
        if (!TextUtils.isEmpty(Name) && TextUtils.isEmpty(Sex)) {//第一次登陆
            requestParams.put("userName", Name);
            requestParams.put("sex", Sex);
            Log.i("LoginAdd1","第一次登陆");
        } else {//第二次登陆，因为x和y不同而刷新服务器
            requestParams.put("userName", user.getUserName());
            requestParams.put("sex", user.getSex());
//            requestParams.put("x", user.getX());
//            requestParams.put("y", user.getY());
            Log.i("LoginAdd1","第二次登陆，刷新服务器");
        }


        HttpUtil.getInstance(BookActivity.this).post(BookActivity.this, UrlConstant.LoginAddUrl, requestParams, new JsonHttpResponseHandler() {

            //这里的Header[]由于删除了HttpClientAPI的原因,所以这里应该引入库httpclient-4.3.6.jar
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("LoginAdd1", "succeed:" + response.toString());
                try {
                    Message message = new Message();
                    String result = response.getString("result");
                    if (result.equals("success")) {
                        Log.i("LoginAdd1", "result:" + result);
                        message.what = 200;
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
                Log.i("LoginAdd1", responseString);
                Message message = new Message();
                message.what = -1;
                message.obj = responseString.toString();
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 等到定位成功后和填写完毕后才发送信息到服务器
     * 如果返回200,则说明数据发送到服务器已经成功
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == -5) {
                flag++;
                if (flag == 2) {
                    SendToServlet();
                }
            } else if (msg.what == -10) {
                flag++;
                if (flag == 2) {
                    if (!TextUtils.isEmpty(user.getX())) {
                        if (!user.getX().equals(x) || !user.getY().equals(y)) {
                            SendToServlet();
                            Log.i("LoginAdd1","因为定位进行服务器的更新操作");
                        }
                        Log.i("LoginAdd1","不用因为定位进行服务器的更新操作");
                    } else {
                        SendToServlet();
                        Log.i("LoginAdd1", "因为定位进行服务器的更新操作");
                    }
                }
                mLocationClient.stop();
            } else if (msg.what == 200) {

                Toast.makeText(BookActivity.this, "个人信息更新成功", Toast.LENGTH_SHORT).show();
                Log.i("LoginAdd1", "nowLocation:x:" + x + ",y:" + y);
                User user = new User();
                user.setPhoneNumber(PhoneNumber);
                user.setUserName(Name);
                user.setSex(Sex);
                user.setX(x);
                user.setY(y);
                db.addUser(user);
//                AccessTokenKeeper.writeAccessData(BookActivity.this,PhoneNumber,Name,Sex,x,y);vcv
                alertDialog.dismiss();
            } else if (msg.what == -1) {
                Toast.makeText(BookActivity.this, "材料上传失败,请重新填写资料", Toast.LENGTH_SHORT).show();
                Log.i("LoginAddFail", (String) msg.obj);
                Log.i("LoginAdd1", "nowLocation:x:" + x + ",y:" + y);
                flag = 1;
            }else if(msg.what==2000){
                alertDialog2.dismiss();
                Toast.makeText(BookActivity.this,"用户信息上传成功",Toast.LENGTH_SHORT).show();
            }

            return false;
        }
    });


    @Override
    public void onReceiveLocation(BDLocation location) {
        //Receive Location
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");//纬度
        x = location.getLatitude() + "";
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");//经度
        y = location.getLongitude() + "";
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");


        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
            Toast.makeText(BookActivity.this, "定位失败", Toast.LENGTH_LONG).show();
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            Toast.makeText(BookActivity.this, "定位失败", Toast.LENGTH_LONG).show();
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        List<Poi> list = location.getPoiList();// POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        handler.sendEmptyMessage(-10);
        Log.i("BaiduLocationApiDem", sb.toString());
    }
}