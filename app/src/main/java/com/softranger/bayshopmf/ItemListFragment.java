package com.softranger.bayshopmf;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemListFragment extends Fragment {

    private ItemAdapter.OnItemClickListener mOnItemClickListener;
    private MainActivity mActivity;

    public ItemListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnItemClickListener = (ItemAdapter.OnItemClickListener) context;
        } catch (Exception e) {
            throw new RuntimeException(context.toString() + " must implement ItemAdapter.OnItemClickListener");
        }
        mActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.main_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        ItemAdapter adapter = new ItemAdapter(getItems(), mActivity);
        adapter.setOnItemClickListener(mOnItemClickListener);
        recyclerView.setAdapter(adapter);
        mActivity = (MainActivity) getActivity();
        mActivity.registerReceiver(mBroadcastReceiver, new IntentFilter(Application.TOKEN_READY));
        return view;
    }

    private Handler mStorageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ApiClient.getInstance().sendRequest(Constants.Api.getStorageUrl(), mStorageHandler);
        }
    };

    private ArrayList<Item> getItems() {
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Item item = new Item();
            items.add(item);
        }
        return items;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
