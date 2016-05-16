package com.example.knowbooks.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.DetailBookList;
import com.example.knowbooks.R;
import com.example.knowbooks.activity.DetailActivity.BookDetailActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq on 2016/4/24.
 */
public class GridViewAdapter extends BaseAdapter{


    private Context context;
    private List<DetailBookList> list=new ArrayList<>();
    private GridView gv;

    private ImageLoader imageLoader;

    public GridViewAdapter(Context context,List<DetailBookList> list,GridView gv){
        this.context=context;
        this.list=list;
        this.gv=gv;
        this.imageLoader=ImageLoader.getInstance();
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=View.inflate(context, R.layout.item_detail_booklist,null);
            viewHolder.imageView= (ImageView) convertView.findViewById(R.id.adapter_detail_booklist_imageView);
            viewHolder.textView= (TextView) convertView.findViewById(R.id.adapter_detail_booklist_textView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

//        imageLoader.displayImage(UrlConstant.url+list.get(position).getBookpicture(),viewHolder.imageView);
        viewHolder.textView.setText(list.get(position).getBookName());
//        int horizontalSpacing = gv.getHorizontalSpacing();
//        int width = (gv.getWidth() - horizontalSpacing * 2
//                - gv.getPaddingLeft() - gv.getPaddingRight()) / 3;
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
//        viewHolder.imageView.setLayoutParams(params);
        imageLoader.displayImage(list.get(position).getBookpicture(),viewHolder.imageView);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, BookDetailActivity.class);
                intent.putExtra("id",list.get(position).getId());
                intent.putExtra("OldActivity","BookListDetail");
                context.startActivity(intent);
            }
        });


        return convertView;
    }

    public class ViewHolder{
        private ImageView imageView;
        private TextView textView;
    }
}
