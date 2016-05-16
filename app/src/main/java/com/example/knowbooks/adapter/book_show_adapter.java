package com.example.knowbooks.adapter;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.Book;
import com.example.knowbooks.R;
import com.example.knowbooks.utils.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xieyongxiong on 2016/4/20.
 */
public class book_show_adapter extends BaseAdapter {

    private Context context;
    private List<Book> list = new ArrayList<>();
    private ViewHolder viewHolder = null;
    private ImageLoader imageLoader;

    private Handler handler;


    public book_show_adapter(Context mcontext, List<Book> list,Handler handler) {
        this.context = mcontext;
        this.list = list;
        this.imageLoader = ImageLoader.getInstance();
        this.handler=handler;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_showbook, null);
            viewHolder = new ViewHolder();
            viewHolder.book_name = (TextView) convertView.findViewById(R.id.show_bookName);
            viewHolder.book_userName = (TextView) convertView.findViewById(R.id.show_userName);
            viewHolder.book_score = (TextView) convertView.findViewById(R.id.show_booknumScore);
            viewHolder.book_commentCount = (TextView) convertView.findViewById(R.id.show_bookCommentCount);
            viewHolder.book_content = (TextView) convertView.findViewById(R.id.show_bookContent);
            viewHolder.book_location = (TextView) convertView.findViewById(R.id.show_distance);
            viewHolder.book_pic = (ImageView) convertView.findViewById(R.id.show_leftImageView);
            viewHolder.book_author = (TextView) convertView.findViewById(R.id.show_bookAuthor);
            viewHolder.book_sex = (ImageView) convertView.findViewById(R.id.show_userSex);
            viewHolder.isCollect = (Button) convertView.findViewById(R.id.show_isCollect);
            viewHolder.ratingBar_score = (RatingBar) convertView.findViewById(R.id.show_RatingScore);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Log.i("SHOWFragmentitem", list.get(position).toString());
        viewHolder.book_name.setText(list.get(position).getBookName());
        viewHolder.book_userName.setText(list.get(position).getUserName());
        viewHolder.book_score.setText(list.get(position).getBookScore() + "");
        viewHolder.book_commentCount.setText(list.get(position).getNumOfComments() + "");
        viewHolder.book_content.setText(list.get(position).getBookSummary());
        viewHolder.book_location.setText(list.get(position).getBookLocation());
        viewHolder.book_author.setText(list.get(position).getBookAuthor()+" 著");
        viewHolder.ratingBar_score = (RatingBar) convertView.findViewById(R.id.show_RatingScore);
        LayerDrawable starts = (LayerDrawable) viewHolder.ratingBar_score.getProgressDrawable();
        starts.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        //还缺少个女性图标的标识
        if (list.get(position).getUserSex().equals("男")) {
            viewHolder.book_sex.setImageResource(R.mipmap.man);
        } else {

        }
        //还缺少个收藏后的图标颜色变化
        if (list.get(position).getIsCollect() == 0) {
            viewHolder.isCollect.setText("收藏");
        } else {
            viewHolder.isCollect.setText("取消收藏");
        }
        viewHolder.isCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long ItemBookId=list.get(position).getId();
                Message message=new Message();
                message.what=5;
                message.obj= ItemBookId;
                message.arg1=list.get(position).getIsCollect();
                handler.sendMessage(message);
            }
        });
        if (list.get(position).getBookScore() == 0) {
            viewHolder.ratingBar_score.setNumStars(0);
        } else {
            viewHolder.ratingBar_score.setRating(Float.parseFloat(String.valueOf(list.get(position).getBookScore())));
        }
        Log.i("fragmentShow", "ImageUrl:" + list.get(position).getTitleImage());
        imageLoader.displayImage(UrlConstant.url + list.get(position).getTitleImage(), viewHolder.book_pic);


//        ViewTreeObserver observer=viewHolder.book_content.getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                ViewTreeObserver obs=viewHolder.book_content.getViewTreeObserver();
//                obs.removeGlobalOnLayoutListener(this);
//                if(viewHolder.book_content.getLineCount()>0)
//                {
//                    int lineEndIndex=viewHolder.book_content.getLayout().getLineEnd(1);
//                    String text=viewHolder.book_content.getText().subSequence(0,lineEndIndex-3)+"...";
//                    viewHolder.book_content.setText(text);
//                }
//            }
//        });
        return convertView;
    }

    private static class ViewHolder {
        TextView book_name, book_userName, book_score, book_commentCount, book_content, book_location, book_author;
        ImageView book_sex;
        Button isCollect;
        RatingBar ratingBar_score;
        ImageView book_pic;
    }
}
