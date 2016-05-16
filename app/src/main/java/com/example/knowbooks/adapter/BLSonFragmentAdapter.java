package com.example.knowbooks.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knowbooks.constants.UrlConstant;
import com.example.knowbooks.entity.response.BookList;
import com.example.knowbooks.R;
import com.example.knowbooks.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by qq on 2016/4/20.
 */
public class BLSonFragmentAdapter extends BaseAdapter {


    private Context context;
    private List<BookList> lists;
    private ImageLoader imageLoader;
    private Handler handler;

    public BLSonFragmentAdapter(Context mContext, List<BookList> lists,Handler handler) {
        this.context = mContext;
        this.lists = lists;
        this.imageLoader = ImageLoader.getInstance();
        this.handler=handler;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_sonfragment_listview, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.sonfragment_imageView);
            viewHolder.book_name = (TextView) convertView.findViewById(R.id.sonfragment_bookTitle);
            viewHolder.left_people = (TextView) convertView.findViewById(R.id.sonfragment_leftPeople);
            viewHolder.right_people = (TextView) convertView.findViewById(R.id.sonfragment_rightPeople);
            viewHolder.right_btn = (Button) convertView.findViewById(R.id.sonfragment_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(!TextUtils.isEmpty(lists.get(position).getBooklistPicture())){
            imageLoader.displayImage(UrlConstant.url + lists.get(position).getBooklistPicture(), viewHolder.imageView);
        }
        viewHolder.book_name.setText(lists.get(position).getBookListName());
        viewHolder.left_people.setText(lists.get(position).getPeopleCount()+"");
        viewHolder.right_people.setText(lists.get(position).getBookCount()+"");
        viewHolder.right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int flag=0;
                if (viewHolder.right_btn.getText().equals("点击收藏")) {
                    viewHolder.right_btn.setText("取消收藏");
                    flag=1;
                } else if (viewHolder.right_btn.getText().equals("取消收藏")) {
                    viewHolder.right_btn.setText("点击收藏");
                    flag=0;
                }

                SendToServlet(position,flag);
            }
        });
        int isCollect=lists.get(position).getIsCollect();
        if(isCollect==0){
            viewHolder.right_btn.setText("点击收藏");
        }else{
            viewHolder.right_btn.setText("取消收藏");
        }
        return convertView;
    }

    private void SendToServlet(int position, final int flag){
        final Long booklistid=lists.get(position).getId();
        String url="";

        RequestParams requestParams=new RequestParams();
        requestParams.put("booklistId",booklistid);
        if(flag==1){//进行收藏书单的操作
            url=UrlConstant.CollectBooklistUrl;
        }else if(flag==0){//进行取消收藏书单的操作
            url=UrlConstant.NoCollectBooklistUrl;
        }
        HttpUtil.getInstance(context).get(context,url,requestParams,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if(flag==1){
                    Log.i("Log1", "书单的id为：" + booklistid + "进行收藏书单的操作的结果是：" + response.toString());
                }else if(flag==0){
                    Log.i("Log1", "书单的id为：" + booklistid + "取消收藏书单的操作的结果是：" + response.toString());
                }
                Message message=new Message();
                message.what=400;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("Log1", "书单的id为：" + booklistid + "执行收藏或者取消收藏的结果是：" + responseString.toString());
            }
        });
    }

    public static class ViewHolder {
        ImageView imageView;
        TextView book_name;
        TextView left_people;
        TextView right_people;
        Button right_btn;
    }
}
