package com.softranger.bayshopmf.ui.settings;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.CodesSpinnerAdapter;
import com.softranger.bayshopmf.adapter.SpinnerAdapter;
import com.softranger.bayshopmf.model.Country;
import com.softranger.bayshopmf.model.CountryCode;
import com.softranger.bayshopmf.model.Language;
import com.softranger.bayshopmf.model.User;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.network.ImageDownloadThread;
import com.softranger.bayshopmf.ui.ParentFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.SpinnerObj;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDataFragment extends ParentFragment implements View.OnClickListener {

    private SettingsActivity mActivity;

    private TextInputEditText mFirstNameInput;
    private TextInputEditText mLastNameInput;
    private TextInputEditText mPhoneInput;

    private LinearLayout mPhoneCodeBtn;
    private LinearLayout mCountryBtn;
    private LinearLayout mLanguageBtn;

    private TextView mPhoneCodeLabel;
    private TextView mCountryLabel;
    private TextView mLanguageLabel;

    private Spinner mPhoneCodeSpinner;
    private Spinner mCountrySpinner;
    private Spinner mLanguageSpinner;

    private ArrayList<Country> mCountries;
    private ArrayList<Language> mLanguages;
    private ArrayList<CountryCode> mCountryCodes;

    private Button mSaveButton;

    private CodesSpinnerAdapter mSpinnerAdapter;

    private static boolean isSaveClicked;

    private static int currentLanguageId;
    private static int currentCountryId;

    public UserDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_data, container, false);
        mActivity = (SettingsActivity) getActivity();
        // create lists
        mCountries = new ArrayList<>();
        mLanguages = new ArrayList<>();
        mCountryCodes = new ArrayList<>();
        // bind spinners
        mPhoneCodeSpinner = (Spinner) view.findViewById(R.id.userDataPhoneCodeSpinner);
        mCountrySpinner = (Spinner) view.findViewById(R.id.userDataCountrySpinner);
        mLanguageSpinner = (Spinner) view.findViewById(R.id.userDataLanguageSpinner);
        // bind labels
        mPhoneCodeLabel = (TextView) view.findViewById(R.id.userDaraPhoneCodeTextlabel);
        mCountryLabel = (TextView) view.findViewById(R.id.userDataCountrylabel);
        mLanguageLabel = (TextView) view.findViewById(R.id.userDataLanguagelabel);
        // bind inputs
        mFirstNameInput = (TextInputEditText) view.findViewById(R.id.userDataFirstNameInput);
        mLastNameInput = (TextInputEditText) view.findViewById(R.id.userDataLastNameInput);
        mPhoneInput = (TextInputEditText) view.findViewById(R.id.userDataPhoneNumberInput);
        mPhoneInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        // bind buttons
        mPhoneCodeBtn = (LinearLayout) view.findViewById(R.id.userDataPhoneCodeLayout);
        mCountryBtn = (LinearLayout) view.findViewById(R.id.userDataCountryLayout);
        mLanguageBtn = (LinearLayout) view.findViewById(R.id.userDataLanguageLayout);
        mPhoneCodeBtn.setOnClickListener(this);
        mCountryBtn.setOnClickListener(this);
        mLanguageBtn.setOnClickListener(this);
        // bind save button
        mSaveButton = (Button) view.findViewById(R.id.userDataSaveButton);
        mSaveButton.setOnClickListener(this);

        ApiClient.getInstance().sendRequest(Constants.Api.urlPersonalData(), mHandler);
        setDataOnPosition();
        return view;
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
        mActivity.changeToolbarTitle(mActivity.getString(R.string.settings));
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
        Application.user = new User.Builder()
                .firstName(data.getString("name"))
                .lastName(data.getString("surname"))
                .countryId(data.getInt("countryId"))
                .phoneCode(data.getString("phoneCode"))
                .phoneNumber(data.getString("phone"))
                .languageId(data.getInt("languageId"))
                .languageName(languageName)
                .countryName(countryName)
                .countries(mCountries)
                .languages(mLanguages)
                .countryCodes(mCountryCodes)
                .build();

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
                onClick(mSaveButton);
            }
        });

        mSpinnerAdapter = new CodesSpinnerAdapter(mActivity, R.layout.spinner_list_item, mCountryCodes);
        mSpinnerAdapter.setOnCountryClickListener(new CodesSpinnerAdapter.OnCountryClickListener() {
            @Override
            public void onCountryClick(CountryCode countryCode, int position) {
                mPhoneCodeLabel.setText(countryCode.getCode());
                Application.user.setPhoneCode(countryCode.getCode());
            }
        });

        mCountrySpinner.setAdapter(countriesAdapter);
        mLanguageSpinner.setAdapter(languagesAdapter);
        mPhoneCodeSpinner.setAdapter(mSpinnerAdapter);

        new ImageDownloadThread<>(mCountryCodes, mDownloadHandler, mActivity).start();
        setDataOnPosition();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userDataPhoneCodeLayout:
                mPhoneCodeSpinner.performClick();
                break;
            case R.id.userDataCountryLayout:
                mCountrySpinner.performClick();
                break;
            case R.id.userDataLanguageLayout:
                mLanguageSpinner.performClick();
                break;
            case R.id.userDataSaveButton:
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
                ApiClient.getInstance().sendRequest(body, Constants.Api.urlPersonalData(), mHandler);
                break;
        }
    }

    @Override
    public void onHandleMessageEnd() {
        isSaveClicked = false;
    }

    private Handler mDownloadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ImageDownloadThread.FINISHED) {
                mSpinnerAdapter.notifyDataSetChanged();
            }
        }
    };
}
