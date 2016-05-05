package com.softranger.bayshopmf.ui.instock;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ItemAdapter;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StorageItemsFragment extends Fragment implements ItemAdapter.OnItemClickListener {

    private MainActivity mActivity;

    public StorageItemsFragment() {
        // Required empty public constructor
    }

    public static StorageItemsFragment newInstance(ArrayList<InStockItem> inStockItems) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("inStockItems", inStockItems);
        StorageItemsFragment fragment = new StorageItemsFragment();
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
        ArrayList<InStockItem> inStockItems = getArguments().getParcelableArrayList("inStockItems");
        ItemAdapter adapter = new ItemAdapter(inStockItems, mActivity);
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
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
