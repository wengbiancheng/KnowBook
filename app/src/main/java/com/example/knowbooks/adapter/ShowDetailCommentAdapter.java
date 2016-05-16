package com.example.knowbooks.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.knowbooks.entity.response.DetailComment;
import com.example.knowbooks.entity.response.ShowDetailSimpleComment;
import com.example.knowbooks.R;
import com.example.knowbooks.utils.DateUtils;

import java.util.List;

/**
 * Created by qq on 2016/4/22.
 */
public class ShowDetailCommentAdapter extends BaseAdapter {

    private Context context;
    private List<DetailComment> list;

    public ShowDetailCommentAdapter(Context context,List<DetailComment> list){
        this.context=context;
        this.list=list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=View.inflate(context, R.layout.item_simple_comment,null);
            viewHolder.Score= (RatingBar) convertView.findViewById(R.id.SimpleComment_Score);
            viewHolder.content= (TextView) convertView.findViewById(R.id.SimpleComment_Content);
            viewHolder.Name= (TextView) convertView.findViewById(R.id.SimpleComment_Name);
            viewHolder.Time= (TextView) convertView.findViewById(R.id.SimpleComment_time);
            viewHolder.SupportCount= (TextView) convertView.findViewById(R.id.SimpleComment_Support_Count);
            viewHolder.CommentCount= (TextView) convertView.findViewById(R.id.SimpleComment_Comment_Count);
            viewHolder.ImageSupportCount= (ImageView) convertView.findViewById(R.id.SimpleComment_Image_Support);
            viewHolder.ImageCommentCount= (ImageView) convertView.findViewById(R.id.SimpleComment_Image_Comment);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        LayerDrawable starts= (LayerDrawable) viewHolder.Score.getProgressDrawable();
        starts.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        System.out.println("-----------------"+position);

        viewHolder.Score.setRating(Float.parseFloat(String.valueOf(list.get(position).getCommentScore())));
        viewHolder.content.setText(list.get(position).getCommentContent());
        viewHolder.Name.setText(list.get(position).getCommentUser());
        viewHolder.Time.setText(DateUtils.getShortTime(list.get(position).getCreateDate()));
        viewHolder.SupportCount.setText(list.get(position).getNumOfLike()+"");
        viewHolder.CommentCount.setText(list.get(position).getSonCommentCount()+"");

        return convertView;
    }

    public class ViewHolder{
        RatingBar Score;
        TextView content;
        TextView Name;
        TextView Time;
        TextView SupportCount;
        TextView CommentCount;
        ImageView ImageSupportCount;
        ImageView ImageCommentCount;
    }
}
