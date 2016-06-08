package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private TextView mTotalPrice;
    private Button mFinishAndSend;
    private CheckBox mAgreeTerms;
    private View mRootView;
    private InForming mInForming;

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
        RequestBody body = new FormBody.Builder()
                .add("insuranceAddressId", String.valueOf(mInForming.isNeedInsurance() ? 1 : 0))
                .build();
        ApiClient.getInstance().sendRequest(body, Constants.Api.urlBuildStep(6, String.valueOf(mInForming.getId())), mHandler);
        return mRootView;
    }

    private void bindViews(View view) {
        mGoodsPrice = (TextView) view.findViewById(R.id.confirmGoodsPriceLabel);
        mDeliveryPrice = (TextView) view.findViewById(R.id.confirmDeliveryPriceLabel);
        mInsurancePrice = (TextView) view.findViewById(R.id.confirmInsurancePriceLabel);
        mDeclarationPrice = (TextView) view.findViewById(R.id.confirmDeclarationPriceLabel);
        mTotalPrice = (TextView) view.findViewById(R.id.confirmTotalPriceLabel);

        Button termsAndConditions = (Button) view.findViewById(R.id.confirmTermsAndConditionsBtn);
        termsAndConditions.setOnClickListener(this);
        mFinishAndSend = (Button) view.findViewById(R.id.confirmFinishAndSendBtn);
        mFinishAndSend.setOnClickListener(this);

        CheckBox additionalPackages = (CheckBox) view.findViewById(R.id.confirmAdditionalPackagesCheckBox);
        additionalPackages.setOnCheckedChangeListener(this);
        CheckBox localDelivery = (CheckBox) view.findViewById(R.id.confirmLocalDeliveryCheckBox);
        localDelivery.setOnCheckedChangeListener(this);
        CheckBox sentOnUserAlert = (CheckBox) view.findViewById(R.id.confirmSentOnUserAlert);
        sentOnUserAlert.setOnCheckedChangeListener(this);
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

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            mInForming.setGoodsPrice(data.getDouble("totalPriceBoxes"));
                            mInForming.setShippingPrice(data.getDouble("shippingCost"));
                            mInForming.setInsurancePrice(data.getDouble("insuranceCommission"));
                            mInForming.setDeclarationPrice(data.getDouble("declarationPrice"));
                            mInForming.setTotalPrice(data.getDouble("totalShippingPrice"));
                            mInForming.setCurrency(data.getString("currency"));
                            setDataInPosition(mInForming);
                        } else {
                            Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRootView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
            }
            mActivity.toggleLoadingProgress(false);
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
                mActivity.toggleLoadingProgress(true);
                RequestBody body = new FormBody.Builder()
                        .add("useAdditionalMaterials", String.valueOf(mInForming.isAdditionalPackage() ? 1 : 0))
                        .add("sentOnUserAlert", String.valueOf(mInForming.isSentOnUserAlert() ? 1 : 0))
                        .build();
                ApiClient.getInstance().sendRequest(body, Constants.Api.urlBuildStep(7, String.valueOf(mInForming.getId())), mFinishHandler);
                break;
        }
    }

    private Handler mFinishHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            FragmentManager fm = mActivity.getFragmentManager();
                            for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                                fm.popBackStack();
                            }
                            mActivity.setToolbarTitle(getString(R.string.in_stock), true);
                            mActivity.removeActionButtons();
                            mActivity.mActionMenu.setVisibility(View.VISIBLE);
                            mActivity.setToolbarToInitialState();
                        } else {
                            Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRootView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
            }
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.confirmAdditionalPackagesCheckBox:
                mInForming.setAdditionalPackage(isChecked);
                break;
            case R.id.confirmSentOnUserAlert:
                mInForming.setSentOnUserAlert(isChecked);
                break;
        }
    }
}
