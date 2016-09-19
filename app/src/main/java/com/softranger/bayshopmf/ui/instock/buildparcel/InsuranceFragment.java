package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Insurance;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsuranceFragment extends ParentFragment implements View.OnClickListener {

    private static final String IN_FORMING_ARG = "in forming item arg";
    private MainActivity mActivity;
    private InForming mInForming;
    private View mRootView;
    private static boolean needInsurance;

    private RadioButton mNeedInSurance;
    private RadioButton mRefuseInsurance;
    private TextView mNeedInsuranceDescription;
    private TextView mRefuseInsuranceDescription;
    private TextView mProductsPriceLabel;
    private TextView mShippingPriceLabel;
    private TextView mInsurancePriceLabel;
    private Button mNeedInsuranceDetails;
    private ImageButton mRefuseInsuranceDetails;
    private Button mConfirmBtn;

    public InsuranceFragment() {
        // Required empty public constructor
    }

    public static InsuranceFragment newInstance(InForming inForming) {
        Bundle args = new Bundle();
        args.putParcelable(IN_FORMING_ARG, inForming);
        InsuranceFragment fragment = new InsuranceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_insurance, container, false);
        mActivity = (MainActivity) getActivity();

        needInsurance = true;

        mNeedInsuranceDetails = (Button) mRootView.findViewById(R.id.insuranceDetailsButton);
        mRefuseInsuranceDetails = (ImageButton) mRootView.findViewById(R.id.noInsuranceDetailsButton);
        mNeedInSurance = (RadioButton) mRootView.findViewById(R.id.insuranceRadioButton);
        mRefuseInsurance = (RadioButton) mRootView.findViewById(R.id.noInsuranceRadioButton);
        RelativeLayout needInsuranceLayout = (RelativeLayout) mRootView.findViewById(R.id.insuranceSelector);
        RelativeLayout refuseInsuranceLayout = (RelativeLayout) mRootView.findViewById(R.id.noInsuranceSelector);
        needInsuranceLayout.setOnClickListener(this);
        refuseInsuranceLayout.setOnClickListener(this);
        mNeedInsuranceDetails.setOnClickListener(this);
        mRefuseInsuranceDetails.setOnClickListener(this);
        mConfirmBtn = (Button) mRootView.findViewById(R.id.insuranceConfirmButton);
        mConfirmBtn.setOnClickListener(this);

        mNeedInsuranceDescription = (TextView) mRootView.findViewById(R.id.insuranceDescriptionLabel);
        mRefuseInsuranceDescription = (TextView) mRootView.findViewById(R.id.noInsuranceDescriptionLabel);
        mProductsPriceLabel = (TextView) mRootView.findViewById(R.id.insuranceProductsCostLabel);
        mShippingPriceLabel = (TextView) mRootView.findViewById(R.id.insuranceShippingCostLabel);
        mInsurancePriceLabel = (TextView) mRootView.findViewById(R.id.insuranceInsuranceCostLabel);

        mInForming = getArguments().getParcelable(IN_FORMING_ARG);

        mActivity.toggleLoadingProgress(true);

        RequestBody body = new FormBody.Builder()
                .add("shipperMeasureId", String.valueOf(mInForming.getShippingMethod().getId()))
                .build();
        ApiClient.getInstance().postRequest(body, Constants.Api.urlBuildStep(4, String.valueOf(mInForming.getId())), mHandler);
        return mRootView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void changeButtonBackground(boolean needInsurance) {
        Drawable buttonBg;
        if (needInsurance) {
            buttonBg = mActivity.getResources().getDrawable(R.drawable.green_button_bg);
        } else {
            buttonBg = mActivity.getResources().getDrawable(R.drawable.red_button_bg);
        }
        mConfirmBtn.setBackground(buttonBg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.insuranceSelector:
                needInsurance = true;
                mNeedInSurance.setChecked(true);
                mRefuseInsurance.setChecked(false);
                changeButtonBackground(needInsurance);
                break;
            case R.id.noInsuranceSelector:
                needInsurance = false;
                mNeedInSurance.setChecked(false);
                mRefuseInsurance.setChecked(true);
                changeButtonBackground(needInsurance);
                break;
            case R.id.insuranceConfirmButton:
                mInForming.setNeedInsurance(needInsurance);
                mActivity.addFragment(ConfirmationFragment.newInstance(mInForming), true);
                break;
            case R.id.insuranceDetailsButton:
                mActivity.addFragment(InsuranceAgreementFragment.newInstance(getString(R.string.lorem_ipsum)), true);
                break;
            case R.id.noInsuranceDetailsButton:
                if (mRefuseInsuranceDescription.getLineCount() == 4) {
                    mActivity.expandTextView(mRefuseInsuranceDescription);
                } else {
                    mActivity.collapseTextView(mRefuseInsuranceDescription, 4);
                }
                break;
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject data = response.getJSONObject("data");
        Insurance insurance = new Insurance.Builder()
                .currency(data.getString("currency"))
                .commission(data.getDouble("commission"))
                .shippingCost(data.getDouble("shippingCost"))
                .totalPriceBoxes(data.getDouble("totalPriceBoxes"))
                .isInsuranceSelected(data.getBoolean("insuranceSelected"))
                .isInsuranceAvailable(data.getBoolean("insuranceAvailable"))
                .declarationTotalPrice(data.getDouble("declarationTotalPrice"))
                .build();

        mProductsPriceLabel.setText(insurance.getCurrency() + insurance.getDeclarationTotalPrice());
        mShippingPriceLabel.setText(insurance.getCurrency() + insurance.getShippingCost());
        mInsurancePriceLabel.setText(insurance.getCurrency() + insurance.getCommission());
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.select_insurance);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.insurance;
    }
}
