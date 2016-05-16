package com.example.knowbooks.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.knowbooks.BaseFragment;
import com.example.knowbooks.R;
import com.example.knowbooks.adapter.TabAdapter;


/**
 * Created by qq on 2016/4/19.
 */
public class BookListFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup rg_tab = null;
    private ViewPager viewPager;
    private TabAdapter adapter;

    private RadioButton Week,New,Hot,Worth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_booklist,null);
        rg_tab= (RadioGroup) view.findViewById(R.id.booklist_rg_tab);
        viewPager= (ViewPager) view.findViewById(R.id.booklist_viewPager);
        adapter=new TabAdapter(Baseactivity.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        rg_tab.setOnCheckedChangeListener(this);

        Week= (RadioButton) view.findViewById(R.id.booklist_thisweek);
        New= (RadioButton) view.findViewById(R.id.booklist_newchoose);
        Hot= (RadioButton) view.findViewById(R.id.booklist_hotchoose);
        Worth= (RadioButton) view.findViewById(R.id.booklist_worthchoose);

        viewPager.setCurrentItem(0);
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.booklist_thisweek:
                viewPager.setCurrentItem(0);
//                Week.setChecked(true);
//                New.setChecked(false);
//                Hot.setChecked(false);
//                Worth.setChecked(false);
            break;
            case R.id.booklist_worthchoose:
                viewPager.setCurrentItem(1);
//                Week.setChecked(false);
//                New.setChecked(false);
//                Hot.setChecked(false);
//                Worth.setChecked(true);
                break;
            case R.id.booklist_newchoose:
                viewPager.setCurrentItem(2);
//                Week.setChecked(false);
//                New.setChecked(true);
//                Hot.setChecked(false);
//                Worth.setChecked(false);
                break;
            case R.id.booklist_hotchoose:
                viewPager.setCurrentItem(3);
//                Week.setChecked(false);
//                New.setChecked(false);
//                Hot.setChecked(true);
//                Worth.setChecked(false);
                break;
        }
    }
}
