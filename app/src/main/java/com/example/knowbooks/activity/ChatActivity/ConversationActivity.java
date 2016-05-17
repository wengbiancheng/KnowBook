package com.example.knowbooks.activity.ChatActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.knowbooks.R;

import io.rong.imlib.model.Conversation;

/**
 * Created by Bob on 15/8/18.
 * 会话页面
 */
public class ConversationActivity extends FragmentActivity {

    private TextView mTitle;
    private RelativeLayout mBack;

    private String mTargetId;

    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */
    private String mTargetIds;

    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
    }
}
