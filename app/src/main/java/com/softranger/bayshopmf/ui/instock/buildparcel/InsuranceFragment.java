package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Insurance;
import com.softranger.bayshopmf.model.packages.InForming;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsuranceFragment extends Fragment implements View.OnClickListener {

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
    private Button mRefuseInsuranceDetails;

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
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        mActivity.setToolbarTitle(getString(R.string.select_insurance), true);

        needInsurance = true;

        mNeedInsuranceDetails = (Button) mRootView.findViewById(R.id.insuranceDetailsButton);
        mRefuseInsuranceDetails = (Button) mRootView.findViewById(R.id.noInsuranceDetailsButton);
        mNeedInSurance = (RadioButton) mRootView.findViewById(R.id.insuranceRadioButton);
        mRefuseInsurance = (RadioButton) mRootView.findViewById(R.id.noInsuranceRadioButton);
        RelativeLayout needInsuranceLayout = (RelativeLayout) mRootView.findViewById(R.id.insuranceSelector);
        RelativeLayout refuseInsuranceLayout = (RelativeLayout) mRootView.findViewById(R.id.noInsuranceSelector);
        needInsuranceLayout.setOnClickListener(this);
        refuseInsuranceLayout.setOnClickListener(this);
        mNeedInsuranceDetails.setOnClickListener(this);
        mRefuseInsuranceDetails.setOnClickListener(this);
        Button next = (Button) mRootView.findViewById(R.id.insuranceConfirmButton);
        next.setOnClickListener(this);

        mNeedInsuranceDescription = (TextView) mRootView.findViewById(R.id.insuranceDescriptionLabel);
        mRefuseInsuranceDescription = (TextView) mRootView.findViewById(R.id.noInsuranceDescriptionLabel);
        mProductsPriceLabel = (TextView) mRootView.findViewById(R.id.insuranceProductsCostLabel);
        mShippingPriceLabel = (TextView) mRootView.findViewById(R.id.insuranceShippingCostLabel);
        mInsurancePriceLabel = (TextView) mRootView.findViewById(R.id.insuranceInsuranceCostLabel);

        mInForming = getArguments().getParcelable(IN_FORMING_ARG);

        mActivity.toggleLoadingProgress(true);
        RequestBody body = new FormBody.Builder()
                .add("autocomplete", String.valueOf(0))
                .add("declarationName", mInForming.getGeneralDescription())
                .add("declarations", String.valueOf(buildProductsArray(mInForming.getProducts())))
                .build();
        ApiClient.getInstance().sendRequest(body, Constants.Api.urlBuildStep(5, String.valueOf(mInForming.getId())), mHandler);
        return mRootView;
    }

    private JSONArray buildProductsArray(ArrayList<Product> products) {
        JSONArray productsJSON = new JSONArray();
        try {
            for (Product product : products) {
                JSONObject object = new JSONObject();
                object.put("declarationItemId", product.getID())
                        .put("declarationItemName", product.getProductName())
                        .put("declarationItemQuantity", product.getProductQuantity())
                        .put("declarationItemPrice", product.getProductPrice());
                productsJSON.put(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productsJSON;
    }

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
                            Insurance insurance = new Insurance.Builder()
                                    .currency(data.getString("currency"))
                                    .commission(data.getDouble("commission"))
                                    .shippingCost(data.getDouble("shippingCost"))
                                    .totalPriceBoxes(data.getDouble("totalPriceBoxes"))
                                    .isInsuranceSelected(data.getBoolean("insuranceSelected"))
                                    .isInsuranceAvailable(data.getBoolean("insuranceAvailable"))
                                    .declarationTotalPrice(data.getDouble("declarationTotalPrice"))
                                    .build();

                            mProductsPriceLabel.setText(insurance.getCurrency() + insurance.getTotalPriceBoxes());
                            mShippingPriceLabel.setText(insurance.getCurrency() + insurance.getShippingCost());
                            mInsurancePriceLabel.setText(insurance.getCurrency() + insurance.getCommission());
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

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.select_insurance), true);
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
            case R.id.insuranceSelector:
                needInsurance = true;
                mNeedInSurance.setChecked(true);
                mRefuseInsurance.setChecked(false);
                break;
            case R.id.noInsuranceSelector:
                needInsurance = false;
                mNeedInSurance.setChecked(false);
                mRefuseInsurance.setChecked(true);
                break;
            case R.id.insuranceConfirmButton:
                mInForming.setNeedInsurance(needInsurance);
                mActivity.addFragment(ConfirmationFragment.newInstance(mInForming), true);
                break;
            case R.id.insuranceDetailsButton:
                if (mNeedInsuranceDescription.getLineCount() == 4) {
                    mActivity.expandTextView(mNeedInsuranceDescription);
                    mNeedInsuranceDetails.setText(getString(R.string.hide_details));
                } else {
                    mActivity.collapseTextView(mNeedInsuranceDescription, 4);
                    mNeedInsuranceDetails.setText(getString(R.string.details));
                }
                break;
            case R.id.noInsuranceDetailsButton:
                if (mRefuseInsuranceDescription.getLineCount() == 4) {
                    mActivity.expandTextView(mRefuseInsuranceDescription);
                    mRefuseInsuranceDetails.setText(getString(R.string.hide_details));
                } else {
                    mActivity.collapseTextView(mRefuseInsuranceDescription, 4);
                    mRefuseInsuranceDetails.setText(getString(R.string.details));
                }
                break;
        }
    }
}
