package com.example.knowbooks.fragment.FragmentControl;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.knowbooks.fragment.WriteFragment.WriteWantBookFragment1;
import com.example.knowbooks.fragment.WriteFragment.WriteWantBookFragment2;
import com.example.knowbooks.fragment.WriteFragment.WriteWantBookFragment3;
import com.example.knowbooks.fragment.WriteFragment.WriteWantBookFragment4;

import java.util.ArrayList;

/**
 * Created by qq on 2016/4/3.
 * 这个是因为BookActivity.finish()的时候只是将Activity移出了栈，没有调用OnDestory()，占用资源也没有被及时释放
 */
public class WriteWantAtyFragmentController {

    private int containerId;
    private FragmentManager fm;
    private ArrayList<Fragment> fragmentList;

    private static WriteWantAtyFragmentController controller;

    public static WriteWantAtyFragmentController getInstance(FragmentActivity activity,int containerId){
        if(controller==null){
            controller=new WriteWantAtyFragmentController(activity,containerId);
        }
        return controller;
    }

    public static void onDestroy() {
        controller = null;
    }


    private WriteWantAtyFragmentController(FragmentActivity activity, int containerId){
        this.containerId=containerId;
        fm=activity.getSupportFragmentManager();
        initFragment();
    }


    //初始化fragment
    private void initFragment(){
        fragmentList=new ArrayList<Fragment>();

        fragmentList.add(new WriteWantBookFragment1());
        fragmentList.add(new WriteWantBookFragment2());
        fragmentList.add(new WriteWantBookFragment3());
        fragmentList.add(new WriteWantBookFragment4());

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
