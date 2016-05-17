package com.example.knowbooks.activity.ChatActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.knowbooks.R;


/**
 * Created by Bob on 15/8/18.
 * 会话列表
 */
public class ConversationListActivity extends FragmentActivity {

    private TextView mTitle;
    private RelativeLayout mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversationlist);
    }
}

