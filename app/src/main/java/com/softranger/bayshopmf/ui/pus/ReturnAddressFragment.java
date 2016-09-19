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
import com.softranger.bayshopmf.ui.general.ResultActivity;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReturnAddressFragment extends ParentFragment implements View.OnFocusChangeListener, View.OnClickListener {

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
        if (mFocusIndicator.getVisibility() != View.VISIBLE) {
            mFocusIndicator.setVisibility(View.VISIBLE);
        }

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
        mFocusIndicator.setVisibility(View.GONE);
        // TODO: 8/24/16 replace with translated text
        // build intent for result activity
        Intent showResult = new Intent(mActivity, ResultActivity.class);
        showResult.putExtra(ResultActivity.TOP_TITLE, "Request received");
        showResult.putExtra(ResultActivity.SECOND_TITLE, "Your request was received by our manager.");
        showResult.putExtra(ResultActivity.IMAGE_ID, R.mipmap.ic_parcel_25dp);
        showResult.putExtra(ResultActivity.DESCRIPTION, "Thanks for providing information about seller, we will send the parcel back shortly.");

        // close fragment
        mActivity.onBackPressed();

        // show result activity
        startActivity(showResult);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.hideKeyboard();
        Intent intent = new Intent(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.sendBroadcast(intent);
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {

    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.return_to_seller_s_address);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.return_to_seller;
    }
}
