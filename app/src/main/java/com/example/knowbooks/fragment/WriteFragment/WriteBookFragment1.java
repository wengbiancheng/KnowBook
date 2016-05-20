package com.example.knowbooks.fragment.WriteFragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.knowbooks.R;
import com.example.knowbooks.activity.WriteActivity.WriteBookAty;
import com.example.knowbooks.adapter.BLSonFragmentAdapter;
import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.utils.HttpUtil;
import com.example.knowbooks.utils.ImageUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq on 2016/4/23.
 */
public class WriteBookFragment1 extends Fragment implements View.OnClickListener {

    private WriteBookAty BaseActivity;

    private EditText bookName;

    private RelativeLayout view1;
    private RelativeLayout view2;
    private ImageView imageView1;
    private ImageView deleteImage1;


    private Button rightBtn;
    private Button leftBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseActivity = (WriteBookAty) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_book1, null);
        view1 = (RelativeLayout) view.findViewById(R.id.write_book_bookImage1);
        bookName = (EditText) view.findViewById(R.id.write_book_bookName);
        imageView1 = (ImageView) view.findViewById(R.id.iv_image);
        deleteImage1 = (ImageView) view.findViewById(R.id.iv_delete_image);


        leftBtn = (Button) view.findViewById(R.id.write_book_leftBtn);
        rightBtn = (Button) view.findViewById(R.id.write_book_rightBtn);
        initListener();
        CheckIsBack();
        updateImgs();
        return view;
    }

    private void initListener() {
        deleteImage1.setVisibility(View.GONE);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtils.showImagePickDialog(BaseActivity);
            }
        });
        deleteImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.getBook().setTitleImage("");
                imageView1.setImageResource(R.drawable.compose_pic_add_more);
                updateImgs();
            }
        });

        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        leftBtn.setEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if (resultCode == BaseActivity.RESULT_CANCELED) {
                    Log.i("ImagePICTURE","fail");
                    return;
                }
                Uri imageUri = data.getData();

                String img_path=ImageUtils.getImageAbsolutePath19(BaseActivity, imageUri);
                Log.i("ImagePICTURE",imageUri.toString());
                //进行压缩图片操作
                Bitmap bitmap1=getimage(img_path);
                imageView1.setImageBitmap(bitmap1);

                //将图片转化为uri
                Uri uri1 = Uri.parse(MediaStore.Images.Media.insertImage(BaseActivity.getContentResolver(), bitmap1, null,null));
                BaseActivity.getBook().setTitleImage(uri1.toString());


                updateImgs();
                break;
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (resultCode == BaseActivity.RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(BaseActivity, ImageUtils.imageUriFromCamera);
                    Log.i("ImagePICTURE", "fail");
                } else {
                    Uri imageUriCamera = ImageUtils.imageUriFromCamera;

                    String img_path1=ImageUtils.getImageAbsolutePath19(BaseActivity,imageUriCamera);
                    Log.i("ImagePICTURE",imageUriCamera.toString());
                    //进行压缩图片操作
                    Bitmap temp_bitmap=getimage(img_path1);
                    imageView1.setImageBitmap(temp_bitmap);

                    //将图片转化为uri
                    Uri temp_url = Uri.parse(MediaStore.Images.Media.insertImage(BaseActivity.getContentResolver(), temp_bitmap, null,null));
                    BaseActivity.getBook().setTitleImage(temp_url.toString());
                    updateImgs();
                }
                break;

            default:
                break;
        }
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

    private void setImage(Uri mImageCaptureUri) {

        // 不管是拍照还是选择图片每张图片都有在数据中存储也存储有对应旋转角度orientation值
        // 所以我们在取出图片是把角度值取出以便能正确的显示图片,没有旋转时的效果观看

        ContentResolver cr = BaseActivity.getContentResolver();
        Cursor cursor = cr.query(mImageCaptureUri, null, null, null, null);// 根据Uri从数据库中找
        if (cursor != null) {
            cursor.moveToFirst();// 把游标移动到首位，因为这里的Uri是包含ID的所以是唯一的不需要循环找指向第一个就是了
            String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路
            String orientation = cursor.getString(cursor
                    .getColumnIndex("orientation"));// 获取旋转的角度
            cursor.close();
            if (filePath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);//根据Path读取资源图片
                int angle = 0;
                if (orientation != null && !"".equals(orientation)) {
                    angle = Integer.parseInt(orientation);
                }
                if (angle != 0) {
                    // 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
                    Matrix m = new Matrix();
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    m.setRotate(90); // 旋转angle度
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                            m, true);// 从新生成图片

                }
                imageView1.setImageBitmap(bitmap);
            }
        }
    }

    private void updateImgs() {
        if (!TextUtils.isEmpty(BaseActivity.getBook().getTitleImage())) {
            deleteImage1.setVisibility(View.VISIBLE);
        } else {
            deleteImage1.setVisibility(View.GONE);
        }
    }

    private void CheckIsBack() {
        if (!TextUtils.isEmpty(BaseActivity.getBook().getBookName())) {
            bookName.setText(BaseActivity.getBook().getBookName());
        }
        if (!TextUtils.isEmpty(BaseActivity.getBook().getTitleImage())) {
            Uri url = Uri.parse(BaseActivity.getBook().getTitleImage());
            imageView1.setImageURI(url);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write_book_leftBtn:
                break;
            case R.id.write_book_rightBtn:
                if (!TextUtils.isEmpty(bookName.getText().toString()) && !TextUtils.isEmpty(BaseActivity.getBook().getTitleImage())) {
                    BaseActivity.getBook().setBookName(bookName.getText().toString());
                    BaseActivity.getFragmentControl().showFragment(1);
                } else {
                    Toast.makeText(BaseActivity, "请补全信息", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
