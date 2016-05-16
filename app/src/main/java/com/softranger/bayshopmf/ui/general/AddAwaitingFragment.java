package com.softranger.bayshopmf.ui.general;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.InProcessingParcel;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAwaitingFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private EditText mProductUrlInput;
    private EditText mProductTrackingNumInput;
    private EditText mProductNameInput;
    private EditText mProductPriceInput;

    private InProcessingParcel.Builder mParcelBuilder;
    private MainActivity mActivity;

    public AddAwaitingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        mActivity = (MainActivity) getActivity();
        mProductUrlInput = (EditText) view.findViewById(R.id.addAwaitingLinkToProductInput);
        mProductTrackingNumInput = (EditText) view.findViewById(R.id.addAwaitingTrackingInput);
        mProductNameInput = (EditText) view.findViewById(R.id.addAwaitingNameInput);
        mProductPriceInput = (EditText) view.findViewById(R.id.addAwaitingPriceInput);
        RadioGroup storageSelector = (RadioGroup) view.findViewById(R.id.addAwaitingStorageSelectorGroup);
        storageSelector.setOnCheckedChangeListener(this);
        Button addParcelBtn = (Button) view.findViewById(R.id.addAwaitingAddParcelButton);
        addParcelBtn.setOnClickListener(this);
        mParcelBuilder = new InProcessingParcel.Builder();
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.addAwaitingUsaSelector:
                mParcelBuilder.deposit(Constants.USA);
                break;
            case R.id.addAwaitingUkSelector:
                mParcelBuilder.deposit(Constants.UK);
                break;
            case R.id.addAwaitingDeSelector:
                mParcelBuilder.deposit(Constants.DE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addAwaitingAddParcelButton) {
            String productUrl = String.valueOf(mProductUrlInput.getText());
            String trackingNum = String.valueOf(mProductTrackingNumInput.getText());
            String productName = String.valueOf(mProductNameInput.getText());
            String productPrice = String.valueOf(mProductPriceInput.getText());
            mParcelBuilder.totalPrice(productPrice)
                    .trackingNumber(trackingNum)
                    .productName(productName)
                    .url(productUrl);
            // TODO: 5/16/16 send parcel to server
            mActivity.onBackPressed();
        }
    }
}
