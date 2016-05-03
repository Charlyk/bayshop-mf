package com.softranger.bayshopmf.ui.instock;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.StorageTabAdapter;
import com.softranger.bayshopmf.model.Item;
import com.softranger.bayshopmf.ui.MainActivity;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoragesHolderFragment extends Fragment {

    private MainActivity mActivity;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    public StoragesHolderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_storages_holder, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.storages_viewPager);
        mTabLayout = (TabLayout) view.findViewById(R.id.storages_tabLayout);
        initializeTabs();
        return view;
    }

    private void initializeTabs() {
        StorageTabAdapter adapter = new StorageTabAdapter(mActivity, mActivity.getSupportFragmentManager());
        adapter.addFragment(StorageItemsFragment.newInstance(getItems()), "DE");
        adapter.addFragment(StorageItemsFragment.newInstance(getItems()), "UK");
        adapter.addFragment(StorageItemsFragment.newInstance(getItems()), "USA");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.mipmap.ic_de_flag);
        mTabLayout.getTabAt(1).setIcon(R.mipmap.ic_uk_flag_inactive);
        mTabLayout.getTabAt(2).setIcon(R.mipmap.ic_usa_flag_inactive);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                invalidateTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void invalidateTabs(int position) {
        switch (position) {
            case 0:
                mTabLayout.getTabAt(position).setIcon(R.mipmap.ic_de_flag);
                mTabLayout.getTabAt(1).setIcon(R.mipmap.ic_uk_flag_inactive);
                mTabLayout.getTabAt(2).setIcon(R.mipmap.ic_usa_flag_inactive);
                break;
            case 1:
                mTabLayout.getTabAt(0).setIcon(R.mipmap.ic_de_flag_inactive);
                mTabLayout.getTabAt(position).setIcon(R.mipmap.ic_uk_flag);
                mTabLayout.getTabAt(2).setIcon(R.mipmap.ic_usa_flag_inactive);
                break;
            case 2:
                mTabLayout.getTabAt(0).setIcon(R.mipmap.ic_de_flag_inactive);
                mTabLayout.getTabAt(1).setIcon(R.mipmap.ic_uk_flag_inactive);
                mTabLayout.getTabAt(position).setIcon(R.mipmap.ic_usa_flag);
                break;
        }
    }

    private ArrayList<Item> getItems() {
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Item item = new Item();
            items.add(item);
        }
        return items;
    }
}
