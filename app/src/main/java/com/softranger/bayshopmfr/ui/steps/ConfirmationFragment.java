package com.softranger.bayshopmfr.ui.steps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.CreationDetails;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.help.HelpDialog;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

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
    private Call<ServerResponse<String>> mResponseCall;
    // chrome tabs intent used to open products url
    private CustomTabsIntent mTabsIntent;

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
    @BindView(R.id.confirmAdditionalPackagePriceLayout)
    RelativeLayout mAdditionalPackLayout;

    @BindView(R.id.confirmDeliveryPriceLabel) TextView mDeliveryPrice;
    @BindView(R.id.confirmInsurancePriceLabel) TextView mInsurancePrice;
    @BindView(R.id.confirmDeclarationPriceLabel) TextView mDeclarationPrice;
    @BindView(R.id.confirmTotalPriceLabel) TextView mTotalPrice;
    @BindView(R.id.insurancePriceLabel) TextView mInsurancePriceLabel;

    @BindView(R.id.confirmFinishAndSendBtn)
    Button mConfirmBtn;

    private boolean wasInsuranceSelected;

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

        if (Application.isAutopackaging() && Application.hasInsurance()) {
            selectInsurance();
        }

        mConfirmBtn.setEnabled(mTermsAndConditions.isChecked());
        mConfirmBtn.setClickable(mTermsAndConditions.isChecked());

        // create tabs intent for products url
        CustomTabsIntent.Builder tabsBuilder = new CustomTabsIntent.Builder();
        tabsBuilder.setToolbarColor(getResources().getColor(R.color.colorAccent));
        tabsBuilder.setSecondaryToolbarColor(getResources().getColor(R.color.colorPrimary));
        mTabsIntent = tabsBuilder.build();

        return view;
    }

    @OnClick(R.id.insuranceSelector)
    void selectInsurance() {
        wasInsuranceSelected = true;
        mInsuranceSelector.setChecked(true);
        mNoIsuranceSelector.setChecked(false);
        if (mInsurancePriceLayout.getVisibility() != View.VISIBLE) mInsurancePriceLayout.setVisibility(View.VISIBLE);
        mInsurancePrice.setText(mCreationDetails.getCurrencySign() + mInsurancePriceValue);
        mTotalPriceValue = mTotalPriceValue + mInsurancePriceValue;
        mTotalPrice.setText(mCreationDetails.getCurrencySign() + Application.round(mTotalPriceValue, 2));
        toggleDescriptionVisibility();
        if (mDetailsLayout.getVisibility() != View.VISIBLE) mDetailsLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.noInsuranceSelector)
    void deselectInsurance() {
        mInsuranceSelector.setChecked(false);
        mNoIsuranceSelector.setChecked(true);
        if (mInsurancePriceLayout.getVisibility() == View.VISIBLE) mInsurancePriceLayout.setVisibility(View.GONE);
        if (wasInsuranceSelected) {
            mTotalPriceValue = mTotalPriceValue - mInsurancePriceValue;
        }
        mTotalPrice.setText(mCreationDetails.getCurrencySign() + Application.round(mTotalPriceValue, 2));
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
        mAdditionalPackLayout.setVisibility(mUseAdditionalPackage.isChecked() ? View.VISIBLE : View.GONE);

        if (mUseAdditionalPackage.isChecked()) {
            mTotalPriceValue += 10f;
        } else {
            mTotalPriceValue -= 10f;
        }

        mTotalPrice.setText(mCreationDetails.getCurrencySign() + Application.round(mTotalPriceValue, 2));
    }

    /**
     * Show info about packaging materials
     */
    @OnClick(R.id.confirmAdditionalPackageDetails)
    void showAdditionalPackagesDetails() {
        HelpDialog.showDialog(mActivity, getString(R.string.additional_package_details));
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
        HelpDialog.showDialog(mActivity, getString(R.string.local_delivery_details));
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
        HelpDialog.showDialog(mActivity, getString(R.string.sent_on_request_details));
    }

    @OnClick(R.id.confirmAgreeTermsDetails)
    void showTermsAndConditions() {
        String strUrl = "https://md.bayshop.com/en/page/terms-of-use.html";
        mTabsIntent.launchUrl(mActivity, Uri.parse(strUrl));
    }

    @OnClick(R.id.confirmAgreeTermsButton)
    void toggleTermsAndConditions() {
        mTermsAndConditions.setChecked(!mTermsAndConditions.isChecked());
        mConfirmBtn.setEnabled(mTermsAndConditions.isChecked());
        mConfirmBtn.setClickable(mTermsAndConditions.isChecked());
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
        mResponseCall = Application.apiInterface().createNewPusParcel(
                boxes.toString(), mSelectedAddressId, mSelectedShipperId, mInsuranceSelector.isChecked(),
                mSendOnAlert.isChecked(), mUseAdditionalPackage.isChecked());
        mResponseCall.enqueue(mResponseCallback);
    }

    private ResponseCallback<String> mResponseCallback = new ResponseCallback<String>() {
        @Override
        public void onSuccess(String data) {
            Intent update = new Intent(ACTION_BUILD_FINISHED);
            mActivity.sendBroadcast(update);

            mActivity.showResultActivity(data + " " + getString(R.string.parcel_added), R.mipmap.ic_confirm_3steps_250dp,
                    String.format(getString(R.string.your_parcel_added), data));
            mActivity.finish();
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<String>> call, Throwable t) {
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
        wasInsuranceSelected = false;
        mUnbinder.unbind();
    }
}
