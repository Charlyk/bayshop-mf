package com.softranger.bayshopmf.ui.storages;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.StorageTabAdapter;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class StorageHolderFragment extends Fragment {

    private MainActivity mActivity;
    private ViewPager mViewPager;
//    private TabLayout mTabLayout;

    public StorageHolderFragment() {
        // Required empty public constructor
    }

    public static StorageHolderFragment newInstance() {
        Bundle args = new Bundle();
        StorageHolderFragment fragment = new StorageHolderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_storages_holder, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.storages_viewPager);
//        mTabLayout = (TabLayout) view.findViewById(R.id.storages_tabLayout);
        mActivity = (MainActivity) getActivity();
        switch (MainActivity.selectedFragment) {
            case IN_STOCK:
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlInStockItems(Constants.US), Constants.US));
//                        StorageItemsFragment.newInstance(Constants.Api.urlInStockItems(Constants.GB), Constants.GB),
//                        StorageItemsFragment.newInstance(Constants.Api.urlInStockItems(Constants.DE), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.warehouse_usa), true);
                break;
            case AWAITING_ARRIVAL:
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlWaitingArrival(Constants.US), Constants.US));
//                        StorageItemsFragment.newInstance(Constants.Api.urlWaitingArrival(Constants.GB), Constants.GB),
//                        StorageItemsFragment.newInstance(Constants.Api.urlWaitingArrival(Constants.DE), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.awaiting_arrival), true);
                break;
            case IN_PROCESSING:
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.IN_PROCESSING), Constants.US));
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.IN_PROCESSING), Constants.GB),
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.IN_PROCESSING), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.in_processing), true);
                break;
            case IN_FORMING: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.LIVE), Constants.US));
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.LIVE), Constants.GB),
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.LIVE), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.in_forming), true);
                break;
            }
            case AWAITING_SENDING: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.PACKED), Constants.US));
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.PACKED), Constants.GB),
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.PACKED), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.awaiting_sending), true);
                break;
            }
            case SENT: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.SENT), Constants.US));
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.SENT), Constants.GB),
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.SENT), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.sent), true);
                break;
            }
            case RECEIVED: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.RECEIVED), Constants.US));
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.RECEIVED), Constants.GB),
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.RECEIVED), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.received), true);
                break;
            }
            case LOCAL_DEPO: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.LOCAL_DEPO), Constants.US));
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.LOCAL_DEPO), Constants.GB),
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.LOCAL_DEPO), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.local_deposit), true);
                break;
            }
            case TAKEN_TO_DELIVERY: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.TAKEN_TO_DELIVERY), Constants.US));
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.TAKEN_TO_DELIVERY), Constants.GB),
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.TAKEN_TO_DELIVERY), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.take_to_delivery), true);
                break;
            }
            case CUSTOMS_HELD: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.CUSTOMS_HELD), Constants.US));
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.GB, Constants.ParcelStatus.CUSTOMS_HELD), Constants.GB),
//                        StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.DE, Constants.ParcelStatus.CUSTOMS_HELD), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.held_by_customs), true);
                break;
            }
            case HELD_BY_PROHIBITION: {
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.HELD_BY_PROHIBITION), Constants.US));
                mActivity.setToolbarTitle(getString(R.string.held_by_prohibition), true);
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
//        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
//        invalidateTabs(mViewPager.getCurrentItem());
//        mViewPager.setOffscreenPageLimit(3);
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                invalidateTabs(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
    }

//    private void invalidateTabs(int position) {
//        switch (position) {
//            case 0:
//                mTabLayout.getTabAt(2).setIcon(R.mipmap.ic_de_flag_inactive);
//                mTabLayout.getTabAt(1).setIcon(R.mipmap.ic_uk_flag_inactive);
//                mTabLayout.getTabAt(position).setIcon(R.mipmap.ic_usa_flag);
//                break;
//            case 1:
//                mTabLayout.getTabAt(2).setIcon(R.mipmap.ic_de_flag_inactive);
//                mTabLayout.getTabAt(position).setIcon(R.mipmap.ic_uk_flag);
//                mTabLayout.getTabAt(0).setIcon(R.mipmap.ic_usa_flag_inactive);
//                break;
//            case 2:
//                mTabLayout.getTabAt(position).setIcon(R.mipmap.ic_de_flag);
//                mTabLayout.getTabAt(1).setIcon(R.mipmap.ic_uk_flag_inactive);
//                mTabLayout.getTabAt(0).setIcon(R.mipmap.ic_usa_flag_inactive);
//                break;
//        }
//    }
}
