package com.softranger.bayshopmf.ui.awaitingarrival;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditAwaitingFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String PRODUCT_ARG = "product argument";

    private EditText mNameInput, mTrackingInput, mUrlInput, mPriceInput;
    private RadioButton mUsaSelector, mUkSelector, mDeSelector;
    private RadioGroup mStorageSelector;
    private Button mSaveButton;
    private static Product product;
    private MainActivity mActivity;
    private View mRootView;

    public EditAwaitingFragment() {
        // Required empty public constructor
    }

    public static EditAwaitingFragment newInstance(Product product) {
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_ARG, product);
        EditAwaitingFragment fragment = new EditAwaitingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_edit_awaiting, container, false);
        mActivity = (MainActivity) getActivity();
        product = getArguments().getParcelable(PRODUCT_ARG);
        bindViews(mRootView);
        mNameInput.setText(product.getProductName());
        mTrackingInput.setText(product.getTrackingNumber());
        mUrlInput.setText(product.getProductUrl());
        mPriceInput.setText(product.getProductPrice());
        mActivity.setToolbarTitle(product.getProductId(), true);

        switch (product.getDeposit()) {
            case Constants.USA:
                mUsaSelector.setChecked(true);
                break;
            case Constants.UK:
                mUkSelector.setChecked(true);
                break;
            case Constants.DE:
                mDeSelector.setChecked(true);
        }
        return mRootView;
    }

    private void bindViews(View view) {
        mNameInput = (EditText) view.findViewById(R.id.editAwaitingNameInput);
        mTrackingInput = (EditText) view.findViewById(R.id.editAwaitingTrackingInput);
        mUrlInput = (EditText) view.findViewById(R.id.editAwaitingUrlInput);
        mPriceInput = (EditText) view.findViewById(R.id.editAwaitingPriceInput);

        mSaveButton = (Button) view.findViewById(R.id.editAwaitingSaveButton);
        mSaveButton.setOnClickListener(this);

        mStorageSelector = (RadioGroup) view.findViewById(R.id.editAwaitingStorageSelectorGroup);
        mStorageSelector.setOnCheckedChangeListener(this);

        mUsaSelector = (RadioButton) view.findViewById(R.id.editAwaitingUsaSelector);
        mUkSelector = (RadioButton) view.findViewById(R.id.editAwaitingUkSelector);
        mDeSelector = (RadioButton) view.findViewById(R.id.editAwaitingDeSelector);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.editAwaitingUsaSelector:
                product.setDeposit(Constants.USA);
                break;
            case R.id.editAwaitingUkSelector:
            case R.id.editAwaitingDeSelector:
                Snackbar.make(mRootView, getString(R.string.not_suported), Snackbar.LENGTH_SHORT).show();
                mUsaSelector.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        String productName = String.valueOf(mNameInput.getText());
        if (productName == null || productName.equals("")) {
            mNameInput.setError("please specify a name for this item");
            return;
        }

        String trackingNumber = String.valueOf(mTrackingInput.getText());
        if (trackingNumber == null || trackingNumber.equals("")) {
            mTrackingInput.setError("please specify a track number for this item");
            return;
        }

        String urlToProduct = String.valueOf(mUrlInput.getText());
        if (urlToProduct == null || urlToProduct.equals("")) {
            mUrlInput.setError("please specify a url for this item");
            return;
        }

        String price = String.valueOf(mPriceInput.getText());
        if (price == null || price.equals("")) {
            mPriceInput.setError("please specify a price for this item");
            return;
        }

        product.setProductName(productName);
        product.setTrackingNumber(trackingNumber);
        product.setProductUrl(urlToProduct);
        product.setProductPrice(price);
        RequestBody body = new FormBody.Builder()
                .add("storage", product.getDeposit())
                .add("tracking", product.getTrackingNumber())
                .add("title", product.getProductName())
                .add("url", product.getProductUrl())
                .add("packagePrice", product.getProductPrice())
                .build();
        ApiClient.getInstance().sendRequest(body, Constants.Api.editWaitingMfItem(String.valueOf(product.getID())), mEdithandler);
        mActivity.toggleLoadingProgress(true);
    }

    private Handler mEdithandler = new Handler(Looper.getMainLooper()) {
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
                            product.setProductName(data.getString("packageName"));
                            product.setID(Integer.parseInt(data.getString("id")));
                            product.setDeposit(data.getString("storage").toLowerCase());
                            product.setTrackingNumber(data.getString("tracking"));
                            product.setProductPrice(data.getString("packagePrice"));
                            product.setProductUrl(data.getString("url"));
                            product.setBarcode(data.getString("barCode"));
                            Snackbar.make(mRootView, getString(R.string.saved_succesfuly), Snackbar.LENGTH_SHORT).show();
                            mActivity.onBackPressed();
                        } else {
                            Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRootView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    mActivity.toggleLoadingProgress(false);
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
                case Constants.ApiResponse.RESONSE_UNAUTHORIZED: {
                    mActivity.logOut();
                }
            }
            mActivity.toggleLoadingProgress(false);
        }
    };
}
