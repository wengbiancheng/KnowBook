package com.example.knowbooks.fragment.WriteFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;
import com.example.knowbooks.BaseApplication;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.WriteActivity.WriteBookAty;
import com.example.knowbooks.activity.WriteActivity.WriteBuyBookAty;
import com.example.knowbooks.activity.WriteActivity.WriteWantBookAty;
import com.example.knowbooks.constants.AccessTokenKeeper;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.User;
import com.example.knowbooks.utils.HttpUtil;
import com.example.knowbooks.utils.ImageUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by qq on 2016/4/23.
 */
public class WriteWantBookFragment4 extends Fragment implements View.OnClickListener,BDLocationListener {

    private WriteWantBookAty BaseActivity;

    private Button rightBtn;
    private Button leftBtn;

    private TextView book_repay_textView;
    private EditText book_repay;

    private LocationClient mLocationClient;

    private String x = "";
    private String y = "";

    private int flag = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseActivity = (WriteWantBookAty) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_book4, null);

        leftBtn = (Button) view.findViewById(R.id.write_book_leftBtn);
        rightBtn = (Button) view.findViewById(R.id.write_book_rightBtn);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        book_repay_textView = (TextView) view.findViewById(R.id.write_book_reason_textView);
        book_repay_textView.setText("心愿报偿");
        book_repay = (EditText) view.findViewById(R.id.write_book_reason);
        book_repay.setHint("请输入你所能提供的心愿报偿");
        BaseApplication baseApplication = (BaseApplication) BaseActivity.getApplication();
        mLocationClient = baseApplication.getInstance();
        mLocationClient.registerLocationListener(this);
        mLocationClient.start();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write_book_leftBtn:
                BaseActivity.getFragmentControl().showFragment(2);
                break;
            case R.id.write_book_rightBtn:
                if (!TextUtils.isEmpty(book_repay.getText().toString())) {
                    BaseActivity.getBook().setWishPay(book_repay.getText().toString());
                    Message message = new Message();
                    message.what = -2;
                    handler.sendMessage(message);
                } else {
                    Toast.makeText(BaseActivity, "请补全信息", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if (msg.what == -2) {
                flag++;
                if (flag == 2) {
                    SendToServlet();
                }
            } else if (msg.what == -10) {
                flag++;
                if (flag == 2) {
                    SendToServlet();
                }
                mLocationClient.stop();
            } else if (msg.what == 200) {
                Intent intent = new Intent(BaseActivity, BookActivity.class);
                startActivity(intent);
                BaseActivity.finish();
            }
            return false;
        }
    });

    private void SendToServlet() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("WantBookName", BaseActivity.getBook().getWantBookName());
        requestParams.put("WantBookAuthor", BaseActivity.getBook().getWantBookAuthor());
        requestParams.put("WantBookType", BaseActivity.getBook().getBookClass());
        requestParams.put("WantBookPay", BaseActivity.getBook().getWishPay());
        requestParams.put("WishPostiton", BaseActivity.getBook().getDistance());
        requestParams.put("WantBookContent", BaseActivity.getBook().getWishContent());

        Uri uri = Uri.parse(BaseActivity.getBook().getWantBookPicture());

        String img_path = ImageUtils.getImageAbsolutePath19(BaseActivity, uri);
        File file = new File(img_path);
        if (file.exists()) {
            Log.i("Log1", "wantBook创建时文件是:file is exist");
        } else {
            Log.i("Log1", "wantBook创建时文件是:file not exist");
        }
        try {
            requestParams.put("WantBookPicture", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        HttpUtil.getInstance(BaseActivity).post(BaseActivity, UrlConstant.CreateWantUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Log1", "创建心愿书籍返回的结果是：" + response.toString());

                try {
                    String result = (String) response.get("result");
                    if (result.equals("success")) {
                        Toast.makeText(BaseActivity,"创建心愿书单成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BaseActivity, BookActivity.class);
                        intent.putExtra("PhoneNumber",BaseActivity.getPhoneNumber());
                        intent.putExtra("onStart", "3");
                        startActivity(intent);
                        BaseActivity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("Log1", "创建心愿书籍失败，原因是：" + responseString.toString());
            }

        });
    }


    @Override
    public void onReceiveLocation(BDLocation location) {
        //Receive Location
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");//纬度
        x=location.getLatitude()+"";
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");//经度
        y=location.getLongitude()+"";
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        BaseActivity.getBook().setDistance(x+","+y);
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
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
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

