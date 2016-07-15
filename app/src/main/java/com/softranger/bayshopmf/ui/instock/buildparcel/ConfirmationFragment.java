package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class ConfirmationFragment extends ParentFragment implements View.OnClickListener {

    private static final String IN_FORMING_ARG = "in forming item argument";
    private MainActivity mActivity;
    private TextView mGoodsPrice;
    private TextView mDeliveryPrice;
    private TextView mInsurancePrice;
    private TextView mDeclarationPrice;
    private TextView mTotalPrice;
    private Button mFinishAndSend;
    private CheckBox mAgreeTerms;
    private CheckBox mAdditionalPackage;
    private CheckBox mLocalDelivery;
    private CheckBox mSendOnAlert;
    private View mRootView;
    private InForming mInForming;
    private static boolean isButtonClicked;

    private RelativeLayout mDeclarationPriceLayout;
    private RelativeLayout mInsurancePriceLayout;

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
        mRootView = inflater.inflate(R.layout.fragment_confirmation, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        mActivity.setToolbarTitle(getString(R.string.confirm), true);
        bindViews(mRootView);
        mInForming = getArguments().getParcelable(IN_FORMING_ARG);
        mActivity.toggleLoadingProgress(true);

        if (!mInForming.isAutoFilling()) mDeclarationPriceLayout.setVisibility(View.GONE);
        if (!mInForming.isNeedInsurance()) mInsurancePriceLayout.setVisibility(View.GONE);

        RequestBody body = new FormBody.Builder()
                .add("insuranceAddressId", String.valueOf(mInForming.isNeedInsurance() ? 1 : 0))
                .build();
        ApiClient.getInstance().postRequest(body, Constants.Api.urlBuildStep(6, String.valueOf(mInForming.getId())), mHandler);
        return mRootView;
    }

    private void bindViews(View view) {

        mDeclarationPriceLayout = (RelativeLayout) view.findViewById(R.id.confirmDeclarationPriceLayout);
        mInsurancePriceLayout = (RelativeLayout) view.findViewById(R.id.confirmInsurancePriceLayout);

        mGoodsPrice = (TextView) view.findViewById(R.id.confirmGoodsPriceLabel);
        mDeliveryPrice = (TextView) view.findViewById(R.id.confirmDeliveryPriceLabel);
        mInsurancePrice = (TextView) view.findViewById(R.id.confirmInsurancePriceLabel);
        mDeclarationPrice = (TextView) view.findViewById(R.id.confirmDeclarationPriceLabel);
        mTotalPrice = (TextView) view.findViewById(R.id.confirmTotalPriceLabel);

        RelativeLayout additionalPack = (RelativeLayout) view.findViewById(R.id.confirmAdditionalPackageButton);
        additionalPack.setOnClickListener(this);
        RelativeLayout localDelivery = (RelativeLayout) view.findViewById(R.id.confirmLocalDeliveryButton);
        localDelivery.setOnClickListener(this);
        RelativeLayout onUserAlert = (RelativeLayout) view.findViewById(R.id.confirmSentOnUserAlertButton);
        onUserAlert.setOnClickListener(this);
        RelativeLayout agreeTerms = (RelativeLayout) view.findViewById(R.id.confirmAgreeTermsButton);
        agreeTerms.setOnClickListener(this);

        ImageView additionalDetails = (ImageView) view.findViewById(R.id.confirmAdditionalPackageDetails);
        additionalDetails.setOnClickListener(this);
        ImageView localDetails = (ImageView) view.findViewById(R.id.confirmLocalDeliveryDetails);
        localDetails.setOnClickListener(this);
        ImageView onAlertDetails = (ImageView) view.findViewById(R.id.confirmSentOnUserAlertDetails);
        onAlertDetails.setOnClickListener(this);
        ImageView confirmDetails = (ImageView) view.findViewById(R.id.confirmAgreeTermsDetails);
        confirmDetails.setOnClickListener(this);

        mFinishAndSend = (Button) view.findViewById(R.id.confirmFinishAndSendBtn);
        mFinishAndSend.setOnClickListener(this);

        mAdditionalPackage = (CheckBox) view.findViewById(R.id.confirmAdditionalPackagesCheckBox);
        mLocalDelivery = (CheckBox) view.findViewById(R.id.confirmLocalDeliveryCheckBox);
        mSendOnAlert = (CheckBox) view.findViewById(R.id.confirmSentOnUserAlert);
        mAgreeTerms = (CheckBox) view.findViewById(R.id.confirmAgreeTermsCheckBox);
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

    private void setDataInPosition(InForming inForming) {
        mGoodsPrice.setText(String.valueOf(inForming.getCurrency() + inForming.getGoodsPrice()));
        mDeliveryPrice.setText(String.valueOf(inForming.getCurrency() + inForming.getShippingPrice()));
        mInsurancePrice.setText(String.valueOf(inForming.getCurrency() + inForming.getInsurancePrice()));
        mDeclarationPrice.setText(String.valueOf(inForming.getCurrency() + inForming.getDeclarationPrice()));
        mTotalPrice.setText(String.valueOf(inForming.getCurrency() + inForming.getTotalPrice()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mTitleReceiver);
    }

    AlertDialog mAlertDialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmAdditionalPackageButton:
                mAdditionalPackage.setChecked(!mAdditionalPackage.isChecked());
                mInForming.setAdditionalPackage(mAdditionalPackage.isChecked());
                break;
            case R.id.confirmLocalDeliveryButton:
                mLocalDelivery.setChecked(!mLocalDelivery.isChecked());
                mInForming.setLocalDelivery(mLocalDelivery.isChecked());
                break;
            case R.id.confirmSentOnUserAlertButton:
                mSendOnAlert.setChecked(!mSendOnAlert.isChecked());
                mInForming.setSentOnUserAlert(mSendOnAlert.isChecked());
                break;
            case R.id.confirmAgreeTermsButton:
                mAgreeTerms.setChecked(!mAgreeTerms.isChecked());
                break;
            case R.id.confirmFinishAndSendBtn:
                if (!mAgreeTerms.isChecked()) {
                    Snackbar.make(mFinishAndSend, "Please agree with our terms and condition first", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                isButtonClicked = true;
                mActivity.toggleLoadingProgress(true);
                RequestBody body = new FormBody.Builder()
                        .add("useAdditionalMaterials", String.valueOf(mInForming.isAdditionalPackage() ? 1 : 0))
                        .add("sentOnUserAlert", String.valueOf(mInForming.isSentOnUserAlert() ? 1 : 0))
                        .build();
                ApiClient.getInstance().postRequest(body, Constants.Api.urlBuildStep(7, String.valueOf(mInForming.getId())), mHandler);
                break;
            case R.id.confirmAdditionalPackageDetails:
                mAlertDialog = mActivity.getDialog("Additional package", "Here will be the details of this element, " +
                                "this text will be replaced with actual details", R.mipmap.ic_arrow_back_white, "OK",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialog.dismiss();
                            }
                        }, null, null);
                mAlertDialog.show();
                break;
            case R.id.confirmLocalDeliveryDetails:
                mAlertDialog = mActivity.getDialog("Local delivery", "Here will be the details of this element, " +
                                "this text will be replaced with actual details", R.mipmap.ic_arrow_back_white, "OK",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialog.dismiss();
                            }
                        }, null, null);
                mAlertDialog.show();
                break;
            case R.id.confirmSentOnUserAlertDetails:
                mAlertDialog = mActivity.getDialog("Sent on user alert", "Here will be the details of this element, " +
                                "this text will be replaced with actual details", R.mipmap.ic_arrow_back_white, "OK",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialog.dismiss();
                            }
                        }, null, null);
                mAlertDialog.show();
                break;
            case R.id.confirmAgreeTermsDetails:
                mAlertDialog = mActivity.getDialog("Agree terms", "Here will be the details of this element, " +
                                "this text will be replaced with actual details", R.mipmap.ic_arrow_back_white, "OK",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialog.dismiss();
                            }
                        }, null, null);
                mAlertDialog.show();
                break;
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        if (isButtonClicked) {
            FragmentManager fm = mActivity.getFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                fm.popBackStack();
            }
            mActivity.setToolbarTitle(getString(R.string.in_stock), true);
            mActivity.removeActionButtons();
            mActivity.mActionMenu.setVisibility(View.VISIBLE);
            mActivity.setToolbarToInitialState();
            Application.counters.put(Constants.ParcelStatus.LIVE, Application.counters.get(Constants.ParcelStatus.LIVE) - 1);
            mActivity.updateParcelCounters(Constants.ParcelStatus.LIVE);
        } else {
            JSONObject data = response.getJSONObject("data");
            mInForming.setGoodsPrice(data.getDouble("totalPriceBoxes"));
            mInForming.setShippingPrice(data.getDouble("shippingCost"));
            mInForming.setInsurancePrice(data.getDouble("insuranceCommission"));
            mInForming.setDeclarationPrice(data.getDouble("declarationPrice"));
            mInForming.setTotalPrice(data.getDouble("totalShippingPrice"));
            mInForming.setCurrency(data.getString("currency"));
            setDataInPosition(mInForming);
        }
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
        isButtonClicked = false;
    }
}
