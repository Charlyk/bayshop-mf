package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.SecondStepAdapter;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.AddressGroup;
import com.softranger.bayshopmf.model.InForming;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.ColorGroupSectionTitleIndicator;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectAddressFragment extends Fragment implements SecondStepAdapter.OnAddressClickListener {

    private static final String IN_FORMING_ARG = "in forming argument";
    private MainActivity mActivity;
    private SecondStepAdapter mAdapter;
    private ColorGroupSectionTitleIndicator mIndicator;
    private RecyclerView mRecyclerView;
    private InForming mInForming;
    private ArrayList<Address> mAddresses;


    public SelectAddressFragment() {
        // Required empty public constructor
    }

    public static SelectAddressFragment newInstance(InForming inForming) {
        Bundle args = new Bundle();
        args.putParcelable(IN_FORMING_ARG, inForming);
        SelectAddressFragment fragment = new SelectAddressFragment();
        fragment.setArguments(args);
        return fragment;
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.buildSecondStepList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAddresses = new ArrayList<>();
        mAdapter = new SecondStepAdapter(mAddresses);
        mAdapter.setOnAddressClickListener(this);
        mInForming = getArguments().getParcelable(IN_FORMING_ARG);
        mRecyclerView.setAdapter(mAdapter);
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.buildSecondStepFastScroller);
        fastScroller.setRecyclerView(mRecyclerView);
        mRecyclerView.setOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(mIndicator);
        RequestBody body = new FormBody.Builder()
                .add("isBatteryLionExists", String.valueOf(mInForming.isHasBattery()))
                .build();
        ApiClient.getInstance().sendRequest(body, Constants.Api.urlBuildStep(2, String.valueOf(mInForming.getId())), mAddressHandler);
        return view;
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

    private Handler mAddressHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray addressesJSON = data.getJSONArray("addresses");
                            for (int i = 0; i < addressesJSON.length(); i++) {
                                JSONObject a = addressesJSON.getJSONObject(i);
                                String name = a.getString("shipping_first_name") + " " + a.getString("shipping_last_name");
                                Address address = new Address.Builder()
                                        .id(a.getInt("id"))
                                        .clientName(name)
                                        .street(a.getString("shipping_address"))
                                        .city(a.getString("shipping_city"))
                                        .country(a.getString("countryTitle"))
                                        .postalCode(a.getString("shipping_zip"))
                                        .phoneNumber(a.getString("phone"))
                                        .build();
                                mAddresses.add(address);
                            }
                            mAdapter.refreshList();
                        } else {
                            Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
            }
            mActivity.toggleLoadingProgress(false);
        }
    };
}
