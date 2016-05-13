package com.softranger.bayshopmf.ui.awaitingarrival;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.Constants;

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
        View view = inflater.inflate(R.layout.fragment_edit_awaiting, container, false);
        mActivity = (MainActivity) getActivity();
        product = getArguments().getParcelable(PRODUCT_ARG);
        bindViews(view);
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
        return view;
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
                product.setDeposit(Constants.UK);
                break;
            case R.id.editAwaitingDeSelector:
                product.setDeposit(Constants.DE);
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
        mActivity.onBackPressed();
    }
}
