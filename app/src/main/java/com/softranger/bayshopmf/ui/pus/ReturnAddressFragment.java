package com.softranger.bayshopmf.ui.pus;


import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.ParentActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReturnAddressFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {

    private ParentActivity mActivity;
    private View mFocusIndicator;
    private TextInputEditText mFirstName;
    private TextInputEditText mLastName;
    private TextInputEditText mAddress;
    private TextInputEditText mCity;
    private TextInputEditText mCountry;
    private TextInputEditText mState;
    private TextInputEditText mPostalCode;
    private TextInputEditText mPhoneNumber;

    private RelativeLayout mFirstNameLayout, mLastNameLayout, mAddressLayout, mCityLayout, mCountryLayout,
            mStateLayout, mPostalCodeLayout, mPhoneLayout;

    public ReturnAddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_return_address, container, false);

        mActivity = (ParentActivity) getActivity();

        mFocusIndicator = view.findViewById(R.id.firstNameInputFocusIndicator);
        mFocusIndicator.setVisibility(View.VISIBLE);

        mFirstName = (TextInputEditText) view.findViewById(R.id.returnFirstNameInput);
        mFirstName.requestFocus();
        mFirstName.setOnFocusChangeListener(this);
        mLastName = (TextInputEditText) view.findViewById(R.id.returnLastNameInput);
        mLastName.setOnFocusChangeListener(this);
        mAddress = (TextInputEditText) view.findViewById(R.id.returnAddressInput);
        mAddress.setOnFocusChangeListener(this);
        mCity = (TextInputEditText) view.findViewById(R.id.returnCityInput);
        mCity.setOnFocusChangeListener(this);
        mCountry = (TextInputEditText) view.findViewById(R.id.returnCountryInput);
        mCountry.setOnFocusChangeListener(this);
        mState = (TextInputEditText) view.findViewById(R.id.returnStateInput);
        mState.setOnFocusChangeListener(this);
        mPostalCode = (TextInputEditText) view.findViewById(R.id.returnPostalCodeInput);
        mPostalCode.setOnFocusChangeListener(this);
        mPhoneNumber = (TextInputEditText) view.findViewById(R.id.returnPhoneInput);
        mPhoneNumber.setOnFocusChangeListener(this);

        mFirstNameLayout = (RelativeLayout) view.findViewById(R.id.returnFirstNameLayout);
        mLastNameLayout = (RelativeLayout) view.findViewById(R.id.returnLastNameLayout);
        mAddressLayout = (RelativeLayout) view.findViewById(R.id.returnAddressLayout);
        mCityLayout = (RelativeLayout) view.findViewById(R.id.returnCityLayout);
        mCountryLayout = (RelativeLayout) view.findViewById(R.id.returnCountryLayout);
        mStateLayout = (RelativeLayout) view.findViewById(R.id.returnStateLayout);
        mPostalCodeLayout = (RelativeLayout) view.findViewById(R.id.returnPostalCodeLayout);
        mPhoneLayout = (RelativeLayout) view.findViewById(R.id.returnPhoneNumberLayout);

        Button confirmBtn = (Button) view.findViewById(R.id.returnConfirmBtn);
        confirmBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.returnFirstNameInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mFirstNameLayout.getY()).setDuration(300).start();
                break;
            case R.id.returnLastNameInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mLastNameLayout.getY()).setDuration(300).start();
                break;
            case R.id.returnAddressInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mAddressLayout.getY()).setDuration(300).start();
                break;
            case R.id.returnCityInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mCityLayout.getY()).setDuration(300).start();
                break;
            case R.id.returnCountryInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mCountryLayout.getY()).setDuration(300).start();
                break;
            case R.id.returnStateInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mStateLayout.getY()).setDuration(300).start();
                break;
            case R.id.returnPostalCodeInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mPostalCodeLayout.getY()).setDuration(300).start();
                break;
            case R.id.returnPhoneInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mPhoneLayout.getY()).setDuration(300).start();
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.hideKeyboard();
        Intent intent = new Intent(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.sendBroadcast(intent);
    }
}