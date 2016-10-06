package com.softranger.bayshopmf.ui.settings;


import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.CodesSpinnerAdapter;
import com.softranger.bayshopmf.adapter.SpinnerAdapter;
import com.softranger.bayshopmf.model.address.Country;
import com.softranger.bayshopmf.model.address.CountryCode;
import com.softranger.bayshopmf.model.user.Language;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.network.ImageDownloadThread;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.SpinnerObj;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.RequestBody;

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

    @BindView(R.id.inputFocusIndicator)
    View mFocusIndicator;

    @BindView(R.id.userDataSaveButton)
    Button mSaveButton;
    @BindView(R.id.userDataScrollView)
    ScrollView mScrollView;

    private CodesSpinnerAdapter mSpinnerAdapter;

    private static boolean isSaveClicked;

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

        mScrollView.setVisibility(View.GONE);

        // create lists
        mCountries = new ArrayList<>();
        mLanguages = new ArrayList<>();
        mCountryCodes = new ArrayList<>();

        mFirstNameInput.requestFocus();
        mPhoneInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mActivity.toggleLoadingProgress(true);
        ApiClient.getInstance().getRequest(Constants.Api.urlPersonalData(), mHandler);
        setDataOnPosition();
        return view;
    }

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
        RequestBody body = new FormBody.Builder()
                .add("surname", Application.user.getLastName())
                .add("name", Application.user.getFirstName())
                .add("countryId", String.valueOf(Application.user.getCountryId()))
                .add("phoneCode", String.valueOf(Application.user.getPhoneCode()))
                .add("phone", Application.user.getPhoneNumber())
                .add("languageId", String.valueOf(Application.user.getLanguageId()))
                .build();
        ApiClient.getInstance().postRequest(body, Constants.Api.urlPersonalData(), mHandler);
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
        mActivity.hideKeyboard();
        mUnbinder.unbind();
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject data = response.getJSONObject("data");
        // get current country id
        currentCountryId = data.getInt("countryId");
        String countryName = "";
        String languageName = "";
        // build countries list
        JSONArray jsonCountries = data.getJSONArray("countries");
        for (int i = 0; i < jsonCountries.length(); i++) {
            JSONObject object = jsonCountries.getJSONObject(i);
            Country country = new Country.Builder()
                    .id(object.getInt("id"))
                    .name(object.getString("title"))
                    .build();
            // set country label text as selected country text
            if (country.getId() == currentCountryId) {
                mCountryLabel.setText(country.getName());
                countryName = country.getName();
            }
            mCountries.add(country);
        }
        // get current language id
        currentLanguageId = data.getInt("languageId");
        // build languages array list
        JSONObject jsonLanguages = data.getJSONObject("languages");
        Iterator<String> keys = jsonLanguages.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Language language = new Language.Builder()
                    .id(Integer.parseInt(key))
                    .name(jsonLanguages.getString(key))
                    .build();
            if (language.getId() == currentLanguageId) {
                mLanguageLabel.setText(language.getName());
                languageName = language.getName();
            }
            mLanguages.add(language);
        }

        // build country codes
        JSONArray jsonCodes = data.getJSONArray("phoneFormats");
        for (int i = 0; i < jsonCodes.length(); i++) {
            JSONObject object = jsonCodes.getJSONObject(i);
            CountryCode countryCode = new CountryCode.Builder()
                    .id(object.getInt("id"))
                    .countryId(object.getInt("countryId"))
                    .code(object.getString("code"))
                    .format(object.getString("format"))
                    .flagUrl(object.getString("flag"))
                    .countryCode(object.getString("countryCode"))
                    .name(object.getString("title"))
                    .build();
            if (countryCode.getCountryId() == currentCountryId) {
                Application.user.setPhoneCode(countryCode.getCode());
            }
            mCountryCodes.add(countryCode);
        }

        // build user
        Application.user.setFirstName(data.getString("name"));
        Application.user.setLastName(data.getString("surname"));
        Application.user.setCountryId(data.getInt("countryId"));
        Application.user.setPhoneCode(data.getString("phoneCode"));
        Application.user.setPhoneNumber(data.getString("phone"));
        Application.user.setLanguageId(data.getInt("languageId"));
        Application.user.setLanguageName(languageName);
        Application.user.setCountryName(countryName);
        Application.user.setCountries(mCountries);
//        Application.user.setLanguages(mLanguages);
        Application.user.setCountryCodes(mCountryCodes);

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
                saveUserData();
            }
        });

        mSpinnerAdapter = new CodesSpinnerAdapter(mActivity, R.layout.phone_code_item, mCountryCodes);
        mSpinnerAdapter.setOnCountryClickListener((countryCode, position) -> {
            mPhoneCodeLabel.setText(countryCode.getCode());
            Application.user.setPhoneCode(countryCode.getCode());
        });

        mCountrySpinner.setAdapter(countriesAdapter);
        mLanguageSpinner.setAdapter(languagesAdapter);
        mPhoneCodeSpinner.setAdapter(mSpinnerAdapter);

        new ImageDownloadThread<>(mCountryCodes, mDownloadHandler, mActivity).start();
        setDataOnPosition();
    }

    @Override
    public void onHandleMessageEnd() {
        isSaveClicked = false;
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.user_data);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.user_data;
    }

    @Override
    public void finallyMethod() {
        mActivity.toggleLoadingProgress(false);
        mScrollView.setVisibility(View.VISIBLE);
    }

    private Handler mDownloadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ImageDownloadThread.FINISHED) {
                mSpinnerAdapter.notifyDataSetChanged();
            }
        }
    };

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
