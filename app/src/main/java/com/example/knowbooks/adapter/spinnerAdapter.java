package com.example.knowbooks.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knowbooks.R;

/**
 * Created by xieyongxiong on 2016/4/28.
 */
public class spinnerAdapter extends BaseAdapter {
    private Context context;
    private String []array;

    public spinnerAdapter(Context mcontext,String []marray) {
        this.context = mcontext;
        array=marray;
    }
    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_spinner, null);
            viewHolder = new ViewHolder();
            viewHolder.item = (Button) convertView.findViewById(R.id.item);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.item.setText(array[position]);
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return convertView;
    }

    private static class ViewHolder {
        Button item;
    }
}
