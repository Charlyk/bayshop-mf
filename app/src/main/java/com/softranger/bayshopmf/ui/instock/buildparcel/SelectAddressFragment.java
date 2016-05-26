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

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.SecondStepAdapter;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.AddressGroup;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.ColorGroupSectionTitleIndicator;
import com.softranger.bayshopmf.util.Constants;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectAddressFragment extends Fragment implements SecondStepAdapter.OnAddressClickListener {

    private MainActivity mActivity;
    private SecondStepAdapter mAdapter;
    private ColorGroupSectionTitleIndicator mIndicator;


    public SelectAddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_build_parcel_address, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        mIndicator = (ColorGroupSectionTitleIndicator) view.findViewById(R.id.buildSecondStepFastScrollerSectionIndicator);
        mActivity.setToolbarTitle(getString(R.string.addresses_list), true);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.buildSecondStepList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new SecondStepAdapter(buildTestAddresses());
        mAdapter.setOnAddressClickListener(this);
        recyclerView.setAdapter(mAdapter);
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.buildSecondStepFastScroller);
        fastScroller.setRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(mIndicator);
        return view;
    }

    private ArrayList<Address> buildTestAddresses() {
        ArrayList<Address> addresses = new ArrayList<>();
        for (char anAlphabet : Constants.ALPHABET) {
            Address address = new Address.Builder()
                    .city("Chisinau")
                    .postalCode("2069")
                    .country("Moldova")
                    .buildingNumber("15")
                    .street("Alecu Russo")
                    .clientName(anAlphabet + "ana vasilevna")
                    .build();
            addresses.add(address);
        }
        return addresses;
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.addresses_list), true);
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
    public void onAddressClick(Address address, int position) {
        mActivity.addFragment(new ShippingMethodFragment(), true);
    }
}
