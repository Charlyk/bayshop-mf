package com.softranger.bayshopmf.ui.steps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.CreationDetails;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.ResultActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONArray;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * Created by Eduard Albu on 10/3/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class ConfirmationFragment extends ParentFragment {

    public static final String ACTION_BUILD_FINISHED = "FINISHED PARCEL BUILDING";

    private static final String DETAILS = "CREATION DETAILS";
    private static final String ADDRESS = "SELECTED ADDRESS";
    private static final String SHIPPER = "SELECTED SHIPPER";
    private static final String SHIPPING_PRICE = "SHIPPING PRICE";

    private Unbinder mUnbinder;
    private ParentActivity mActivity;
    private CreationDetails mCreationDetails;
    private String mSelectedAddressId;
    private String mSelectedShipperId;
    private int mInsurancePriceValue;
    private double mTotalPriceValue;
    private Call<ServerResponse<Integer>> mResponseCall;

    @BindView(R.id.insuranceRadioButton) RadioButton mInsuranceSelector;
    @BindView(R.id.noInsuranceRadioButton) RadioButton mNoIsuranceSelector;
    @BindView(R.id.confirmationParcelDetailsLayout) LinearLayout mDetailsLayout;
    @BindView(R.id.insuranceDescriptionLabel) TextView mInsuranceDescription;
    @BindView(R.id.noInsuranceDescriptionLabel) TextView mNoInsuranceDescription;
    @BindView(R.id.confirmAdditionalPackagesCheckBox) CheckBox mUseAdditionalPackage;
    @BindView(R.id.confirmLocalDeliveryCheckBox) CheckBox mNeedLocalDelivery;
    @BindView(R.id.confirmSentOnUserAlert) CheckBox mSendOnAlert;
    @BindView(R.id.confirmAgreeTermsCheckBox) CheckBox mTermsAndConditions;
    @BindView(R.id.confirmInsurancePriceLayout) RelativeLayout mInsurancePriceLayout;

    @BindView(R.id.confirmDeliveryPriceLabel) TextView mDeliveryPrice;
    @BindView(R.id.confirmInsurancePriceLabel) TextView mInsurancePrice;
    @BindView(R.id.confirmDeclarationPriceLabel) TextView mDeclarationPrice;
    @BindView(R.id.confirmTotalPriceLabel) TextView mTotalPrice;
    @BindView(R.id.insurancePriceLabel) TextView mInsurancePriceLabel;

    public ConfirmationFragment() {
        // require emtpy constructor
    }

    public static ConfirmationFragment newInstance(CreationDetails details, String selectedAddressId,
                                                   String selectedShipperId, double shippingPrice) {
        Bundle args = new Bundle();
        args.putParcelable(DETAILS, details);
        args.putString(ADDRESS, selectedAddressId);
        args.putString(SHIPPER, selectedShipperId);
        args.putDouble(SHIPPING_PRICE, shippingPrice);
        ConfirmationFragment fragment = new ConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirmation, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (ParentActivity) getActivity();
        mCreationDetails = getArguments().getParcelable(DETAILS);
        mSelectedAddressId = getArguments().getString(ADDRESS);
        mSelectedShipperId = getArguments().getString(SHIPPER);

        double shippingPrice = getArguments().getDouble(SHIPPING_PRICE);
        double total = shippingPrice + mCreationDetails.getDeclarationPrice();
        int percent = Integer.parseInt(mCreationDetails.getInsurancePercent());
        double percentage = percent / 100.0f;
        mInsurancePriceValue = (int) (total * percentage);

        if (mInsurancePriceValue < mCreationDetails.getInsuranceMin()) {
            mInsurancePriceValue = mCreationDetails.getInsuranceMin();
        } else if (mInsurancePriceValue > mCreationDetails.getInsuranceMax()) {
            mInsurancePriceValue = mCreationDetails.getInsuranceMax();
        }

        mInsurancePriceLabel.setText(mCreationDetails.getCurrencySign() + mInsurancePriceValue);
        mDeliveryPrice.setText(mCreationDetails.getCurrencySign() + shippingPrice);
        mTotalPriceValue = shippingPrice;
        mDeclarationPrice.setText(mCreationDetails.getCurrencySign() + mCreationDetails.getDeclarationPrice());
        return view;
    }

    @OnClick(R.id.insuranceSelector)
    void selectInsurance() {
        mInsuranceSelector.setChecked(true);
        mNoIsuranceSelector.setChecked(false);
        if (mInsurancePriceLayout.getVisibility() != View.VISIBLE) mInsurancePriceLayout.setVisibility(View.VISIBLE);
        mInsurancePrice.setText(mCreationDetails.getCurrencySign() + mInsurancePriceValue);
        mTotalPriceValue = mTotalPriceValue + mInsurancePriceValue;
        mTotalPrice.setText(mCreationDetails.getCurrencySign() + mTotalPriceValue);
        toggleDescriptionVisibility();
        if (mDetailsLayout.getVisibility() != View.VISIBLE) mDetailsLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.noInsuranceSelector)
    void deselectInsurance() {
        mInsuranceSelector.setChecked(false);
        mNoIsuranceSelector.setChecked(true);
        if (mInsurancePriceLayout.getVisibility() == View.VISIBLE) mInsurancePriceLayout.setVisibility(View.GONE);
        mTotalPriceValue = mTotalPriceValue - mInsurancePriceValue;
        mTotalPrice.setText(mCreationDetails.getCurrencySign() + mTotalPriceValue);
        toggleDescriptionVisibility();
        if (mDetailsLayout.getVisibility() != View.VISIBLE) mDetailsLayout.setVisibility(View.VISIBLE);
    }

    private void toggleDescriptionVisibility() {
        if (mInsuranceSelector.isChecked()) {
            mInsuranceDescription.setVisibility(View.VISIBLE);
            mNoInsuranceDescription.setVisibility(View.GONE);
        } else if (mNoIsuranceSelector.isChecked()) {
            mNoInsuranceDescription.setVisibility(View.VISIBLE);
            mInsuranceDescription.setVisibility(View.GONE);
        }
    }

    /**
     * Enable or disable additional packaging materials request
     */
    @OnClick(R.id.confirmAdditionalPackageButton)
    void addAdditionalPackagingMaterials() {
        mUseAdditionalPackage.setChecked(!mUseAdditionalPackage.isChecked());
    }

    /**
     * Show info about packaging materials
     */
    @OnClick(R.id.confirmAdditionalPackageDetails)
    void showAdditionalPackagesDetails() {

    }

    /**
     * Enable or disable home delivery request
     */
    @OnClick(R.id.confirmLocalDeliveryButton)
    void orderLocalDelivery() {
        mNeedLocalDelivery.setChecked(!mNeedLocalDelivery.isChecked());
    }

    /**
     * Show information about home delivery service
     */
    @OnClick(R.id.confirmLocalDeliveryDetails)
    void showLocalDeliveryDetails() {

    }

    /**
     * Enable or disable sending on user alert service
     */
    @OnClick(R.id.confirmSentOnUserAlertButton)
    void doNotSendParcelYet() {
        mSendOnAlert.setChecked(!mSendOnAlert.isChecked());
    }

    /**
     * Show info about holding a parcel to our depot
     */
    @OnClick(R.id.confirmSentOnUserAlertDetails)
    void showOnUserAlertDetails() {
        mTermsAndConditions.setChecked(!mTermsAndConditions.isChecked());
    }

    @OnClick(R.id.confirmAgreeTermsDetails)
    void showTermsAndConditions() {

    }

    @OnClick(R.id.confirmFinishAndSendBtn)
    void createParcel() {
        if (!mInsuranceSelector.isChecked() && !mNoIsuranceSelector.isChecked()) {
            Toast.makeText(mActivity, getString(R.string.select_insurance), Toast.LENGTH_SHORT).show();
            return;
        }

        mActivity.toggleLoadingProgress(true);
        JSONArray boxes = new JSONArray();
        for (int i : mCreationDetails.getItems()) {
            boxes.put(i);
        }
        mResponseCall = Application.apiInterface().createNewPusParcel(Application.currentToken,
                boxes.toString(), mSelectedAddressId, mSelectedShipperId, mInsuranceSelector.isChecked(),
                mSendOnAlert.isChecked(), mUseAdditionalPackage.isChecked());
        mResponseCall.enqueue(mResponseCallback);
    }

    private ResponseCallback<Integer> mResponseCallback = new ResponseCallback<Integer>() {
        @Override
        public void onSuccess(Integer data) {
            Intent update = new Intent(ACTION_BUILD_FINISHED);
            mActivity.sendBroadcast(update);

            Intent intent = new Intent(mActivity, ResultActivity.class);
            intent.putExtra(ResultActivity.TOP_TITLE, getString(R.string.parcel_created));
            intent.putExtra(ResultActivity.IMAGE_ID, R.mipmap.ic_parcel_50dp);
            intent.putExtra(ResultActivity.SECOND_TITLE, data + " " + getString(R.string.parcel_added));
            intent.putExtra(ResultActivity.DESCRIPTION, getString(R.string.your_parcel_added) + data);
            mActivity.startActivity(intent);
            mActivity.finish();
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<Integer>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public String getFragmentTitle() {
        return getString(R.string.confirm);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.confirmation;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
