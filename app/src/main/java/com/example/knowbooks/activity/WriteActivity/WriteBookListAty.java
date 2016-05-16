package com.example.knowbooks.activity.WriteActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.internal.app.WindowDecorActionBar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.BookActivity;
import com.example.knowbooks.activity.MainActivity;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.Book;
import com.example.knowbooks.utils.HttpUtil;
import com.example.knowbooks.utils.ImageUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by qq on 2016/4/24.
 */
public class WriteBookListAty extends Activity implements View.OnClickListener {


    private EditText editText;
    private ImageView imageView1;
    private ImageView deleteImage1;
    private Button btn;


    private ImageButton title_left;
    private TextView title_middle;
    private Button title_right;

    private String BookPictureUrl="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write_booklist);
        initView();
        initListener();
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.write_booklist_bookName);
        imageView1 = (ImageView) findViewById(R.id.write_booklist_iv_image);
        deleteImage1 = (ImageView) findViewById(R.id.write_booklist_iv_delete_image);
        btn = (Button) findViewById(R.id.write_booklist_btn);
        btn.setOnClickListener(this);


        title_left = (ImageButton) findViewById(R.id.title_leftImageBtn);
        title_middle = (TextView) findViewById(R.id.title_middleTextView);
        title_right = (Button) findViewById(R.id.title_rightBtn);
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);
        title_middle.setText("创建书单");
    }

    private void initListener() {
        deleteImage1.setVisibility(View.GONE);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtils.showImagePickDialog(WriteBookListAty.this);
            }
        });
        deleteImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookPictureUrl="";
                imageView1.setImageResource(R.drawable.compose_pic_add_more);
                updateImgs();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftImageBtn:
                Intent intent = new Intent(WriteBookListAty.this, BookActivity.class);
                intent.putExtra("onStart", "1");
                startActivity(intent);
                WriteBookListAty.this.finish();
                break;
            case R.id.title_rightBtn:
                Intent intent1 = new Intent(WriteBookListAty.this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.write_booklist_btn:
                SendToServlet();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if (resultCode == WriteBookListAty.RESULT_CANCELED) {
                    Log.i("ImagePICTURE", "fail");
                    return;
                }
                Uri imageUri = data.getData();

                BookPictureUrl = imageUri.toString();
                Log.i("ImagePICTURE", BookPictureUrl);
                imageView1.setImageURI(imageUri);
                updateImgs();
                break;
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (resultCode == WriteBookListAty.RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(WriteBookListAty.this, ImageUtils.imageUriFromCamera);
                    Log.i("ImagePICTURE", "fail");
                } else {
                    Uri imageUriCamera = ImageUtils.imageUriFromCamera;
                    BookPictureUrl = imageUriCamera.toString();
                    Log.i("ImagePICTURE", imageUriCamera.toString());
                    imageView1.setImageURI(imageUriCamera);
                    updateImgs();
                }
                break;

            default:
                break;
        }
    }

    private void updateImgs() {
        if(!TextUtils.isEmpty(BookPictureUrl)){
            deleteImage1.setVisibility(View.VISIBLE);
        }else{
            deleteImage1.setVisibility(View.GONE);
        }
    }

    private void SendToServlet() {

        RequestParams requestParams=new RequestParams();
        Uri uri=Uri.parse(BookPictureUrl);

        String img_path=ImageUtils.getImageAbsolutePath19(WriteBookListAty.this,uri);
        File file=new File(img_path);
        if(file.exists()) {
            try {
                requestParams.put("booklistPicture", file);
                Log.i("WriteBookListAty1","file exists");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this,"信息有误,请重新选择图片",Toast.LENGTH_SHORT).show();
        }


        requestParams.put("booklistName",editText.getText().toString());
        Log.i("WriteBookListAty1", editText.getText().toString());
        HttpUtil.getInstance(this).post(this, UrlConstant.CreatebooklistUrl,requestParams,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("WriteBookListAty1",response.toString());
                try {
                    String result= (String) response.get("result");
                    if(result.equals("success")){
                        Intent intent=new Intent(WriteBookListAty.this, BookActivity.class);
                        intent.putExtra("onStart","1");
                        startActivity(intent);
                    }else{
                        Toast.makeText(WriteBookListAty.this,"创建书单失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("WriteBookListAty1", responseString.toString());
                Toast.makeText(WriteBookListAty.this,"创建书单失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
