package com.softranger.bayshopmf.ui.instock;


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
import com.softranger.bayshopmf.model.InProcessingParcel;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoragesHolderFragment extends Fragment {

    private MainActivity mActivity;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private static String listToShow;

    public StoragesHolderFragment() {
        // Required empty public constructor
    }

    public static StoragesHolderFragment newInstance(@NonNull String listToShow) {
        Bundle args = new Bundle();
        args.putString("list to show", listToShow);
        StoragesHolderFragment fragment = new StoragesHolderFragment();
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
                initializeTabs(StorageItemsFragment.newInstance(getItems(Constants.USA), Constants.USA),
                        StorageItemsFragment.newInstance(getItems(Constants.UK), Constants.UK),
                        StorageItemsFragment.newInstance(getItems(Constants.DE), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.in_stock), true);
                break;
            case Constants.ListToShow.AWAITING_ARRIVAL:
                initializeTabs(StorageItemsFragment.newInstance(getAwaitingProducts(Constants.USA), Constants.USA),
                        StorageItemsFragment.newInstance(getAwaitingProducts(Constants.UK), Constants.UK),
                        StorageItemsFragment.newInstance(getAwaitingProducts(Constants.DE), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.awaiting_arrival), true);
                break;
            case Constants.ListToShow.IN_PROCESSING:
                initializeTabs(StorageItemsFragment.newInstance(getProcessingProducts(Constants.USA), Constants.USA),
                        StorageItemsFragment.newInstance(getProcessingProducts(Constants.UK), Constants.UK),
                        StorageItemsFragment.newInstance(getProcessingProducts(Constants.DE), Constants.DE));
                mActivity.setToolbarTitle(mActivity.getString(R.string.in_processing), true);
                break;
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

    private ArrayList<InProcessingParcel> getProcessingProducts(String deposit) {
        ArrayList<InProcessingParcel> processingProducts = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            InProcessingParcel product = (InProcessingParcel) new InProcessingParcel.Builder()
                    .processingProgress(i + 1)
                    .productName("Laptop din SUA")
                    .parcelId("PUS213342432423")
                    .deposit(deposit)
                    .weight("3.2kg")
                    .createdDate("23 Jan 2016, 10:35")
                    .build();
            processingProducts.add(product);
        }
        return processingProducts;
    }

    private ArrayList<Product> getAwaitingProducts(String deposit) {
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Product product = new Product.Builder()
                    .productName("Telefon ciotcos")
                    .productId("MF123123124")
                    .productPrice("200$")
                    .productQuantity("1")
                    .date("12 May 2016")
                    .productUrl("http://ebay.com")
                    .trackingNumber("213124382127312")
                    .deposit(deposit)
                    .build();
            products.add(product);
        }
        return products;
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
