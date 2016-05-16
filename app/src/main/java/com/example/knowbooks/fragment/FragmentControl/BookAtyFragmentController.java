package com.example.knowbooks.fragment.FragmentControl;

import android.support.v4.app.*;

import com.example.knowbooks.fragment.BookListFragment;
import com.example.knowbooks.fragment.BuyFragment;
import com.example.knowbooks.fragment.WantFragment;
import com.example.knowbooks.fragment.ShowFragment;

import java.util.ArrayList;

/**
 * Created by qq on 2016/4/3.
 */
public class BookAtyFragmentController {

    private int containerId;
    private FragmentManager fm;
    private ArrayList<Fragment> fragmentList;

    private static BookAtyFragmentController controller;

    public static BookAtyFragmentController getInstance(FragmentActivity activity,int containerId){
        if(controller==null){
            controller=new BookAtyFragmentController(activity,containerId);
        }
        return controller;
    }

    public static void onDestroy() {
        controller = null;
    }


    private BookAtyFragmentController(FragmentActivity activity, int containerId){
        this.containerId=containerId;
        fm=activity.getSupportFragmentManager();
//        if(activity instanceof BookActivity){
//            initFragment();
//        }else if(activity instanceof WriteBookAty){
//            initFragment1();
//        }
        initFragment();
    }

    //初始化fragment
    private void initFragment(){
        fragmentList=new ArrayList<Fragment>();

        fragmentList.add(new ShowFragment());
        fragmentList.add(new BookListFragment());
        fragmentList.add(new BuyFragment());
        fragmentList.add(new WantFragment());

        FragmentTransaction ft=fm.beginTransaction();
        for(Fragment fragment:fragmentList){
            ft.add(containerId,fragment);
        }

        ft.commit();
    }
    //初始化fragment
//    private void initFragment1(){
//        fragmentList=new ArrayList<Fragment>();
//
//        fragmentList.add(new WriteBookFragment1());
//        fragmentList.add(new WriteBookFragment2());
//        fragmentList.add(new WriteBookFragment3());
//        fragmentList.add(new WriteBookFragment4());
//
//        FragmentTransaction ft=fm.beginTransaction();
//        for(Fragment fragment:fragmentList){
//            ft.add(containerId,fragment);
//        }
//
//        ft.commit();
//    }

    public Fragment getFragment(int position){
        return fragmentList.get(position);
    }

    public int getPosition(Fragment fragment){
        for(int i=0;i<fragmentList.size();i++){
            if(fragment==fragmentList.get(i)){
                return i;
            }
        }
        return -1;
    }

    public void showFragment(int position){
        hideFragments();
        Fragment fragment=fragmentList.get(position);
        FragmentTransaction ft=fm.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    public void hideFragments(){
        FragmentTransaction ft=fm.beginTransaction();
        for(Fragment fragment:fragmentList){
            if(fragment!=null){
                ft.hide(fragment);
            }
        }
        ft.commit();
    }
}
