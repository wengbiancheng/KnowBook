package com.example.knowbooks;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.knowbooks.activity.BookActivity;

/**
 * Created by qq on 2016/4/19.
 */
public class BaseFragment extends Fragment{

    protected BookActivity Baseactivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Baseactivity= (BookActivity) getActivity();
    }

}
