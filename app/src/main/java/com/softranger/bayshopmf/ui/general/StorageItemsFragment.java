package com.softranger.bayshopmf.ui.general;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ItemAdapter;
import com.softranger.bayshopmf.model.InProcessingParcel;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.awaitingarrival.AwaitingArrivalProductFragment;
import com.softranger.bayshopmf.ui.inprocessing.InProcessingDetails;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.ui.instock.DetailsFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StorageItemsFragment<T extends Parcelable> extends Fragment implements ItemAdapter.OnItemClickListener {

    private MainActivity mActivity;
    public String mDeposit;

    public StorageItemsFragment() {
        // Required empty public constructor
    }

    public static <T extends Parcelable> StorageItemsFragment newInstance(@NonNull ArrayList<T> items, @NonNull String deposit) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("items", items);
        StorageItemsFragment<T> fragment = new StorageItemsFragment<>();
        fragment.mDeposit = deposit;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        mActivity = (MainActivity) getActivity();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.storage_itemsList);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(manager);
        ArrayList<T> items = getArguments().getParcelableArrayList("items");
        ItemAdapter<T> adapter = new ItemAdapter<>(items, mActivity);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        mActivity.registerReceiver(mBroadcastReceiver, new IntentFilter(Application.TOKEN_READY));
        return view;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ApiClient.getInstance().sendRequest(Constants.Api.getStorageUrl(), mStorageHandler);
        }
    };

    private Handler mStorageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    @Override
    public void onRowClick(final InStockItem inStockItem, int position) {
        mActivity.addFragment(DetailsFragment.newInstance(inStockItem), true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.setToolbarTitle(inStockItem.getName(), true);
            }
        }, 300);
    }

    @Override
    public void onIconClick(InStockItem inStockItem, boolean isSelected, int position) {

    }

    @Override
    public void onProductClick(Product product, int position) {
        mActivity.addFragment(AwaitingArrivalProductFragment.newInstance(product), true);
    }

    @Override
    public void onInProcessingProductClick(InProcessingParcel inProcessingParcel, int position) {
        mActivity.addFragment(InProcessingDetails.newInstance(inProcessingParcel), true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
