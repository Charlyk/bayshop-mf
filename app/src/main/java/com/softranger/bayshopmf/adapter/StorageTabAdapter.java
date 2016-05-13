package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.softranger.bayshopmf.ui.general.StorageItemsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eduard Albu on 5/3/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class StorageTabAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private FragmentManager mFragmentManager;
    private Context mContext;

    public StorageTabAdapter(Context context, FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mContext = context;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof StorageItemsFragment) {
            mFragmentManager.beginTransaction().remove((Fragment) object).commit();
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(mContext, mFragmentList.get(position).getClass().getName(),
                mFragmentList.get(position).getArguments());
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }



    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mTitles.add(title);
        notifyDataSetChanged();
    }

    public void clearLists() {
        mFragmentList.clear();
        mTitles.clear();
    }
}
