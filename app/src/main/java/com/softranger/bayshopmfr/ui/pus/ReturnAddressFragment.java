package com.softranger.bayshopmfr.ui.pus;


import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.pus.PUSParcelDetailed;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.network.BayShopApiInterface;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReturnAddressFragment extends ParentFragment implements Callback<ServerResponse> {

    private static final String DETAILED_PARCEL = "detailed pus parcel";
    private ParentActivity mActivity;
    private Unbinder mUnbinder;
    private BayShopApiInterface mApiInterface;
    private PUSParcelDetailed mPUSParcelDetailed;

    private Call<ServerResponse> mResponseCall;

    @BindView(R.id.firstNameInputFocusIndicator) View mFocusIndicator;
    @BindView(R.id.returnFirstNameInput) TextInputEditText mFirstName;
    @BindView(R.id.returnLastNameInput) TextInputEditText mLastName;
    @BindView(R.id.returnAddressInput) TextInputEditText mAddress;
    @BindView(R.id.returnCityInput) TextInputEditText mCity;
    @BindView(R.id.returnCountryInput) TextInputEditText mCountry;
    @BindView(R.id.returnStateInput) TextInputEditText mState;
    @BindView(R.id.returnPostalCodeInput) TextInputEditText mPostalCode;
    @BindView(R.id.returnPhoneInput) TextInputEditText mPhoneNumber;

    @BindView(R.id.returnFirstNameLayout) RelativeLayout mFirstNameLayout;
    @BindView(R.id.returnLastNameLayout) RelativeLayout mLastNameLayout;
    @BindView(R.id.returnAddressLayout) RelativeLayout mAddressLayout;
    @BindView(R.id.returnCityLayout) RelativeLayout mCityLayout;
    @BindView(R.id.returnCountryLayout) RelativeLayout mCountryLayout;
    @BindView(R.id.returnStateLayout) RelativeLayout mStateLayout;
    @BindView(R.id.returnPostalCodeLayout) RelativeLayout mPostalCodeLayout;
    @BindView(R.id.returnPhoneNumberLayout) RelativeLayout mPhoneLayout;

    public ReturnAddressFragment() {
        // Required empty public constructor
    }

    public static ReturnAddressFragment newInstance(PUSParcelDetailed pusParcelDetailed) {
        Bundle args = new Bundle();
        args.putParcelable(DETAILED_PARCEL, pusParcelDetailed);
        ReturnAddressFragment fragment = new ReturnAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_return_address, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (ParentActivity) getActivity();

        IntentFilter intentFilter = new IntentFilter(Application.ACTION_RETRY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mPUSParcelDetailed = getArguments().getParcelable(DETAILED_PARCEL);

        mFocusIndicator.setVisibility(View.VISIBLE);

        mFirstName.requestFocus();

        mApiInterface = Application.apiInterface();

        return view;
    }

    @OnClick(R.id.returnConfirmBtn)
    void confirmReturningToSeller() {
        // get all user inputs
        String firstName = getTextFrom(mFirstName);
        String lastName = getTextFrom(mLastName);
        String address = getTextFrom(mAddress);
        String city = getTextFrom(mCity);
        String country = getTextFrom(mCountry);
        String state = getTextFrom(mState);
        String postalCode = getTextFrom(mPostalCode);
        String phoneNumber = getTextFrom(mPhoneNumber);

        // check them if they are filled
        if (isEmpty(firstName)) {
            mFirstName.setError(getString(R.string.enter_first_name));
            return;
        }
        if (isEmpty(lastName)) {
            mLastName.setError(getString(R.string.enter_last_name));
            return;
        }
        if (isEmpty(address)) {
            mAddress.setError(getString(R.string.enter_street_name));
            return;
        }
        if (isEmpty(city)) {
            mCity.setError(getString(R.string.enter_city));
            return;
        }
        if (isEmpty(country)) {
            mCountry.setError(getString(R.string.enter_country_name));
            return;
        }
        if (isEmpty(state)) {
            mState.setError(getString(R.string.enter_state));
            return;
        }
        if (isEmpty(postalCode)) {
            mPostalCode.setError(getString(R.string.enter_postal_code));
            return;
        }
        if (isEmpty(phoneNumber)) {
            mPhoneNumber.setError(getString(R.string.enter_phone_number));
            return;
        }

        mResponseCall = mApiInterface.returnToSellerAddress(
                mPUSParcelDetailed.getId(), firstName, lastName,
                address, city, country, postalCode, phoneNumber, state);
        mActivity.toggleLoadingProgress(true);
        mResponseCall.enqueue(this);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                    confirmReturningToSeller();
                    break;
            }
        }
    };

    private String getTextFrom(TextInputEditText inputEditText) {
        return String.valueOf(inputEditText.getText());
    }

    private boolean isEmpty(String value) {
        return value.equals("");
    }

    @OnFocusChange({R.id.returnFirstNameInput, R.id.returnLastNameInput, R.id.returnAddressInput,
            R.id.returnCityInput, R.id.returnCountryInput, R.id.returnStateInput,
            R.id.returnPostalCodeInput, R.id.returnPhoneInput})
    void onTextFieldsFocusChanged(View view, boolean hasFocus) {
        if (mFocusIndicator.getVisibility() != View.VISIBLE) {
            mFocusIndicator.setVisibility(View.VISIBLE);
        }
        if (!hasFocus) return;
        switch (view.getId()) {
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
    public void onDetach() {
        super.onDetach();
        mActivity.hideKeyboard();
        if (mResponseCall != null) mResponseCall.cancel();
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.return_to_seller_s_address);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.return_to_seller;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mResponseCall != null) mResponseCall.cancel();
        mUnbinder.unbind();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response ) {
        if (response.body() != null) {
            ServerResponse serverResponse = response.body();
            if (serverResponse.getMessage().equalsIgnoreCase(Constants.ApiResponse.OK_MESSAGE)) {
                mActivity.showResultActivity(getString(R.string.request_received),
                        R.mipmap.ic_confirm_prohibition_250dp, getString(R.string.return_request_received));
            } else {
                Toast.makeText(mActivity, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                ServerResponse serverResponse = new ObjectMapper().readValue(response.errorBody().string(), ServerResponse.class);
                Toast.makeText(mActivity, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public void onFailure(Call<ServerResponse> call, Throwable t) {
        if (t instanceof ConnectException || t instanceof UnknownHostException) {
            Application.getInstance().sendBroadcast(new Intent(ResponseCallback.ACTION_NO_CONNECTION));
        } else {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        mActivity.toggleLoadingProgress(false);
    }
}
