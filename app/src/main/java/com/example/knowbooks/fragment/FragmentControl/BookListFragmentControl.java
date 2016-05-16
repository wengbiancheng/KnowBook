package com.example.knowbooks.fragment.FragmentControl;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.knowbooks.fragment.BookListSonFragment.HotFragment;
import com.example.knowbooks.fragment.BookListSonFragment.NewFragment;
import com.example.knowbooks.fragment.BookListSonFragment.WeekFragment;
import com.example.knowbooks.fragment.BookListSonFragment.WorthFragment;

import java.util.ArrayList;

/**
 * Created by qq on 2016/4/20.
 */
public class BookListFragmentControl {
    private int containerId;
    private FragmentManager fm;
    private ArrayList<Fragment> fragmentList;

    private static BookListFragmentControl controller;

    public static BookListFragmentControl getInstance(){
        if(controller==null){
            controller=new BookListFragmentControl();
        }
        return controller;
    }

    public static void onDestroy() {
        controller = null;
    }


    private BookListFragmentControl(){
        initFragment();
    }

    //初始化fragment
    private void initFragment(){
        fragmentList=new ArrayList<Fragment>();

        fragmentList.add(new WeekFragment());
        fragmentList.add(new WorthFragment());
        fragmentList.add(new NewFragment());
        fragmentList.add(new HotFragment());
    }

    public Fragment getFragment(int position){
        return fragmentList.get(position);
    }


}
