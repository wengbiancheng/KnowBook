package com.example.knowbooks.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knowbooks.entity.response.BuyBook;
import com.example.knowbooks.R;
import com.example.knowbooks.utils.DateUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq on 2016/4/25.
 */
public class BuyFragmentAdapter extends BaseAdapter {

    private Context context;
    private List<BuyBook>list=new ArrayList<>();
    private ImageLoader imageLoader;

    public BuyFragmentAdapter(Context context,List<BuyBook> list){
        this.context=context;
        this.list=list;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=View.inflate(context, R.layout.item_buyfragment,null);
            viewHolder.imageView= (ImageView) convertView.findViewById(R.id.item_buyfragment_bookImage);
            viewHolder.bookName= (TextView) convertView.findViewById(R.id.item_buyfragment_bookName);
            viewHolder.bookAuthor= (TextView) convertView.findViewById(R.id.item_buyfragment_bookAuthor);
            viewHolder.time= (TextView) convertView.findViewById(R.id.item_buyfragment_time);
            viewHolder.userName= (TextView) convertView.findViewById(R.id.item_buyfragment_userName);
            viewHolder.userSex= (ImageView) convertView.findViewById(R.id.item_buyfragment_sex);
            viewHolder.price=(TextView) convertView.findViewById(R.id.item_buyfragment_price);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

//        viewHolder.imageView
        viewHolder.bookName.setText(list.get(position).getBookName());
        viewHolder.bookAuthor.setText(list.get(position).getBookAuthor()+" 著");
        viewHolder.time.setText(DateUtils.getShortTime(list.get(position).getCreateDate()));
        viewHolder.userName.setText(list.get(position).getBuyBookUser());
        if(list.get(position).getBuyBookUserSex().equals("男")){
            viewHolder.userSex.setImageResource(R.mipmap.man);
        }else{

        }
        viewHolder.price.setText(list.get(position).getBookPrice()+" 元");

        return convertView;
    }
    public class ViewHolder{
        private ImageView imageView;
        private TextView bookName;
        private TextView bookAuthor;
        private TextView time;
        private TextView userName;
        private ImageView userSex;
        private TextView price;
    }
}
