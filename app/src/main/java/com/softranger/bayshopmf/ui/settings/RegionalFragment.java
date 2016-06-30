package com.softranger.bayshopmf.ui.settings;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.SpinnerAdapter;
import com.softranger.bayshopmf.model.Country;
import com.softranger.bayshopmf.model.Language;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.ParentFragment;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.SpinnerObj;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegionalFragment extends ParentFragment implements View.OnClickListener {

    private SettingsActivity mActivity;

    private Spinner mCountrySpinner;
    private Spinner mLanguageSpinner;
    private TextView mCountryLabel;
    private TextView mLanguageLabel;
    private ArrayList<Country> mCountries;
    private ArrayList<Language> mLanguages;
    private Button mSaveBtn;
    private ProgressBar mProgressBar;
    private LinearLayout mRegionalHolderLayout;

    private static boolean isSaveClicked;

    private static int currentLanguageId;
    private static int currentCountryId;

    public RegionalFragment() {
        // Required empty public constructor
    }

    public static RegionalFragment newInstance() {
        Bundle args = new Bundle();
        RegionalFragment fragment = new RegionalFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_regional, container, false);
        mActivity = (SettingsActivity) getActivity();

        mSaveBtn = (Button) view.findViewById(R.id.regionalSaveButton);
        mProgressBar = (ProgressBar) view.findViewById(R.id.regionalProgressBar);
        mRegionalHolderLayout = (LinearLayout) view.findViewById(R.id.regionalHolderLayout);
        LinearLayout countryBtn = (LinearLayout) view.findViewById(R.id.regionalCountryButton);
        LinearLayout languageBtn = (LinearLayout) view.findViewById(R.id.regionalLanguageButton);
        countryBtn.setOnClickListener(this);
        languageBtn.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);

        mCountries = new ArrayList<>();
        mLanguages = new ArrayList<>();

        mCountrySpinner = (Spinner) view.findViewById(R.id.regionalCountrySpinner);
        mLanguageSpinner = (Spinner) view.findViewById(R.id.regionalLanguageSpinner);

        mCountryLabel = (TextView) view.findViewById(R.id.regionalCountryLabel);
        mLanguageLabel = (TextView) view.findViewById(R.id.regionalLanguageLabel);

        mRegionalHolderLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        ApiClient.getInstance().getRequest(Constants.Api.urlPersonalData(), mHandler);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regionalCountryButton:
                mCountrySpinner.performClick();
                break;
            case R.id.regionalLanguageButton:
                mLanguageSpinner.performClick();
                break;
            case R.id.regionalSaveButton:
                isSaveClicked = true;
                mProgressBar.setVisibility(View.VISIBLE);
                mSaveBtn.setEnabled(false);
                mSaveBtn.setClickable(false);
                // TODO: 6/29/16 send data to server
                break;
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        if (isSaveClicked) {

            return;
        }

        JSONObject data = response.getJSONObject("data");
        // get current country id
        currentCountryId = data.getInt("countryId");
        // build countries list
        JSONArray jsonCountries = data.getJSONArray("countries");
        for (int i = 0; i < jsonCountries.length(); i++) {
            JSONObject object = jsonCountries.getJSONObject(i);
            Country country = new Country.Builder()
                    .id(object.getInt("id"))
                    .name(object.getString("title"))
                    .build();
            // set country label text as selected country text
            if (country.getId() == currentCountryId) mCountryLabel.setText(country.getName());
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
            if (language.getId() == currentLanguageId) mLanguageLabel.setText(language.getName());
            mLanguages.add(language);
        }

        // set up spinners
        SpinnerAdapter<Language> languagesAdapter = new SpinnerAdapter<>(mActivity, R.layout.country_spinner_item, mLanguages);
        languagesAdapter.setOnCountryClickListener(new SpinnerAdapter.OnCountryClickListener() {
            @Override
            public <T extends SpinnerObj> void onCountryClick(T country, int position) {
                mLanguageLabel.setText(country.getName());
                currentLanguageId = country.getId();
            }
        });

        SpinnerAdapter<Country> countriesAdapter = new SpinnerAdapter<>(mActivity, R.layout.country_spinner_item, mCountries);
        countriesAdapter.setOnCountryClickListener(new SpinnerAdapter.OnCountryClickListener() {
            @Override
            public <T extends SpinnerObj> void onCountryClick(T country, int position) {
                mCountryLabel.setText(country.getName());
                currentCountryId = country.getId();
            }
        });

        mCountrySpinner.setAdapter(countriesAdapter);
        mLanguageSpinner.setAdapter(languagesAdapter);
    }

    @Override
    public void onHandleMessageEnd() {
        isSaveClicked = false;
        mRegionalHolderLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mSaveBtn.setEnabled(true);
        mSaveBtn.setClickable(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.changeToolbarTitle(mActivity.getString(R.string.settings));
    }
}
