package com.softranger.bayshopmf.ui.calculator;

import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.CalculatorAdapter;
import com.softranger.bayshopmf.model.address.Country;
import com.softranger.bayshopmf.ui.addresses.CountriesDialogFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class ShippingCalculatorActivity extends ParentActivity implements TextWatcher,
        View.OnClickListener, CountriesDialogFragment.OnCountrySelectListener {

    @BindView(R.id.calculatorWeightInput)
    EditText mWeightInput;
    @BindView(R.id.calculatorVolumeXInput)
    EditText mXInput;
    @BindView(R.id.calculatorVolumeYInput)
    EditText mYInput;
    @BindView(R.id.calculatorVolumeZInput)
    EditText mZInput;
    @BindView(R.id.calculatorCountryNameLabel)
    TextView mCountryNameLabel;
    private CalculatorAdapter mAdapter;
    private static String selectedStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_calculator);
        ButterKnife.bind(this);
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        selectedStorage = Constants.US;

        mWeightInput.addTextChangedListener(this);
        mXInput.addTextChangedListener(this);
        mYInput.addTextChangedListener(this);
        mZInput.addTextChangedListener(this);

        mCountryNameLabel = (TextView) findViewById(R.id.calculatorCountryNameLabel);

        mAdapter = new CalculatorAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.calculatorShippingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

    }

    //------------------- Inputs listener -------------------//
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String weight = String.valueOf(mWeightInput.getText());
        String x = String.valueOf(mXInput.getText());
        String y = String.valueOf(mYInput.getText());
        String z = String.valueOf(mZInput.getText());
        String countryName = String.valueOf(mCountryNameLabel.getText());

        getShippingPriceFromServer(weight, x, y, z, countryName, selectedStorage);
    }

    @OnClick(R.id.countryBtnLayout)
    void selectCountry() {
        if (Application.user.getCountries() != null) {
            CountriesDialogFragment dialogFragment = CountriesDialogFragment.newInstance(Application.user.getCountries());
            dialogFragment.setOnCountrySelectListener(this);
            dialogFragment.show(getSupportFragmentManager(), this.getClass().getSimpleName());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //------------------- Click listener -------------------//
    @Override
    public void onClick(View v) {

    }

    public void getShippingPriceFromServer(String weight, String x, String y, String z, String countryName, String storage) {

    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {

                        } else {
                            Toast.makeText(ShippingCalculatorActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ShippingCalculatorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(ShippingCalculatorActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(ShippingCalculatorActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

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
    }
}
