package com.example.knowbooks.adapter;

/**
 * Created by Me on 2015/11/25.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.knowbooks.fragment.FragmentControl.BookListFragmentControl;

public class TabAdapter extends FragmentPagerAdapter {

    public static final String[] TITLES = new String[] {"本地","精选","最新","最热"};

    public TabAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int arg0){
        int position=arg0%TITLES.length;
        return BookListFragmentControl.getInstance().getFragment(position);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return TITLES[position % TITLES.length];
    }

    @Override
    public int getCount()
    {
        return TITLES.length;
    }

}