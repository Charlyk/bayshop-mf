package com.softranger.bayshopmf.ui.arrival;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.StorageTabAdapter;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.ui.instock.StorageItemsFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class WaitingArrivalFragment extends Fragment {

    private MainActivity mActivity;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    public WaitingArrivalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storages_holder, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.storages_viewPager);
        mTabLayout = (TabLayout) view.findViewById(R.id.storages_tabLayout);
        mActivity = (MainActivity) getActivity();
        initializeTabs();
        return view;
    }

    private void initializeTabs() {
        StorageTabAdapter adapter = new StorageTabAdapter(mActivity, mActivity.getSupportFragmentManager());
        adapter.addFragment(StorageItemsFragment.newInstance(getItems("DE")), "DE");
        adapter.addFragment(StorageItemsFragment.newInstance(getItems("UK")), "UK");
        adapter.addFragment(StorageItemsFragment.newInstance(getItems("USA")), "USA");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        invalidateTabs(mViewPager.getCurrentItem());
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
                mTabLayout.getTabAt(2).setIcon(R.mipmap.ic_de_flag_inactive);
                mTabLayout.getTabAt(1).setIcon(R.mipmap.ic_uk_flag_inactive);
                mTabLayout.getTabAt(position).setIcon(R.mipmap.ic_usa_flag);
                break;
            case 1:
                mTabLayout.getTabAt(2).setIcon(R.mipmap.ic_de_flag_inactive);
                mTabLayout.getTabAt(position).setIcon(R.mipmap.ic_uk_flag);
                mTabLayout.getTabAt(0).setIcon(R.mipmap.ic_usa_flag_inactive);
                break;
            case 2:
                mTabLayout.getTabAt(position).setIcon(R.mipmap.ic_de_flag);
                mTabLayout.getTabAt(1).setIcon(R.mipmap.ic_uk_flag_inactive);
                mTabLayout.getTabAt(0).setIcon(R.mipmap.ic_usa_flag_inactive);
                break;
        }
    }

    private ArrayList<InStockItem> getItems(String deposit) {
        ArrayList<InStockItem> inStockItems = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            InStockItem inStockItem = new InStockItem.Builder()
                    .name("MF7529416 (Smartphone neidentificat)")
                    .trackingNumber("213234723461237412")
                    .deposit(deposit)
                    .build();
            inStockItems.add(inStockItem);
        }
        return inStockItems;
    }
}
