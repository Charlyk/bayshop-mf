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
import com.softranger.bayshopmf.model.Item;
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

    public static StorageItemsFragment newInstance(ArrayList<Item> items) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("items", items);
        StorageItemsFragment fragment = new StorageItemsFragment();
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.storage_itemsList);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(manager);
        ArrayList<Item> items = getArguments().getParcelableArrayList("items");
        ItemAdapter adapter = new ItemAdapter(items, mActivity);
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
    public void onRowClick(Item item, int position) {
        FragmentManager fragmentManager = mActivity.getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out);
        transaction.add(R.id.fragment_container, DetailsFragment.newInstance(item));
        transaction.addToBackStack("DetailsFragment");
        transaction.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.mDrawerToggle.setDrawerIndicatorEnabled(false);
                mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                mActivity.getSupportActionBar().setHomeButtonEnabled(true);
                mActivity.mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.onBackPressed();
                    }
                });
                mActivity.mDrawerToggle.syncState();

            }
        }, 300);
    }

    @Override
    public void onIconClick(Item item, boolean isSelected, int position) {

    }

    @Override
    public void onLongClick(Item item, boolean isSelected, int position) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
