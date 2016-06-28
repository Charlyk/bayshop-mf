package com.softranger.bayshopmf.ui.awaitingarrival;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.ParentFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.storages.StorageItemsFragment;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAwaitingFragment extends ParentFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private EditText mProductUrlInput;
    private EditText mProductTrackingNumInput;
    private EditText mProductNameInput;
    private EditText mProductPriceInput;

    private RadioButton mUsaSelector, mUkSelector, mDeSelector;

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
        RadioGroup storageSelector = (RadioGroup) mRootView.findViewById(R.id.addAwaitingStorageSelectorGroup);
        storageSelector.setOnCheckedChangeListener(this);
        Button addParcelBtn = (Button) mRootView.findViewById(R.id.addAwaitingAddParcelButton);
        addParcelBtn.setOnClickListener(this);
        mProduct = new Product.Builder().build();
        mUsaSelector = (RadioButton) mRootView.findViewById(R.id.addAwaitingUsaSelector);
        mUkSelector = (RadioButton) mRootView.findViewById(R.id.addAwaitingUkSelector);
        mDeSelector = (RadioButton) mRootView.findViewById(R.id.addAwaitingDeSelector);
        mUsaSelector.setChecked(true);
        mProduct.setDeposit(Constants.USA);
        return mRootView;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.addAwaitingUsaSelector:
                mProduct.setDeposit(Constants.USA);
                break;
            case R.id.addAwaitingUkSelector:
            case R.id.addAwaitingDeSelector:
                Snackbar.make(mRootView, getString(R.string.not_suported), Snackbar.LENGTH_SHORT).show();
                mUsaSelector.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addAwaitingAddParcelButton) {
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
            ApiClient.getInstance().sendRequest(body, Constants.Api.urlAddWaitingArrivalItem(), mHandler);
            mActivity.toggleLoadingProgress(true);
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        Intent intent = new Intent(StorageItemsFragment.ACTION_ITEM_CHANGED);
        intent.putExtra("deposit", mProduct.getDeposit());
        mActivity.sendBroadcast(intent);
        mActivity.onBackPressed();
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }
}
