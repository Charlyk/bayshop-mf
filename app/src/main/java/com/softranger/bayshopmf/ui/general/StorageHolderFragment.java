package com.softranger.bayshopmf.ui.general;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;


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
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.getInStockItems(Constants.USA), Constants.USA),
                        StorageItemsFragment.newInstance(Constants.Api.getInStockItems(Constants.UK), Constants.USA),
                        StorageItemsFragment.newInstance(Constants.Api.getInStockItems(Constants.DE), Constants.USA));
                mActivity.setToolbarTitle(mActivity.getString(R.string.in_stock), true);
                break;
            case Constants.ListToShow.AWAITING_ARRIVAL:
                initializeTabs(StorageItemsFragment.newInstance(Constants.Api.getWaitingMf(Constants.USA), Constants.USA),
                        StorageItemsFragment.newInstance(Constants.Api.getWaitingMf(Constants.UK), Constants.USA),
                        StorageItemsFragment.newInstance(Constants.Api.getWaitingMf(Constants.DE), Constants.USA));
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

    private Handler mResponseHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        boolean error = response.getBoolean("error");
                        if (!error) {
                            switch (listToShow) {
                                case Constants.ListToShow.IN_STOCK:

                                    break;
                                case Constants.ListToShow.AWAITING_ARRIVAL:

                                    break;
                                case Constants.ListToShow.IN_PROCESSING:

                                    break;
                            }
                        } else {
                            String message = response.optString("message", getString(R.string.unknown_error));
                            Snackbar.make(mViewPager, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mActivity.toggleLoadingProgress(false);
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mViewPager, message, Snackbar.LENGTH_SHORT).show();
                    mActivity.toggleLoadingProgress(false);
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    IOException exception = (IOException) msg.obj;
                    String message = exception.getMessage();
                    Snackbar.make(mViewPager, message, Snackbar.LENGTH_SHORT).show();
                    mActivity.toggleLoadingProgress(false);
                    break;
                }
                case Constants.ApiResponse.RESONSE_UNAUTHORIZED: {
                    mActivity.toggleLoadingProgress(false);
                    mActivity.logOut();
                }
            }
        }
    };

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
