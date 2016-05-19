package com.example.knowbooks.fragment.FragmentControl;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.knowbooks.fragment.WriteFragment.WriteBuyBookFragment1;
import com.example.knowbooks.fragment.WriteFragment.WriteBuyBookFragment2;
import com.example.knowbooks.fragment.WriteFragment.WriteBuyBookFragment3;
import com.example.knowbooks.fragment.WriteFragment.WriteBuyBookFragment4;

import java.util.ArrayList;

/**
 * Created by qq on 2016/4/3.
 * 这个是因为BookActivity.finish()的时候只是将Activity移出了栈，没有调用OnDestory()，占用资源也没有被及时释放
 */
public class WriteBuyAtyFragmentController {

    private int containerId;
    private FragmentManager fm;
    private ArrayList<Fragment> fragmentList;

    private static WriteBuyAtyFragmentController controller;

    public static WriteBuyAtyFragmentController getInstance(FragmentActivity activity,int containerId){
        if(controller==null){
            controller=new WriteBuyAtyFragmentController(activity,containerId);
        }
        return controller;
    }

    public static void onDestroy() {
        controller = null;
    }


    private WriteBuyAtyFragmentController(FragmentActivity activity, int containerId){
        this.containerId=containerId;
        fm=activity.getSupportFragmentManager();
        initFragment();
    }


    //初始化fragment
    private void initFragment(){
        fragmentList=new ArrayList<Fragment>();

        fragmentList.add(new WriteBuyBookFragment1());
        fragmentList.add(new WriteBuyBookFragment2());
        fragmentList.add(new WriteBuyBookFragment3());
        fragmentList.add(new WriteBuyBookFragment4());

        FragmentTransaction ft=fm.beginTransaction();
        for(Fragment fragment:fragmentList){
            ft.add(containerId,fragment);
        }

        ft.commit();
    }

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
