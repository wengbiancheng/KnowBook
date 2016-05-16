package com.example.knowbooks.fragment.WriteFragment;

import android.content.Intent;
import android.database.Cursor;
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

                String url=imageUri.toString();
                Log.i("ImagePICTURE",url);
                BaseActivity.getBook().setTitleImage(url);
                imageView1.setImageURI(imageUri);
                updateImgs();
                break;
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (resultCode == BaseActivity.RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(BaseActivity, ImageUtils.imageUriFromCamera);
                    Log.i("ImagePICTURE", "fail");
                } else {
                    Uri imageUriCamera = ImageUtils.imageUriFromCamera;

                    Log.i("ImagePICTURE",imageUriCamera.toString());
                    BaseActivity.getBook().setTitleImage(imageUriCamera.toString());
                    imageView1.setImageURI(imageUriCamera);
                    updateImgs();
                }
                break;

            default:
                break;
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
