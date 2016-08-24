package com.softranger.bayshopmf.ui.awaitingarrival;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.ResultActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.storages.StorageItemsFragment;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAwaitingFragment extends ParentFragment implements View.OnClickListener, View.OnFocusChangeListener {

    private EditText mProductUrlInput;
    private EditText mProductTrackingNumInput;
    private EditText mProductNameInput;
    private EditText mProductPriceInput;

    private CheckBox mAdditionalPhoto;
    private CheckBox mCheckProduct;
    private CheckBox mRepacking;

//    private RadioButton mUsaSelector, mUkSelector, mDeSelector;

    private MainActivity mActivity;
    private Product mProduct;
    private View mRootView;

    public AddAwaitingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_add_awaiting, container, false);
        mActivity = (MainActivity) getActivity();
        mProductUrlInput = (EditText) mRootView.findViewById(R.id.addAwaitingLinkToProductInput);
        mProductTrackingNumInput = (EditText) mRootView.findViewById(R.id.addAwaitingTrackingInput);
        mProductNameInput = (EditText) mRootView.findViewById(R.id.addAwaitingNameInput);
        mProductPriceInput = (EditText) mRootView.findViewById(R.id.addAwaitingPriceInput);

        mProductNameInput.setOnFocusChangeListener(this);
        mProductTrackingNumInput.setOnFocusChangeListener(this);
        mProductUrlInput.setOnFocusChangeListener(this);
        mProductPriceInput.setOnFocusChangeListener(this);

//        RadioGroup storageSelector = (RadioGroup) mRootView.findViewById(R.id.addAwaitingStorageSelectorGroup);
//        storageSelector.setOnCheckedChangeListener(this);
        Button addParcelBtn = (Button) mRootView.findViewById(R.id.addAwaitingAddParcelButton);
        addParcelBtn.setOnClickListener(this);
        mProduct = new Product.Builder().build();
//        mUsaSelector = (RadioButton) mRootView.findViewById(R.id.addAwaitingUsaSelector);
//        mUkSelector = (RadioButton) mRootView.findViewById(R.id.addAwaitingUkSelector);
//        mDeSelector = (RadioButton) mRootView.findViewById(R.id.addAwaitingDeSelector);
//        mUsaSelector.setChecked(true);
        mProduct.setDeposit(Constants.USA);

        mCheckProduct = (CheckBox) mRootView.findViewById(R.id.checkCheckBtn);
        mAdditionalPhoto = (CheckBox) mRootView.findViewById(R.id.photosCheckBtn);
        mRepacking = (CheckBox) mRootView.findViewById(R.id.repackingCheckBtn);

        RelativeLayout photosBtn = (RelativeLayout) mRootView.findViewById(R.id.addAwaitingAdditionalPhotoBtn);
        RelativeLayout checkBtn = (RelativeLayout) mRootView.findViewById(R.id.addAwaitingParcelPrecheckBtn);
        RelativeLayout repackBtn = (RelativeLayout) mRootView.findViewById(R.id.addAwaitingRepackingBtn);

        photosBtn.setOnClickListener(this);
        checkBtn.setOnClickListener(this);
        repackBtn.setOnClickListener(this);

        return mRootView;
    }

//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        switch (checkedId) {
//            case R.id.addAwaitingUsaSelector:
//                mProduct.setDeposit(Constants.USA);
//                break;
//            case R.id.addAwaitingUkSelector:
//            case R.id.addAwaitingDeSelector:
//                Snackbar.make(mRootView, getString(R.string.not_suported), Snackbar.LENGTH_SHORT).show();
//                mUsaSelector.setChecked(true);
//                break;
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAwaitingAdditionalPhotoBtn:
                mAdditionalPhoto.setChecked(!mAdditionalPhoto.isChecked());
                break;
            case R.id.addAwaitingParcelPrecheckBtn:
                mCheckProduct.setChecked(!mCheckProduct.isChecked());
                break;
            case R.id.addAwaitingRepackingBtn:
                mRepacking.setChecked(!mRepacking.isChecked());
                break;
            case R.id.addAwaitingAddParcelButton:
                String productUrl = String.valueOf(mProductUrlInput.getText());
                if (productUrl.equals("") || !URLUtil.isValidUrl(productUrl)) {
                    mProductUrlInput.setError("Please specify a valid product url");
                    return;
                }
                String trackingNum = String.valueOf(mProductTrackingNumInput.getText());
                if (productUrl.equals("")) {
                    mProductTrackingNumInput.setError("Please specify product tracking number");
                    return;
                }
                String productName = String.valueOf(mProductNameInput.getText());
                if (productUrl.equals("")) {
                    mProductNameInput.setError("Please specify product name");
                    return;
                }
                String productPrice = String.valueOf(mProductPriceInput.getText());
                if (productPrice.equals("")) {
                    mProductPriceInput.setError("Please specify product price");
                    return;
                }
                if (mProduct.getDeposit() == null) {
                    Snackbar.make(mRootView, "Please select depot.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                mProduct.setProductPrice(productPrice);
                mProduct.setTrackingNumber(trackingNum);
                mProduct.setProductName(productName);
                mProduct.setProductUrl(productUrl);
                RequestBody body = new FormBody.Builder()
                        .add("storage", mProduct.getDeposit())
                        .add("tracking", mProduct.getTrackingNumber())
                        .add("title", mProduct.getProductName())
                        .add("url", mProduct.getProductUrl())
                        .add("packagePrice", mProduct.getProductPrice())
                        .build();
                ApiClient.getInstance().postRequest(body, Constants.Api.urlAddWaitingArrivalItem(), mHandler);
                mActivity.toggleLoadingProgress(true);
                break;
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject data = response.getJSONObject("data");
        Intent intent = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
        intent.putExtra("deposit", mProduct.getDeposit());
        mActivity.sendBroadcast(intent);

        // build the intent for result activity
        Intent showResult = new Intent(mActivity, ResultActivity.class);
        showResult.putExtra(ResultActivity.TOP_TITLE, getString(R.string.parcel_added));
        showResult.putExtra(ResultActivity.SECOND_TITLE, getString(R.string.parcel_was_added) + " "
                + data.optString("barCode", ""));
        showResult.putExtra(ResultActivity.IMAGE_ID, R.mipmap.ic_parcel_25dp);
        showResult.putExtra(ResultActivity.DESCRIPTION, getString(R.string.thank_you_awaiting));

        // close fragment
        mActivity.onBackPressed();

        // show result activity
        startActivity(showResult);


        int count = Application.counters.get(Constants.ParcelStatus.AWAITING_ARRIVAL);
        count += 1;
        Application.counters.put(Constants.ParcelStatus.AWAITING_ARRIVAL, count);
        mActivity.updateParcelCounters(Constants.ParcelStatus.AWAITING_ARRIVAL);
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.addAwaitingNameInput:
                if (hasFocus) mProductNameInput.setHint("");
                else mProductNameInput.setHint(getString(R.string.ipad_mini));
                break;
            case R.id.addAwaitingLinkToProductInput:
                if (hasFocus) mProductUrlInput.setHint("");
                else mProductUrlInput.setHint(getString(R.string.http_example_com_example_product));
                break;
            case R.id.addAwaitingTrackingInput:
                if (hasFocus) mProductTrackingNumInput.setHint("");
                else mProductTrackingNumInput.setHint(getString(R.string._12345678901234567890));
                break;
            case R.id.addAwaitingPriceInput:
                if (hasFocus) mProductPriceInput.setHint("");
                else mProductPriceInput.setHint(getString(R.string._400));
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.hideKeyboard();
    }
}
