package com.softranger.bayshopmf.ui.settings;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ShippingMethodAdapter;
import com.softranger.bayshopmf.model.Shipper;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShippersFragment extends ParentFragment implements ShippingMethodAdapter.OnShippingClickListener {

    public static final String SHIPPER_SELECTED = "com.softranger.bayshopmf.ui.settings.SHIPPER_SELECTED";
    private Unbinder mUnbinder;
    private SettingsActivity mActivity;
    private Call<ServerResponse<ArrayList<Shipper>>> mCall;
    private ShippingMethodAdapter mAdapter;

    @BindView(R.id.autopackShippersList)
    RecyclerView mRecyclerView;

    public ShippersFragment() {
        // Required empty public constructor
    }

    public static ShippersFragment newInstance() {
        Bundle args = new Bundle();
        ShippersFragment fragment = new ShippersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shippers, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (SettingsActivity) getActivity();
        mActivity.toggleLoadingProgress(true);
        mCall = Application.apiInterface().getMemberShippers();
        mCall.enqueue(mResponseCallback);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        return view;
    }

    private ResponseCallback<ArrayList<Shipper>> mResponseCallback = new ResponseCallback<ArrayList<Shipper>>() {
        @Override
        public void onSuccess(ArrayList<Shipper> data) {
            mAdapter = new ShippingMethodAdapter(data, "");
            mAdapter.setOnShippingClickListener(ShippersFragment.this);
            mAdapter.setShowPrice(false);
            mRecyclerView.setAdapter(mAdapter);
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<ArrayList<Shipper>>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public String getFragmentTitle() {
        return getString(R.string.shipping_method);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.shipping_method;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
    }

    @Override
    public void onDetailsClick(Shipper shippingMethod, int position, ImageButton detailsButton) {

    }

    @Override
    public void onSelectClick(Shipper shippingMethod, int position) {
        Intent selectShipper = new Intent(SHIPPER_SELECTED);
        selectShipper.putExtra("shipper", shippingMethod);
        mActivity.sendBroadcast(selectShipper);
        mActivity.onBackPressed();
    }
}
