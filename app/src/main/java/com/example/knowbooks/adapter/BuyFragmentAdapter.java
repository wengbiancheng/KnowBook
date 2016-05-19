package com.example.knowbooks.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knowbooks.constants.UrlConstant;
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
            viewHolder.locationRange= (TextView) convertView.findViewById(R.id.item_buyfragment_location);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

        imageLoader.displayImage(UrlConstant.url + list.get(position).getBookPicture(), viewHolder.imageView);
        Log.i("Buy", "buy界面的listView的adapter是:" + UrlConstant.url + list.get(position).getBookPicture());
        viewHolder.bookName.setText(list.get(position).getBookName());
        viewHolder.bookAuthor.setText(list.get(position).getBookAuthor()+" 著");
        viewHolder.time.setText(DateUtils.getShortTime(list.get(position).getCreateDate()));
        viewHolder.userName.setText(list.get(position).getBuyBookUser());
        if(list.get(position).getBuyBookUserSex().equals("男")){
            viewHolder.userSex.setImageResource(R.mipmap.man);
        }else{
            viewHolder.userSex.setImageResource(R.mipmap.woman);
        }
        viewHolder.price.setText(list.get(position).getBookPrice()+" 元");

        String locationRange=list.get(position).getLocationRange();
        if (TextUtils.isEmpty(locationRange)){
            viewHolder.locationRange.setText("距离您约0m内");
        }else {
            if (locationRange.equals("8")) {
                viewHolder.locationRange.setText("距离您约20m内");
            } else if (locationRange.equals("7")) {
                viewHolder.locationRange.setText("距离您约80m内");
            } else if (locationRange.equals("6")) {
                viewHolder.locationRange.setText("距离您约610m内");
            } else if (locationRange.equals("5")) {
                viewHolder.locationRange.setText("距离您约2.4km内");
            } else if (locationRange.equals("4")) {
                viewHolder.locationRange.setText("距离您约20km内");
            } else if (locationRange.equals("3")) {
                viewHolder.locationRange.setText("距离您约80km内");
            } else if (locationRange.equals("2")) {
                viewHolder.locationRange.setText("距离您约630km内");
            } else if (locationRange.equals("1")) {
                viewHolder.locationRange.setText("距离您约2500km内");
            }
        }
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
        private TextView locationRange;
    }
}
