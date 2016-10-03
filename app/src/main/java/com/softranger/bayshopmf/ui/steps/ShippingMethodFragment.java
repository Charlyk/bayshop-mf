package com.softranger.bayshopmf.ui.steps;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ShippingMethodAdapter;
import com.softranger.bayshopmf.model.CreationDetails;
import com.softranger.bayshopmf.model.Shipper;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShippingMethodFragment extends ParentFragment implements ShippingMethodAdapter.OnShippingClickListener {

    private static final String SHIPPING_METHODS = "SHIPPING METHODS ARGS";
    private static final String SELECTED_ADDRESS_ID = "ID FOR SELECTED ADDRESS";

    private Unbinder mUnbinder;
    private ParentActivity mActivity;
    private CreationDetails mCreationDetails;
    private String mSelectedAddressId;

    @BindView(R.id.shippingMethodRecyclerView) RecyclerView mRecyclerView;

    public ShippingMethodFragment() {
        // Required empty public constructor
    }

    public static ShippingMethodFragment newInstance(CreationDetails creationDetails,
                                                     String selectedAddressId) {
        Bundle args = new Bundle();
        args.putParcelable(SHIPPING_METHODS, creationDetails);
        args.putString(SELECTED_ADDRESS_ID, selectedAddressId);
        ShippingMethodFragment fragment = new ShippingMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shipping_method, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (ParentActivity) getActivity();
        mCreationDetails = getArguments().getParcelable(SHIPPING_METHODS);
        ArrayList<Shipper> shippingMethods = mCreationDetails.getShippers();
        ShippingMethodAdapter adapter = new ShippingMethodAdapter(shippingMethods, mCreationDetails.getCurrencySign());
        adapter.setOnShippingClickListener(this);
        mSelectedAddressId = getArguments().getString(SELECTED_ADDRESS_ID);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.shipping_method);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.shipping_method;
    }

    @Override
    public void onDetailsClick(Shipper shippingMethod, int position, ImageButton detailsButton) {

    }

    @Override
    public void onSelectClick(Shipper shippingMethod, int position) {
        mActivity.addFragment(ConfirmationFragment.newInstance(mCreationDetails, mSelectedAddressId,
                shippingMethod.getId(), shippingMethod.getCalculatedPrice()), true);
    }
}
