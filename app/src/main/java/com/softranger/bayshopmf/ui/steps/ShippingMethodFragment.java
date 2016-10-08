package com.softranger.bayshopmf.ui.steps;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.softranger.bayshopmf.ui.settings.SettingsFragment;
import com.softranger.bayshopmf.util.Application;
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
    private AlertDialog mAlertDialog;

    @BindView(R.id.shippingMethodRecyclerView) RecyclerView mRecyclerView;

    public ShippingMethodFragment() {
        // Required empty public constructor
    }

    public static ShippingMethodFragment newInstance(@NonNull CreationDetails creationDetails,
                                                     @NonNull String selectedAddressId) {
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
        if (mCreationDetails != null) {
            ArrayList<Shipper> shippingMethods = mCreationDetails.getShippers();
            mSelectedAddressId = getArguments().getString(SELECTED_ADDRESS_ID);
            if (mSelectedAddressId == null)
                throw new RuntimeException("Selected address id is null");
            if (Application.isAutopackaging() && canGoFurther(shippingMethods)) {
                Shipper shipper = shippingMethods.get(shippingMethods.indexOf(
                        new Shipper().setId(Application.getSelectedShipperId())));
                mActivity.replaceFragment(ConfirmationFragment.newInstance(mCreationDetails,
                        mSelectedAddressId, shipper.getId(), shipper.getCalculatedPrice()));
                mActivity.setToolbarTitle(getString(R.string.confirm));
            } else {
                ShippingMethodAdapter adapter = new ShippingMethodAdapter(shippingMethods, mCreationDetails.getCurrencySign());
                adapter.setOnShippingClickListener(this);

                mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                mRecyclerView.setAdapter(adapter);
            }
        } else {
            throw new RuntimeException("CreationDetails is null");
        }
        return view;
    }

    private boolean canGoFurther(ArrayList<Shipper> shippers) {
        return shippers.contains(new Shipper().setId(Application.getSelectedShipperId()));
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
        if (Application.isAutopackagingAddressSelected() && !Application.isAutopackagingShipperSelected()) {
            mAlertDialog = mActivity.getDialog(getString(R.string.save_shipper),
                    getString(R.string.save_selected_method), R.mipmap.ic_shipping_by_36dp,
                    getString(R.string.yes), (view) -> {
                        // save selected shipper to preferences
                        Application.autoPackPrefs.edit().putString(SettingsFragment.SHIPPING_ID,
                                shippingMethod.getId()).apply();
                        Application.autoPackPrefs.edit().putString(SettingsFragment.SHIPPING_NAME,
                                shippingMethod.getTitle()).apply();
                        // dismiss dialog and add confirmation fragment
                        mAlertDialog.dismiss();
                        mActivity.addFragment(ConfirmationFragment.newInstance(mCreationDetails, mSelectedAddressId,
                                shippingMethod.getId(), shippingMethod.getCalculatedPrice()), true);
                    }, getString(R.string.no), (view) -> {
                        // dismiss dialog and add confirmation fragment
                        mAlertDialog.dismiss();
                        mActivity.addFragment(ConfirmationFragment.newInstance(mCreationDetails, mSelectedAddressId,
                                shippingMethod.getId(), shippingMethod.getCalculatedPrice()), true);
                    }, R.color.colorAccent);
            mAlertDialog.show();
        } else {
            mActivity.addFragment(ConfirmationFragment.newInstance(mCreationDetails, mSelectedAddressId,
                    shippingMethod.getId(), shippingMethod.getCalculatedPrice()), true);
        }
    }
}
