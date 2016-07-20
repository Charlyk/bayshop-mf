package com.softranger.bayshopmf.ui.addresses;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WarehouseAddressFragment extends Fragment implements View.OnClickListener {


    private static final String STORAGE = "storage argument";

    private TextView mFullName, mLineOne, mLineTwo, mCity, mState, mPostalCode, mCountry, mPhone;

    private WarehouseAddressesActivity mActivity;

    private String mStorage;

    public WarehouseAddressFragment() {
        // Required empty public constructor
    }

    public static WarehouseAddressFragment newInstance(String storage) {
        Bundle args = new Bundle();
        args.putString(STORAGE, storage);
        WarehouseAddressFragment fragment = new WarehouseAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_warehouse_address, container, false);

        mActivity = (WarehouseAddressesActivity) getActivity();

        mStorage = getArguments().getString(STORAGE);

        Button fullNameCopy = (Button) view.findViewById(R.id.addressesFullNameCopyButton);
        Button lineOneCopy = (Button) view.findViewById(R.id.addressesLineOneCopyButton);
        Button lineTwoCopy = (Button) view.findViewById(R.id.addressesLineTwoCopyButton);
        Button cityCopy = (Button) view.findViewById(R.id.addressesCityCopyButton);
        Button stateCopy = (Button) view.findViewById(R.id.addressesStateCopyButton);
        Button postalCopy = (Button) view.findViewById(R.id.addressesPostalCodeCopyButton);
        Button countryCopy = (Button) view.findViewById(R.id.addressesCountryCopyButton);
        Button phoneCopy = (Button) view.findViewById(R.id.addressesPhoneNumberCopyButton);

        fullNameCopy.setOnClickListener(this);
        lineOneCopy.setOnClickListener(this);
        lineTwoCopy.setOnClickListener(this);
        cityCopy.setOnClickListener(this);
        stateCopy.setOnClickListener(this);
        postalCopy.setOnClickListener(this);
        countryCopy.setOnClickListener(this);
        phoneCopy.setOnClickListener(this);

        mFullName = (TextView) view.findViewById(R.id.addressesFullNameLabel);
        mLineOne = (TextView) view.findViewById(R.id.addressesLineOneLabel);
        mLineTwo = (TextView) view.findViewById(R.id.addressesLineTwoLabel);
        mCity = (TextView) view.findViewById(R.id.addressesCityLabel);
        mState = (TextView) view.findViewById(R.id.addressesStateLabel);
        mPostalCode = (TextView) view.findViewById(R.id.addressesPostalCodeLabel);
        mCountry = (TextView) view.findViewById(R.id.addressesCountryLabel);
        mPhone = (TextView) view.findViewById(R.id.addressesPhoneNumberLabel);
        return view;
    }

    @Override
    public void onClick(View v) {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager)
                mActivity.getSystemService(Context.CLIPBOARD_SERVICE);

        String copiedText = "";

        switch (v.getId()) {
            case R.id.addressesFullNameCopyButton:
                copiedText = String.valueOf(mFullName.getText());
                break;
            case R.id.addressesLineOneCopyButton:
                copiedText = String.valueOf(mLineOne.getText());
                break;
            case R.id.addressesLineTwoCopyButton:
                copiedText = String.valueOf(mLineTwo.getText());
                break;
            case R.id.addressesCityCopyButton:
                copiedText = String.valueOf(mCity.getText());
                break;
            case R.id.addressesStateCopyButton:
                copiedText = String.valueOf(mState.getText());
                break;
            case R.id.addressesPostalCodeCopyButton:
                copiedText = String.valueOf(mPostalCode.getText());
                break;
            case R.id.addressesCountryCopyButton:
                copiedText = String.valueOf(mCountry.getText());
                break;
            case R.id.addressesPhoneNumberCopyButton:
                copiedText = String.valueOf(mPhone.getText());
                break;
        }

        if (copiedText.length() > 0) {
            // Creates a new text clip to put on the clipboard
            ClipData clip = ClipData.newPlainText("simple text", copiedText);
            // Set the clipboard's primary clip.
            clipboard.setPrimaryClip(clip);

            Toast.makeText(mActivity, getString(R.string.copied), Toast.LENGTH_SHORT).show();
        }
    }
}
