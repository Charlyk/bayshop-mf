package com.softranger.bayshopmf.ui.settings;


import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.CodesSpinnerAdapter;
import com.softranger.bayshopmf.adapter.SpinnerAdapter;
import com.softranger.bayshopmf.model.address.Country;
import com.softranger.bayshopmf.model.address.CountryCode;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.user.Language;
import com.softranger.bayshopmf.model.user.User;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.util.SpinnerObj;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDataFragment extends ParentFragment {

    private SettingsActivity mActivity;
    private Unbinder mUnbinder;

    @BindView(R.id.userDataFirstNameInput)
    TextInputEditText mFirstNameInput;
    @BindView(R.id.userDataLastNameInput)
    TextInputEditText mLastNameInput;
    @BindView(R.id.userDataPhoneNumberInput)
    TextInputEditText mPhoneInput;
    @BindView(R.id.userDataFirstNameInputLayout)
    TextInputLayout mFirstNameLayout;
    @BindView(R.id.userDataLastNameInputLayout)
    TextInputLayout mLastNameLayout;
    @BindView(R.id.userDataPhoneNumberInputLayout)
    TextInputLayout mPhoneLayout;

    @BindView(R.id.userDaraPhoneCodeTextlabel)
    TextView mPhoneCodeLabel;
    @BindView(R.id.userDataCountrylabel)
    TextView mCountryLabel;
    @BindView(R.id.userDataLanguagelabel)
    TextView mLanguageLabel;

    @BindView(R.id.userDataPhoneCodeSpinner)
    Spinner mPhoneCodeSpinner;
    @BindView(R.id.userDataCountrySpinner)
    Spinner mCountrySpinner;
    @BindView(R.id.userDataLanguageSpinner)
    Spinner mLanguageSpinner;

    private ArrayList<Country> mCountries;
    private ArrayList<Language> mLanguages;
    private ArrayList<CountryCode> mCountryCodes;
    private Call<ServerResponse> mSaveCall;
    private Call<ServerResponse<User>> mDataCall;
    private boolean goBack;

    @BindView(R.id.inputFocusIndicator)
    View mFocusIndicator;

    @BindView(R.id.userDataSaveButton)
    Button mSaveButton;
    @BindView(R.id.userDataScrollView)
    ScrollView mScrollView;

    private boolean isSaveClicked;

    private static int currentLanguageId;
    private static int currentCountryId;

    public UserDataFragment() {
        // Required empty public constructor
    }

    public static UserDataFragment newInstance() {
        Bundle args = new Bundle();
        UserDataFragment fragment = new UserDataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_data, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (SettingsActivity) getActivity();

        IntentFilter intentFilter = new IntentFilter(Application.ACTION_RETRY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mScrollView.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.GONE);

        // create lists
        mCountries = new ArrayList<>();
        mLanguages = new ArrayList<>();
        mCountryCodes = new ArrayList<>();

        mFirstNameInput.requestFocus();
        mPhoneInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mActivity.toggleLoadingProgress(true);

        mDataCall = Application.apiInterface().getUserPersonalData();
        mDataCall.enqueue(mResponseCallback);
        setDataOnPosition();
        return view;
    }

    @Override
    public void refreshFragment() {
        if (isSaveClicked) {
            saveUserData();
        } else {
            mDataCall = Application.apiInterface().getUserPersonalData();
            mDataCall.enqueue(mResponseCallback);
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                    refreshFragment();
                    break;
            }
        }
    };

    @OnClick(R.id.userDataPhoneCodeLayout)
    void selectPhoneCode() {
        mPhoneCodeSpinner.performClick();
    }

    @OnClick(R.id.userDataCountryLayout)
    void selectCoutny() {
        mCountrySpinner.performClick();
    }

    @OnClick(R.id.userDataLanguageLayout)
    void selectLanguage() {
        mLanguageSpinner.performClick();
    }

    @OnClick(R.id.userDataSaveButton)
    void saveUserData() {
        isSaveClicked = true;
        String surname = String.valueOf(mLastNameInput.getText());
        String name = String.valueOf(mFirstNameInput.getText());
        String phone = String.valueOf(mPhoneInput.getText());
        Application.user.setLastName(surname);
        Application.user.setFirstName(name);
        Application.user.setPhoneNumber(phone);

        mSaveCall = Application.apiInterface().saveUserPersonalData(Application.user.getLastName(),
                Application.user.getFirstName(), String.valueOf(Application.user.getCountryId()),
                String.valueOf(Application.user.getPhoneCode()), Application.user.getPhoneNumber(),
                String.valueOf(Application.user.getLanguageId()));
        mSaveCall.enqueue(mSaveResponseCallback);
        mActivity.toggleLoadingProgress(true);
    }

    private ResponseCallback mSaveResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            Toast.makeText(mActivity, getString(R.string.saved_succesfuly), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            mActivity.onBackPressed();
            isSaveClicked = false;
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            isSaveClicked = false;
        }

        @Override
        public void onError(Call call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            isSaveClicked = false;
        }
    };

    private ResponseCallback<User> mResponseCallback = new ResponseCallback<User>() {
        @Override
        public void onSuccess(User data) {
            currentCountryId = data.getCountryId();
            String countryName = "";
            mCountries = data.getCountries();
            for (Country country : mCountries) {
                if (country.getId() == currentCountryId) {
                    mCountryLabel.setText(country.getName());
                    countryName = country.getName();
                }
            }

            currentLanguageId = data.getLanguageId();
            String languageName = "";
            mLanguages = data.getLanguages();
            for (Language language : mLanguages) {
                if (language.getId() == currentLanguageId) {
                    mLanguageLabel.setText(language.getName());
                    languageName = language.getName();
                }
            }

            Application.user = data;
            Application.user.setCountryName(countryName);
            Application.user.setLanguageName(languageName);
            mCountryCodes = data.getCountryCodes();

            setUpSpinners();

            mActivity.toggleLoadingProgress(false);
            mScrollView.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call<ServerResponse<User>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }
    };

    private void setUpSpinners() {
        // set up spinners
        SpinnerAdapter<Language> languagesAdapter = new SpinnerAdapter<>(mActivity, R.layout.country_spinner_item, mLanguages);
        languagesAdapter.setOnCountryClickListener(new SpinnerAdapter.OnCountryClickListener() {
            @Override
            public <T extends SpinnerObj> void onCountryClick(T country, int position) {
                mLanguageLabel.setText(country.getName());
                currentLanguageId = country.getId();
                Application.user.setLanguageId(country.getId());
            }
        });

        SpinnerAdapter<Country> countriesAdapter = new SpinnerAdapter<>(mActivity, R.layout.country_spinner_item, mCountries);
        countriesAdapter.setOnCountryClickListener(new SpinnerAdapter.OnCountryClickListener() {
            @Override
            public <T extends SpinnerObj> void onCountryClick(T country, int position) {
                mCountryLabel.setText(country.getName());
                currentCountryId = country.getId();
                Application.user.setCountryId(country.getId());
            }
        });

        CodesSpinnerAdapter spinnerAdapter = new CodesSpinnerAdapter(mActivity, R.layout.phone_code_item, mCountryCodes);
        spinnerAdapter.setOnCountryClickListener((countryCode, position) -> {
            mPhoneCodeLabel.setText(countryCode.getCode());
            Application.user.setPhoneCode(countryCode.getCode());
        });

        mCountrySpinner.setAdapter(countriesAdapter);
        mLanguageSpinner.setAdapter(languagesAdapter);
        mPhoneCodeSpinner.setAdapter(spinnerAdapter);

        setDataOnPosition();
    }

    private void setDataOnPosition() {
        mPhoneCodeLabel.setText(Application.user.getPhoneCode());
        mLanguageLabel.setText(Application.user.getLanguageName());
        mCountryLabel.setText(Application.user.getCountryName());
        mFirstNameInput.setText(Application.user.getFirstName());
        mLastNameInput.setText(Application.user.getLastName());
        mPhoneInput.setText(Application.user.getPhoneNumber());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSaveCall != null) mSaveCall.cancel();
        if (mDataCall != null) mDataCall.cancel();
        mActivity.hideKeyboard();
        mUnbinder.unbind();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.user_data);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.user_data;
    }

    @OnFocusChange({R.id.userDataFirstNameInput, R.id.userDataLastNameInput, R.id.userDataPhoneNumberInput})
    void onInputFocusChanged(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.userDataFirstNameInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mFirstNameLayout.getY()).setDuration(300).start();
                break;
            case R.id.userDataLastNameInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mLastNameLayout.getY()).setDuration(300).start();
                break;
            case R.id.userDataPhoneNumberInput:
                if (hasFocus)
                    ObjectAnimator.ofFloat(mFocusIndicator, "y", mPhoneLayout.getY()).setDuration(300).start();
                break;
        }
    }
}
