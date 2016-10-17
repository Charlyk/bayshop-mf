package com.softranger.bayshopmf.ui.awaitingarrival;


import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.AwaitingArrival;
import com.softranger.bayshopmf.model.box.Product;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAwaitingFragment extends ParentFragment {

    public static final String ACTION_ITEM_ADDED = "ADDED NEW AWAITING PARCEL";

    @BindView(R.id.addAwaitingLinkToProductInput)
    EditText mProductUrlInput;
    @BindView(R.id.addAwaitingTrackingInput)
    EditText mProductTrackingNumInput;
    @BindView(R.id.addAwaitingNameInput)
    EditText mProductNameInput;
    @BindView(R.id.addAwaitingPriceInput)
    EditText mProductPriceInput;

    @BindView(R.id.photosCheckBtn)
    CheckBox mAdditionalPhoto;
    @BindView(R.id.checkCheckBtn)
    CheckBox mCheckProduct;
    @BindView(R.id.repackingCheckBtn)
    CheckBox mRepacking;

    @BindView(R.id.addAwaitingLinkLayout)
    LinearLayout mLinkLayout;
    @BindView(R.id.addAwaitingTrackinglayout)
    LinearLayout mTrackingLayout;
    @BindView(R.id.addAwaitingNameLayout)
    LinearLayout mNameLayout;
    @BindView(R.id.addAwaitingPricelayout)
    LinearLayout mPriceLayout;

    @BindView(R.id.addAwaitingInputFocusIndicator)
    View mFocusIndicator;

//    private RadioButton mUsaSelector, mUkSelector, mDeSelector;

    private MainActivity mActivity;
    private Product mProduct;
    private Unbinder mUnbinder;
    private Call<ServerResponse<AwaitingArrival>> mAddAwaitingCall;

    public AddAwaitingFragment() {
        // Required empty public constructor
    }

    public static AddAwaitingFragment newInstance() {
        Bundle args = new Bundle();
        AddAwaitingFragment fragment = new AddAwaitingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_awaiting, container, false);
        mActivity = (MainActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, rootView);

        mProduct = new Product();

        return rootView;
    }

    @OnFocusChange({R.id.addAwaitingLinkToProductInput, R.id.addAwaitingTrackingInput,
            R.id.addAwaitingNameInput, R.id.addAwaitingPriceInput})
    void onFocusChaged(View v, boolean hasFocus) {
        if (mFocusIndicator.getVisibility() != View.VISIBLE)
            mFocusIndicator.setVisibility(View.VISIBLE);

        switch (v.getId()) {
            case R.id.addAwaitingNameInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mNameLayout.getY()).setDuration(300).start();
                if (hasFocus) mProductNameInput.setHint("");
                else mProductNameInput.setHint(getString(R.string.ipad_mini));
                break;
            case R.id.addAwaitingLinkToProductInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mLinkLayout.getY()).setDuration(300).start();
                if (hasFocus) mProductUrlInput.setHint("");
                else mProductUrlInput.setHint(getString(R.string.http_example_com_example_product));
                break;
            case R.id.addAwaitingTrackingInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mTrackingLayout.getY()).setDuration(300).start();
                if (hasFocus) mProductTrackingNumInput.setHint("");
                else mProductTrackingNumInput.setHint(getString(R.string._12345678901234567890));
                break;
            case R.id.addAwaitingPriceInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mPriceLayout.getY()).setDuration(300).start();
                if (hasFocus) mProductPriceInput.setHint("");
                else mProductPriceInput.setHint(getString(R.string._400));
                break;
        }
    }

    @OnClick({R.id.addAwaitingAdditionalPhotoBtn, R.id.addAwaitingParcelPrecheckBtn,
            R.id.addAwaitingRepackingBtn, R.id.addAwaitingAddParcelButton})
    void onButtonClick(View v) {
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
                mProduct.setPrice(productPrice);
                mProduct.setTitle(productName);
                mProduct.setUrl(productUrl);

                mActivity.toggleLoadingProgress(true);
//                mAddAwaitingCall = Application.apiInterface().addNewAwaitingParcel(Constants.USA,
//                        mProduct.getTitle(), mProduct.getUrl(), mProduct.getPrice());
                mAddAwaitingCall.enqueue(mResponseCallback);
                break;
        }
    }

    private ResponseCallback<AwaitingArrival> mResponseCallback = new ResponseCallback<AwaitingArrival>() {
        @Override
        public void onSuccess(AwaitingArrival data) {
            Intent intent = new Intent(ACTION_ITEM_ADDED);
            mActivity.sendBroadcast(intent);

            // close fragment
            mActivity.onBackPressed();

            mActivity.showResultActivity(getString(R.string.parcel_added), getString(R.string.parcel_was_added) + " "
                    + data.getUid(), R.mipmap.ic_parcel_25dp, getString(R.string.thank_you_awaiting));

            int count = Application.counters.get(Constants.ParcelStatus.AWAITING_ARRIVAL);
            count += 1;
            Application.counters.put(Constants.ParcelStatus.AWAITING_ARRIVAL, count);
            mActivity.updateParcelCounters(Constants.ParcelStatus.AWAITING_ARRIVAL);
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<AwaitingArrival>> call, Throwable t) {
            // TODO: 9/21/16 handle errors
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public String getFragmentTitle() {
        return getString(R.string.add_awaiting_package);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.add_awaiting_parcel;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.hideKeyboard();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAddAwaitingCall != null) mAddAwaitingCall.cancel();
        mUnbinder.unbind();
        Intent showActionBtn = new Intent(AwaitingArrivalFragment.ACTION_SHOW_BTN);
        mActivity.sendBroadcast(showActionBtn);
    }
}
