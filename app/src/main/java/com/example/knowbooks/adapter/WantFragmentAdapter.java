package com.example.knowbooks.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knowbooks.entity.response.WantBook;
import com.example.knowbooks.R;
import com.example.knowbooks.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq on 2016/4/25.
 */
public class WantFragmentAdapter extends BaseAdapter {


    private List<WantBook> list=new ArrayList<>();
    private Context context;
    public WantFragmentAdapter(Context context,List<WantBook> list){
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

        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=View.inflate(context, R.layout.item_fragment_want,null);
            viewHolder.imageView= (ImageView) convertView.findViewById(R.id.item_want_bookImage);
            viewHolder.bookName= (TextView) convertView.findViewById(R.id.item_want_bookName);
            viewHolder.bookAuthor= (TextView) convertView.findViewById(R.id.item_want_bookAuthor);
            viewHolder.Type= (TextView) convertView.findViewById(R.id.item_want_type);
            viewHolder.time= (TextView) convertView.findViewById(R.id.item_want_time);
            viewHolder.userName= (TextView) convertView.findViewById(R.id.item_want_username);
            viewHolder.repay= (TextView) convertView.findViewById(R.id.item_want_repay);
            viewHolder.userSex= (ImageView) convertView.findViewById(R.id.item_want_userSex);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

//        viewHolder.imageView
        viewHolder.bookName.setText(list.get(position).getWantBookName());
        viewHolder.bookAuthor.setText(list.get(position).getWantBookAuthor());
        viewHolder.Type.setText(list.get(position).getBookClass());
        viewHolder.time.setText(DateUtils.getShortTime(list.get(position).getCreateDate()));
        viewHolder.userName.setText(list.get(position).getUserName());
        viewHolder.repay.setText(list.get(position).getWishPay());
        if(list.get(position).getUserSex().equals("男")){
            viewHolder.userSex.setImageResource(R.mipmap.man);
        }else{

        }

        return convertView;
    }
    public class ViewHolder{
        private ImageView imageView;
        private TextView bookName;
        private TextView bookAuthor;
        private TextView Type;
        private TextView time;
        private TextView userName;
        private ImageView userSex;
        private TextView repay;
    }
}
