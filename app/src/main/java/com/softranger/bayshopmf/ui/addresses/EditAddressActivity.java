package com.softranger.bayshopmf.ui.addresses;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.address.Address;
import com.softranger.bayshopmf.model.address.AddressToEdit;
import com.softranger.bayshopmf.model.address.Country;
import com.softranger.bayshopmf.model.address.CountryCode;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.util.Application;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import retrofit2.Call;

public class EditAddressActivity extends AppCompatActivity implements CodesDialogFragment.OnCodeSelectedListener,
        CountriesDialogFragment.OnCountrySelectListener {

    public static final String ADDRESS_ID_EXTRA = "ADDRESS ID";

    public static final String ACTION_REFRESH_ADDRESS = "REFRESH ADDRESSES LIST";

    @BindView(R.id.addAddressFirstNameInput) TextInputEditText mFirstNameInput;
    @BindView(R.id.addAddressLastNameInput) TextInputEditText mLastNameInput;
    @BindView(R.id.addAddressStreetInput) TextInputEditText mStreetNameInput;
    @BindView(R.id.addAddressPhoneNumberInput) TextInputEditText mPhoneInput;
    @BindView(R.id.addAddressCityInput) TextInputEditText mCityInput;
    @BindView(R.id.addAddressPostalCodeInput) TextInputEditText mPostalCodeInput;
    @BindView(R.id.addAddressEmailInput) TextInputEditText mEmailInput;

    @BindView(R.id.editAddressFirstNameInputLayout) TextInputLayout mFirstNameLayout;
    @BindView(R.id.editAddressLastNameLayout) TextInputLayout mLastNameLayout;
    @BindView(R.id.editAddressStreetLayout) TextInputLayout mStreetLayout;
    @BindView(R.id.editAddressPhoneNumberLayout) TextInputLayout mPhoneLayout;
    @BindView(R.id.editAddressCityLayout) TextInputLayout mCityLayout;
    @BindView(R.id.editAddressPostalCodeLayout) TextInputLayout mPostalCodeLayout;
    @BindView(R.id.editAddressEmailLayout) TextInputLayout mEmailLayout;

    @BindView(R.id.editAddressInputFocusIndicator) View mFocusIndicator;

    @BindView(R.id.phoneCodeTextlabel) TextView mPhoneCode;
    @BindView(R.id.addAddressCountrylabel) TextView mCountryLabel;
    @BindView(R.id.editAddressToolbarTitle) TextView mToolbarTitle;

    @BindView(R.id.editAddressToolbar) Toolbar mToolbar;
    @BindView(R.id.editAddressProgressBar) ProgressBar mProgressBar;
    @BindView(R.id.editAddressFieldsHolder) ScrollView mScrollView;

    private Call<ServerResponse<AddressToEdit>> mResponseCall;
    private int mAddressId;
    private AddressToEdit mAddress;
    private boolean mIsSaveClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        ButterKnife.bind(this);

        mToolbar.setNavigationOnClickListener((view) -> {
            onBackPressed();
        });

        Intent intent = getIntent();

        if (intent.hasExtra(ADDRESS_ID_EXTRA)) {
            mScrollView.setVisibility(View.GONE);
            mAddressId = intent.getExtras().getInt(ADDRESS_ID_EXTRA);
            mToolbarTitle.setText(getString(R.string.edit_address));
            mResponseCall = Application.apiInterface().getMemberAddress(String.valueOf(mAddressId));
        } else {
            mToolbarTitle.setText(getString(R.string.add_new_address));
            mAddressId = -1;
            mResponseCall = Application.apiInterface().getPhoneCodes();
        }

        toggleLoading(true);
        mResponseCall.enqueue(mResponseCallback);
    }

    private void toggleLoading(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private ResponseCallback<AddressToEdit> mResponseCallback = new ResponseCallback<AddressToEdit>() {
        @Override
        public void onSuccess(AddressToEdit data) {
            mAddress = data;
            toggleLoading(false);
            setDataOnPosition(mAddress.getAddress());
            if (mScrollView.getVisibility() != View.VISIBLE) mScrollView.setVisibility(View.VISIBLE);
            if (mIsSaveClicked) {
                sendBroadcast(new Intent(ACTION_REFRESH_ADDRESS));
                onBackPressed();
            }
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(EditAddressActivity.this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            toggleLoading(false);
            mIsSaveClicked = false;
        }

        @Override
        public void onError(Call<ServerResponse<AddressToEdit>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(EditAddressActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            toggleLoading(false);
            mIsSaveClicked = false;
        }
    };

    private void setDataOnPosition(Address address) {
        if (address == null) return;
        mFirstNameInput.setText(address.getFirstName());
        mLastNameInput.setText(address.getLastName());
        mEmailInput.setText(address.getEmail());
        mStreetNameInput.setText(address.getStreet());
        mPhoneInput.setText(address.getPhoneNumber());
        mCityInput.setText(address.getCity());
        mPhoneCode.setText(address.getPhoneCode());
        mCountryLabel.setText(address.getState());
        mPostalCodeInput.setText(address.getPostalCode());
    }

    @OnClick(R.id.addAddressSaveAddressButton)
    void saveAddressToServer() {
        String firstName = String.valueOf(mFirstNameInput.getText());
        if (firstName.equals("")) {
            mFirstNameInput.setError(getString(R.string.enter_first_name));
            return;
        }

        String lastName = String.valueOf(mLastNameInput.getText());
        if (lastName.equals("")) {
            mLastNameInput.setError(getString(R.string.enter_last_name));
            return;
        }

        String email = String.valueOf(mEmailInput.getText());
        if (email.equals("")) {
            mEmailInput.setError(getString(R.string.enter_valid_email));
            return;
        }

        String streetName = String.valueOf(mStreetNameInput.getText());
        if (streetName.equals("")) {
            mStreetNameInput.setError(getString(R.string.enter_street_name));
            return;
        }

        String countryCode = String.valueOf(mPhoneCode.getText());
        if (countryCode.equals("")) {
            Toast.makeText(this, getString(R.string.enter_country_code), Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = String.valueOf(mPhoneInput.getText());
        if (phoneNumber.equals("")) {
            mPhoneInput.setError(getString(R.string.enter_phone_number));
            return;
        }

        String city = String.valueOf(mCityInput.getText());
        if (city.equals("")) {
            mCityInput.setError(getString(R.string.enter_city));
            return;
        }

        String country = String.valueOf(mCountryLabel.getText());
        if (country.equals("")) {
            Toast.makeText(this, getString(R.string.enter_country_name), Toast.LENGTH_SHORT).show();
            return;
        }

        String postalCode = String.valueOf(mPostalCodeInput.getText());
        if (postalCode.equals("")) {
            mPostalCodeInput.setError(getString(R.string.enter_postal_code));
            return;
        }

        mResponseCall = Application.apiInterface().saveMemberAddress(mAddressId > -1 ? String.valueOf(mAddress) : "",
                firstName, lastName, email, streetName, city, postalCode, countryCode, phoneNumber, country,
                String.valueOf(mAddress.getAddress().getCountryId())
        );

        toggleLoading(true);
        mIsSaveClicked = true;
        mResponseCall.enqueue(mResponseCallback);
    }

    @OnClick(R.id.phoneCodeLayout)
    void showSelectPhoneCodeDialog() {
        CodesDialogFragment dialogFragment = CodesDialogFragment.newInstance(mAddress.getCountryCodes());
        dialogFragment.setOnCodeSelectedListener(this);
        dialogFragment.show(getSupportFragmentManager(), this.getClass().getSimpleName());
    }

    @Override
    public void onCodeSelected(CountryCode countryCode) {
        runOnUiThread(() -> {mPhoneCode.setText(countryCode.getCode());});
    }

    @OnClick(R.id.countryLayout)
    void showSelectCountryDialog() {
        CountriesDialogFragment dialogFragment = CountriesDialogFragment.newInstance(mAddress.getCountries());
        dialogFragment.setOnCountrySelectListener(this);
        dialogFragment.show(getSupportFragmentManager(), this.getClass().getSimpleName());
    }

    @Override
    public void onCountrySelected(Country country) {
        mCountryLabel.setText(country.getName());
        mAddress.getAddress().setCountryId(country.getId());
    }

    @OnFocusChange({R.id.addAddressFirstNameInput, R.id.addAddressLastNameInput, R.id.addAddressStreetInput,
            R.id.addAddressPhoneNumberInput, R.id.addAddressCityInput, R.id.addAddressPostalCodeInput,
            R.id.addAddressEmailInput})
    void didSelectAnotherField(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.addAddressFirstNameInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mFirstNameLayout.getY()).setDuration(300).start();
                break;
            case R.id.addAddressLastNameInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mLastNameLayout.getY()).setDuration(300).start();
                break;
            case R.id.addAddressEmailInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mEmailLayout.getY()).setDuration(300).start();
                break;
            case R.id.addAddressStreetInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mStreetLayout.getY()).setDuration(300).start();
                break;
            case R.id.addAddressPhoneNumberInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mPhoneLayout.getY()).setDuration(300).start();
                break;
            case R.id.addAddressCityInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mCityLayout.getY()).setDuration(300).start();
                break;
            case R.id.addAddressPostalCodeInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mPostalCodeLayout.getY()).setDuration(300).start();
                break;
        }
    }
}
