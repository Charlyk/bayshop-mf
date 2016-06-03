package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.InForming;
import com.softranger.bayshopmf.ui.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmationFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String IN_FORMING_ARG = "in forming item argument";
    private MainActivity mActivity;
    private TextView mGoodsPrice;
    private TextView mDeliveryPrice;
    private TextView mInsurancePrice;
    private TextView mDeclarationPrice;
    private Button mTermsAndConditions;
    private Button mFinishAndSend;
    private CheckBox mAdditionalPackages;
    private CheckBox mLocalDelivery;
    private CheckBox mAgreeTerms;

    public ConfirmationFragment() {
        // Required empty public constructor
    }

    public static ConfirmationFragment newInstance(InForming inForming) {
        Bundle args = new Bundle();
        args.putParcelable(IN_FORMING_ARG, inForming);
        ConfirmationFragment fragment = new ConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirmation, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        mActivity.setToolbarTitle(getString(R.string.confirm), true);
        bindViews(view);
        return view;
    }

    private void bindViews(View view) {
        mGoodsPrice = (TextView) view.findViewById(R.id.confirmGoodsPriceLabel);
        mDeliveryPrice = (TextView) view.findViewById(R.id.confirmDeliveryPriceLabel);
        mInsurancePrice = (TextView) view.findViewById(R.id.confirmInsurancePriceLabel);
        mDeclarationPrice = (TextView) view.findViewById(R.id.confirmDeclarationPriceLabel);

        mTermsAndConditions = (Button) view.findViewById(R.id.confirmTermsAndConditionsBtn);
        mTermsAndConditions.setOnClickListener(this);
        mFinishAndSend = (Button) view.findViewById(R.id.confirmFinishAndSendBtn);
        mFinishAndSend.setOnClickListener(this);

        mAdditionalPackages = (CheckBox) view.findViewById(R.id.confirmAdditionalPackagesCheckBox);
        mAdditionalPackages.setOnCheckedChangeListener(this);
        mLocalDelivery = (CheckBox) view.findViewById(R.id.confirmLocalDeliveryCheckBox);
        mLocalDelivery.setOnCheckedChangeListener(this);
        mAgreeTerms = (CheckBox) view.findViewById(R.id.confirmAgreeTermsCheckBox);
        mAgreeTerms.setOnCheckedChangeListener(this);
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.confirm), true);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmTermsAndConditionsBtn:

                break;
            case R.id.confirmFinishAndSendBtn:
                if (!mAgreeTerms.isChecked()) {
                    Snackbar.make(mFinishAndSend, "Please agree with our terms and condition first", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                FragmentManager fm = mActivity.getFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                    fm.popBackStack();
                }
                mActivity.setToolbarTitle(getString(R.string.in_stock), true);
                mActivity.mActionMenu.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
