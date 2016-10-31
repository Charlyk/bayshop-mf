package com.softranger.bayshopmf.ui.calculator;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ShippingMethodAdapter;
import com.softranger.bayshopmf.model.CalculatorResult;
import com.softranger.bayshopmf.model.Shipper;
import com.softranger.bayshopmf.model.address.Country;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.addresses.CountriesDialogFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class ShippingCalculatorActivity extends ParentActivity implements CountriesDialogFragment.OnCountrySelectListener {

    @BindView(R.id.calculatorWeightInput)
    EditText mWeightInput;
    @BindView(R.id.calculatorVolumeXInput)
    EditText mXInput;
    @BindView(R.id.calculatorVolumeYInput)
    EditText mYInput;
    @BindView(R.id.calculatorVolumeZInput)
    EditText mZInput;
    @BindView(R.id.shippingCalculatorResultLayout)
    RelativeLayout mResultLayout;
    @BindView(R.id.calculatorCountryNameLabel)
    TextView mCountryNameLabel;
    @BindView(R.id.calculatorResetButton)
    LinearLayout mResetBtn;

    private ShippingMethodAdapter mAdapter;
    private static String selectedStorage;

    private ArrayList<Shipper> mShippers;

    private double mWeight;
    private double X;
    private double Y;
    private double Z;

    private String mCountryId;
    private Call<ServerResponse<CalculatorResult>> mCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_calculator);
        ButterKnife.bind(this);
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        selectedStorage = Constants.US;

        mShippers = new ArrayList<>();
        mAdapter = new ShippingMethodAdapter(mShippers, Constants.USD_SYMBOL);
        mAdapter.setCalculatorPrice(true);
        RecyclerView recyclerView = ButterKnife.findById(this, R.id.calculatorShippingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        toggleResultVisibility(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Application.user != null) {
            Country country = Application.user.getCountries().get(0);
            mCountryId = String.valueOf(country.getId());
            mCountryNameLabel.setText(country.getName());
        }
    }

    @OnClick(R.id.countryBtnLayout)
    void selectCountry() {
        if (Application.user.getCountries() != null) {
            CountriesDialogFragment dialogFragment = CountriesDialogFragment.newInstance(Application.user.getCountries());
            dialogFragment.setOnCountrySelectListener(this);
            dialogFragment.show(getSupportFragmentManager(), this.getClass().getSimpleName());
        }
    }

    private void toggleResultVisibility(boolean show) {
        mResultLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mResetBtn.setEnabled(show);
        mResetBtn.setClickable(show);
    }

    @OnTextChanged(value = R.id.calculatorVolumeXInput, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void volumeXTextChanged(CharSequence cs) {
        try {
            X = Double.parseDouble(cs.toString());
            computeShippingCost();
        } catch (Exception e) {
            mXInput.setError(getString(R.string.number_format_error));
        }
    }

    @OnTextChanged(value = R.id.calculatorVolumeYInput, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void volumeYTextChanged(CharSequence cs) {
        try {
            Y = Double.parseDouble(cs.toString());
            computeShippingCost();
        } catch (Exception e) {
            mYInput.setError(getString(R.string.number_format_error));
        }
    }

    @OnTextChanged(value = R.id.calculatorVolumeZInput, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void volumeZTextChanged(CharSequence cs) {
        try {
            Z = Double.parseDouble(cs.toString());
            computeShippingCost();
        } catch (Exception e) {
            mZInput.setError(getString(R.string.number_format_error));
        }
    }

    @OnTextChanged(value = R.id.calculatorWeightInput, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void weightTextChanged(CharSequence cs) {
        try {
            mWeight = Double.parseDouble(cs.toString());
            computeShippingCost();
        } catch (Exception e) {
            mWeightInput.setError(getString(R.string.number_format_error));
        }
    }

    private void computeShippingCost() {
        mCall = Application.apiInterface().computeShippingCost(selectedStorage, mCountryId, mWeight, X, Y, Z);
        mCall.enqueue(mResponseCallback);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.calculatorResetButton)
    void resetInputs() {
        mShippers.clear();
        mWeightInput.setText("");
        mXInput.setText("");
        mYInput.setText("");
        mZInput.setText("");
        mAdapter.refreshList();
        toggleResultVisibility(false);
    }

    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {

    }

    @Override
    public void toggleLoadingProgress(boolean show) {

    }

    @Override
    public void replaceFragment(ParentFragment fragment) {

    }

    @Override
    public void onBackStackChanged() {

    }

    @Override
    public void onCountrySelected(Country country) {
        mCountryNameLabel.setText(country.getName());
        mCountryId = String.valueOf(country.getId());
        computeShippingCost();
    }

    private ResponseCallback<CalculatorResult> mResponseCallback = new ResponseCallback<CalculatorResult>() {
        @Override
        public void onSuccess(CalculatorResult data) {
            toggleResultVisibility(data != null && data.getShippers().size() > 0);

            mShippers.clear();
            if (data != null) {
                mShippers.addAll(data.getShippers());
            }

            mAdapter.refreshList();
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(ShippingCalculatorActivity.this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Call<ServerResponse<CalculatorResult>> call, Throwable t) {
            Toast.makeText(ShippingCalculatorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCall != null) mCall.cancel();
    }
}
