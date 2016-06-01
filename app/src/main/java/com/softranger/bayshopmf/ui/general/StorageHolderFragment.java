package com.softranger.bayshopmf.ui.general;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.StorageTabAdapter;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class StorageHolderFragment extends Fragment {

    private MainActivity mActivity;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    public static String listToShow;

    public StorageHolderFragment() {
        // Required empty public constructor
    }

    public static StorageHolderFragment newInstance(@NonNull String listToShow) {
        Bundle args = new Bundle();
        args.putString("list to show", listToShow);
        StorageHolderFragment fragment = new StorageHolderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_storages_holder, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.storages_viewPager);
        mTabLayout = (TabLayout) view.findViewById(R.id.storages_tabLayout);
        mActivity = (MainActivity) getActivity();
        listToShow = getArguments().getString("list to show");
        switch (listToShow) {
            case Constants.ListToShow.IN_STOCK:
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlInStockItems(Constants.USA), Constants.USA),
                        StorageItemsFragment.newInstance(Constants.Api.urlInStockItems(Constants.UK), Constants.UK),
                        StorageItemsFragment.newInstance(Constants.Api.urlInStockItems(Constants.DE), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.in_stock), true);
                break;
            case Constants.ListToShow.AWAITING_ARRIVAL:
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlWaitingArrival(Constants.USA), Constants.USA),
                        StorageItemsFragment.newInstance(Constants.Api.urlWaitingArrival(Constants.UK), Constants.UK),
                        StorageItemsFragment.newInstance(Constants.Api.urlWaitingArrival(Constants.DE), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.awaiting_arrival), true);
                break;
            case Constants.ListToShow.IN_PROCESSING:
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.IN_PROCESSING), Constants.US),
                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.IN_PROCESSING), Constants.GB),
                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.IN_PROCESSING), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.in_processing), true);
                break;
            case Constants.ListToShow.IN_FORMING: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.LIVE), Constants.US),
                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.LIVE), Constants.GB),
                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.LIVE), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.in_forming), true);
                break;
            }
            case Constants.ListToShow.AWAITING_SENDING: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.PACKED), Constants.US),
                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.PACKED), Constants.GB),
                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.PACKED), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.awaiting_sending), true);
                break;
            }
            case Constants.ListToShow.SENT: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.SENT), Constants.US),
                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.SENT), Constants.GB),
                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.SENT), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.sent), true);
                break;
            }
            case Constants.ListToShow.RECEIVED: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.RECEIVED), Constants.US),
                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.RECEIVED), Constants.GB),
                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.RECEIVED), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.sent), true);
                break;
            }
        }
        return view;
    }

    private void initializeTabs(StorageItemsFragment... fragments) {
        StorageTabAdapter adapter = new StorageTabAdapter(mActivity, mActivity.getSupportFragmentManager());
        for (StorageItemsFragment fragment : fragments) {
            adapter.addFragment(fragment, fragment.mDeposit);
        }
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
}
