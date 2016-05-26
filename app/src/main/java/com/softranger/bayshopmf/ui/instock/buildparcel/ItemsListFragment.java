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

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.FirstStepAdapter;
import com.softranger.bayshopmf.model.InStockItem;
import com.softranger.bayshopmf.ui.MainActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsListFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private MainActivity mActivity;
    private FirstStepAdapter mAdapter;

    public ItemsListFragment() {
        // Required empty public constructor
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
        mAdapter = new FirstStepAdapter(new ArrayList<InStockItem>());
        recyclerView.setAdapter(mAdapter);

        Button nextButton = (Button) view.findViewById(R.id.buildFirstStepNextButton);
        nextButton.setOnClickListener(this);

        CheckBox hasBattery = (CheckBox) view.findViewById(R.id.buildFirstStepHasBattery);
        hasBattery.setOnCheckedChangeListener(this);

        String listItems = getString(R.string.list_items);
        mActivity.setToolbarTitle(listItems, true);

        return view;
    }

    @Override
    public void onClick(View v) {
        mActivity.addFragment(new SelectAddressFragment(), true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
}
