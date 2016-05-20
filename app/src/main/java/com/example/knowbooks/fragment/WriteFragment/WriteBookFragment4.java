package com.example.knowbooks.fragment.WriteFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;
import com.example.knowbooks.BaseApplication;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.WriteActivity.WriteBookAty;
import com.example.knowbooks.constants.AccessTokenKeeper;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.User;
import com.example.knowbooks.utils.HttpUtil;
import com.example.knowbooks.utils.ImageUtils;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by qq on 2016/4/23.
 */
public class WriteBookFragment4 extends Fragment implements View.OnClickListener ,BDLocationListener {

    private WriteBookAty BaseActivity;


    private Button rightBtn;
    private Button leftBtn;

    private EditText book_reason;

    private LocationClient mLocationClient;

    private String x="";
    private String y="";

    private int flag=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseActivity = (WriteBookAty) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_book4, null);

        leftBtn = (Button) view.findViewById(R.id.write_book_leftBtn);
        rightBtn = (Button) view.findViewById(R.id.write_book_rightBtn);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        book_reason = (EditText) view.findViewById(R.id.write_book_reason);
        BaseApplication baseApplication= (BaseApplication) BaseActivity.getApplication();
        mLocationClient= baseApplication.getInstance();
        mLocationClient.registerLocationListener(this);
        mLocationClient.start();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write_book_leftBtn:
                BaseActivity.getFragmentControl().showFragment(1);
                break;
            case R.id.write_book_rightBtn:
                if (!TextUtils.isEmpty(book_reason.getText().toString())) {
                    BaseActivity.getBook().setRecommenReason(book_reason.getText().toString());
//
//                    new AlertDialog.Builder(BaseActivity).setTitle("注意").setMessage("你已经填完了推荐书籍的信息，点击“发送”发送到服务器，点击“继续”则将填写卖书信息")
//                            .setPositiveButton("发送", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    SendToServlet();
//                                }
//                            }).setNegativeButton("继续", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(BaseActivity, WriteSellAty.class);
//                            startActivity(intent);
//                            BaseActivity.finish();
//                        }
//                    }).create().show();
                    Message message=new Message();
                    message.what=-2;
                    handler.sendMessage(message);
                } else {
                    Toast.makeText(BaseActivity, "请补全信息", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if(msg.what==-2){
                flag++;
                if(flag==2){
                    SendToServlet();
                }
            }else if(msg.what==-10){
                flag++;
                if(flag==2){
                    SendToServlet();
                }
                mLocationClient.stop();
            }else if(msg.what==200){
                Intent intent=new Intent(BaseActivity,BookActivity.class);
                startActivity(intent);
                BaseActivity.finish();
            }
            return false;
        }
    });
    private void SendToServlet() {
        RequestParams requestParams=new RequestParams();
        requestParams.put("bookName",BaseActivity.getBook().getBookName());
        requestParams.put("bookAuthor",BaseActivity.getBook().getBookAuthor());
        requestParams.put("bookClass",BaseActivity.getBook().getBookClass());
        requestParams.put("bookSummary",BaseActivity.getBook().getBookSummary());
        requestParams.put("recommenReason",BaseActivity.getBook().getRecommenReason());
        User user= AccessTokenKeeper.readAccessDate(BaseActivity);
        requestParams.put("phoneNumber",user.getPhoneNumber());
        requestParams.put("bookLocation",x+","+y);

        Uri uri=Uri.parse(BaseActivity.getBook().getTitleImage());

        String img_path=ImageUtils.getImageAbsolutePath19(BaseActivity,uri);
        File file=new File(img_path);
        if(file.exists()){
            Log.i("WriteBookFragment44","file is exist");
        }else{
            Log.i("WriteBookFragment44","file not exist");
        }
        try {
            requestParams.put("bookPicture",file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.i("WriteBookFragment44","file.Name:"+file.getAbsolutePath());

        HttpUtil.getInstance(BaseActivity).post(BaseActivity,UrlConstant.CreateShowBookUrl,requestParams,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("WriteBookFragment44",response.toString());
                try {
                    String result= (String) response.get("result");
                    if(result.equals("success"))
                    {
                       Message message=new Message();
                        message.what=200;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("WriteBookFragment44", "onFailure----"+responseString);
                Toast.makeText(getActivity(),"传输失败",Toast.LENGTH_SHORT).show();
                flag=0;
                mLocationClient.start();
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

            Toast.makeText(BaseActivity, "gps定位成功", Toast.LENGTH_LONG).show();

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
