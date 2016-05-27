package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.FirstStepAdapter;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.ui.general.StorageItemsFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsListFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        FirstStepAdapter.OnItemClickListener {

    private static final String IN_STOCK_ARG = "in stock items argument";
    private MainActivity mActivity;
    private FirstStepAdapter mAdapter;
    private TextView mTotalWeight;
    private TextView mTotalPrice;
    private ArrayList<InStockItem> mInStockItems;

    public ItemsListFragment() {
        // Required empty public constructor
    }

    public static ItemsListFragment newInstance(ArrayList<InStockItem> inStockItems) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(IN_STOCK_ARG, inStockItems);
        ItemsListFragment fragment = new ItemsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_build_parcel_first_step, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.buildFirstStepItemsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mInStockItems = getArguments().getParcelableArrayList(IN_STOCK_ARG);
        if (mInStockItems == null) mInStockItems = new ArrayList<>();
        mAdapter = new FirstStepAdapter(mInStockItems);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);

        mTotalPrice = (TextView) view.findViewById(R.id.buildFirstFragmentTotalPriceLabel);
        mTotalWeight = (TextView) view.findViewById(R.id.buildFirstFragmentTotalWeightLabel);
        updateTotals();

        Button nextButton = (Button) view.findViewById(R.id.buildFirstStepNextButton);
        nextButton.setOnClickListener(this);

        CheckBox hasBattery = (CheckBox) view.findViewById(R.id.buildFirstStepHasBattery);
        hasBattery.setOnCheckedChangeListener(this);

        String listItems = getString(R.string.list_items);
        mActivity.setToolbarTitle(listItems, true);

        return view;
    }

    private int getTotalWeight(ArrayList<InStockItem> inStockItems) {
        int totalWeight = 0;
        for (InStockItem item : inStockItems) {
            totalWeight = totalWeight + 1;
        }
        return totalWeight;
    }

    private int getTotalPrice(ArrayList<InStockItem> inStockItems) {
        int totalPrice = 0;
        for (InStockItem inStockItem : inStockItems) {
            totalPrice = totalPrice + 40;
        }
        return totalPrice;
    }

    @Override
    public void onClick(View v) {
        mActivity.addFragment(new SelectAddressFragment(), true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    private void updateTotals() {
        mTotalPrice.setText(String.valueOf(getTotalPrice(mInStockItems)));
        mTotalWeight.setText(String.valueOf(getTotalWeight(mInStockItems)));
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.list_items), true);
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mTitleReceiver);
    }

    @Override
    public void onDeleteClick(InStockItem inStockItem, int position) {
        mAdapter.removeItem(position);
        if (mAdapter.getItemCount() == 0) {
            mActivity.onBackPressed();
        } else {
            updateTotals();
        }
    }
}
