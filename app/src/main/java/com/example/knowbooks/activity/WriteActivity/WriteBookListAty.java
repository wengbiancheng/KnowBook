package com.example.knowbooks.activity.WriteActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
                BookPictureUrl = "";
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

//                BookPictureUrl = imageUri.toString();
//                Log.i("ImagePICTURE", BookPictureUrl);
//                imageView1.setImageURI(imageUri);

                String img_path=ImageUtils.getImageAbsolutePath19(WriteBookListAty.this, imageUri);
                Log.i("ImagePICTURE",imageUri.toString());
                //进行压缩图片操作
                Bitmap bitmap1=getimage(img_path);
                imageView1.setImageBitmap(bitmap1);

                //将图片转化为uri
                Uri uri1 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap1, null,null));
                BookPictureUrl=uri1.toString();


                updateImgs();
                break;
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (resultCode == WriteBookListAty.RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(WriteBookListAty.this, ImageUtils.imageUriFromCamera);
                    Log.i("ImagePICTURE", "fail");
                } else {
                    Uri imageUriCamera = ImageUtils.imageUriFromCamera;
//                    BookPictureUrl = imageUriCamera.toString();
//                    Log.i("ImagePICTURE", imageUriCamera.toString());
//                    imageView1.setImageURI(imageUriCamera);

                    String img_path1=ImageUtils.getImageAbsolutePath19(WriteBookListAty.this,imageUriCamera);
                    //进行压缩图片操作
                    Bitmap temp_bitmap=getimage(img_path1);
                    imageView1.setImageBitmap(temp_bitmap);

                    //将图片转化为uri
                    Uri temp_url = Uri.parse(MediaStore.Images.Media.insertImage(WriteBookListAty.this.getContentResolver(), temp_bitmap, null,null));
                    BookPictureUrl=temp_url.toString();

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
        HttpUtil.getInstance(this).post(this, UrlConstant.CreatebooklistUrl, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("WriteBookListAty1", response.toString());
                try {
                    String result = (String) response.get("result");
                    if (result.equals("success")) {
                        Toast.makeText(WriteBookListAty.this, "书单创建成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(WriteBookListAty.this, BookActivity.class);
                        intent.putExtra("onStart", "1");
                        startActivity(intent);
                    } else {
                        Toast.makeText(WriteBookListAty.this, "创建书单失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("WriteBookListAty1", responseString.toString());
                Toast.makeText(WriteBookListAty.this, "创建书单失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
