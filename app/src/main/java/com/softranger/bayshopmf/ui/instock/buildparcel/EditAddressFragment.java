package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.CodesSpinnerAdapter;
import com.softranger.bayshopmf.adapter.CountrySpinnerAdapter;
import com.softranger.bayshopmf.model.Address;
import com.softranger.bayshopmf.model.Country;
import com.softranger.bayshopmf.model.CountryCode;
import com.softranger.bayshopmf.model.Photo;
import com.softranger.bayshopmf.model.ShippingMethod;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditAddressFragment extends Fragment implements View.OnClickListener,
        CodesSpinnerAdapter.OnCountryClickListener, CountrySpinnerAdapter.OnCountryClickListener {

    public static final String ADD_NEW = "add new address";
    public static final String EDIT = "edit an address";
    private static final String ACTION = "action";

    private static final String ADDRESS_ARG = "address argument";
    public static final String ACTION_REFRESH_ADDRESS = "REFRESH ADDRESSES LIST";
    private MainActivity mActivity;
    private Address mAddress;
    private View mRootView;
    private ArrayList<CountryCode> mCountryCodes;
    private ArrayList<Country> mCountries;
    private CodesSpinnerAdapter mSpinnerAdapter;
    private CountrySpinnerAdapter mCountrySpinnerAdapter;

    private static String action;

    private TextInputEditText mFirstNameInput, mLastNameInput, mStreetNameInput,
            mPhoneInput, mCityInput, mPostalCodeInput, mEmailInput;
    private TextView mPhoneCode, mCountryLabel;
    private Spinner mCodeSpinner, mCountriesSpinner;
    private static int addressId;

    public EditAddressFragment() {
        // Required empty public constructor
    }

    public static EditAddressFragment newInstance(@Nullable Address address) {
        Bundle args = new Bundle();
        if (address != null) {
            args.putParcelable(ADDRESS_ARG, address);
            args.putString(ACTION, EDIT);
        } else {
            args.putString(ACTION, ADD_NEW);
        }
        EditAddressFragment fragment = new EditAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.address_menu, menu);
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_edit_address, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_UPDATE_TITLE);
        mActivity.registerReceiver(mTitleReceiver, intentFilter);
        mCountryCodes = new ArrayList<>();
        mCountries = new ArrayList<>();

        bindAddressInputs(mRootView);

        action = getArguments().getString(ACTION);

        if (getArguments().containsKey(ADDRESS_ARG)) {
            mAddress = getArguments().getParcelable(ADDRESS_ARG);
            addressId = mAddress.getId();
            mActivity.setToolbarTitle(getString(R.string.edit_address), true);
            setDataOnPosition(mAddress);
            ApiClient.getInstance().sendRequest(Constants.Api.urlGetAddress(String.valueOf(addressId)), mHandler);
        } else {
            mAddress = new Address.Builder().build();
            mActivity.setToolbarTitle(getString(R.string.add_new_address), true);
            ApiClient.getInstance().sendRequest(Constants.Api.urlGetPhoneCodes(), mHandler);
        }

        Button saveButton = (Button) mRootView.findViewById(R.id.addAddressSaveAddressButton);
        saveButton.setOnClickListener(this);
        LinearLayout codeLayout = (LinearLayout) mRootView.findViewById(R.id.phoneCodeLayout);
        codeLayout.setOnClickListener(this);
        LinearLayout countryLayout = (LinearLayout) mRootView.findViewById(R.id.countryLayout);
        countryLayout.setOnClickListener(this);
        return mRootView;
    }

    private void setDataOnPosition(Address address) {
        mFirstNameInput.setText(address.getFirstName());
        mLastNameInput.setText(address.getLastName());
        mEmailInput.setText(address.getEmail());
        mStreetNameInput.setText(address.getStreet());
        mPhoneInput.setText(address.getPhoneNumber());
        mCityInput.setText(address.getCity());
        mPhoneCode.setText(address.getPhoneCode());
        mCountryLabel.setText(address.getCountry());
        mPostalCodeInput.setText(address.getPostalCode());
    }

    private void bindAddressInputs(View view) {
        mFirstNameInput = (TextInputEditText) view.findViewById(R.id.addAddressFirstNameInput);
        mLastNameInput = (TextInputEditText) view.findViewById(R.id.addAddressLastNameInput);
        mEmailInput = (TextInputEditText) view.findViewById(R.id.addAddressEmailInput);
        mStreetNameInput = (TextInputEditText) view.findViewById(R.id.addAddressStreetInput);
        mPhoneInput = (TextInputEditText) view.findViewById(R.id.addAddressPhoneNumberInput);
        mPhoneInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mCityInput = (TextInputEditText) view.findViewById(R.id.addAddressCityInput);
        mPostalCodeInput = (TextInputEditText) view.findViewById(R.id.addAddressPostalCodeInput);
        mPhoneCode = (TextView) view.findViewById(R.id.phoneCodeTextlabel);
        mCodeSpinner = (Spinner) view.findViewById(R.id.phoneCodeSpinner);
        mCountryLabel = (TextView) view.findViewById(R.id.addAddressCountrylabel);
        mCountriesSpinner = (Spinner) view.findViewById(R.id.countrySpinner);
    }

    private BroadcastReceiver mTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_UPDATE_TITLE:
                    mActivity.setToolbarTitle(getString(R.string.addresses_list), true);
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mTitleReceiver);
    }

    @Override
    public void onClick(View v) {
        mActivity.hideKeyboard();
        switch (v.getId()) {
            case R.id.countryLayout:
                mCountriesSpinner.performClick();
                break;
            case R.id.phoneCodeLayout:
                mCodeSpinner.performClick();
                break;
            case R.id.addAddressSaveAddressButton:
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
                    Toast.makeText(mActivity, getString(R.string.enter_country_code), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mActivity, getString(R.string.enter_country_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                String postalCode = String.valueOf(mPostalCodeInput.getText());
                if (postalCode.equals("")) {
                    mPostalCodeInput.setError(getString(R.string.enter_postal_code));
                    return;
                };

                RequestBody requestBody = new FormBody.Builder()
                        .add("shipping_first_name", firstName)
                        .add("shipping_last_name", lastName)
                        .add("shipping_email", email)
                        .add("shipping_address", streetName)
                        .add("shipping_city", city)
                        .add("shipping_zip", postalCode)
                        .add("shipping_phone_code", countryCode)
                        .add("shipping_phone", phoneNumber)
                        .add("shipping_state", country)
                        .add("countryId", String.valueOf(mAddress.getCountryId()))
                        .build();

                String url;
                if (action.equals(ADD_NEW)) {
                    url = Constants.Api.urlAddNewAddress(null);
                } else {
                    url = Constants.Api.urlAddNewAddress(String.valueOf(addressId));
                }
                ApiClient.getInstance().sendRequest(requestBody, url, mSaveAddressHandler);
                mActivity.toggleLoadingProgress(true);
                break;
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            buildCountryCodes(data.getJSONArray("maskFormatsAll"));
                            buildCountries(data.getJSONObject("countries"));
                            if (action.equals(EDIT)) {
                                JSONObject a = data.getJSONObject("address");
                                mAddress = buildAddress(a);
                                setDataOnPosition(mAddress);
                            }
                        } else {
                            Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRootView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
            }
            mActivity.toggleLoadingProgress(false);
        }
    };

    private void buildCountryCodes(JSONArray countryCodes) throws Exception {
        for (int i = 0; i < countryCodes.length(); i++) {
            JSONObject jsonCode = countryCodes.getJSONObject(i);
            CountryCode countryCode = new CountryCode.Builder()
                    .id(jsonCode.getInt("id"))
                    .countryId(jsonCode.getInt("countryId"))
                    .code(jsonCode.getString("code"))
                    .format(jsonCode.getString("format"))
                    .flagUrl(jsonCode.getString("flag"))
                    .countryCode(jsonCode.getString("countryCode"))
                    .name(jsonCode.getString("title"))
                    .build();
            mCountryCodes.add(countryCode);
        }
        new DownloadThread(mCountryCodes).start();
        mSpinnerAdapter = new CodesSpinnerAdapter(mActivity, R.layout.spinner_list_item, mCountryCodes);
        mSpinnerAdapter.setOnCountryClickListener(this);
        mCodeSpinner.setAdapter(mSpinnerAdapter);
    }

    private void buildCountries(JSONObject countries) throws Exception {
        Iterator<String> keys = countries.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Country country = new Country.Builder()
                    .id(key)
                    .name(countries.getString(key))
                    .build();
            mCountries.add(country);
        }
        mCountrySpinnerAdapter = new CountrySpinnerAdapter(mActivity, R.layout.country_spinner_item, mCountries);
        mCountrySpinnerAdapter.setOnCountryClickListener(this);
        mCountriesSpinner.setAdapter(mCountrySpinnerAdapter);
    }

    private Address buildAddress(JSONObject a) {
        try {
            String name = a.getString("shipping_first_name") + " " + a.getString("shipping_last_name");
            return new Address.Builder()
                    .id(a.getInt("id"))
                    .clientName(name)
                    .firstName(a.getString("shipping_first_name"))
                    .lastName(a.getString("shipping_last_name"))
                    .email(a.optString("shipping_email", ""))
                    .street(a.getString("shipping_address"))
                    .city(a.getString("shipping_city"))
                    .country(a.optString("shipping_state", ""))
                    .postalCode(a.getString("shipping_zip"))
                    .phoneNumber(a.optString("shipping_phone", ""))
                    .phoneCode(a.optString("shipping_phone_code", ""))
                    .countryId(a.optInt("countryId", 0))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Handler mSaveAddressHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject a = data.getJSONObject("address");
                            mAddress = buildAddress(a);
                            mActivity.sendBroadcast(new Intent(ACTION_REFRESH_ADDRESS));
                            mActivity.onBackPressed();
                        } else {
                            Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(mRootView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    Response response = (Response) msg.obj;
                    String message = response.message();
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = mActivity.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                    break;
                }
            }
            mActivity.toggleLoadingProgress(false);
        }
    };

    @Override
    public void onCountryClick(CountryCode countryCode, int position) {
        String code = countryCode.getCode();
        mAddress.setPhoneCode(code);
        mPhoneCode.setText(code);
    }

    @Override
    public void onCountryClick(Country country, int position) {
        mAddress.setCountry(country.getName());
        mAddress.setCountryId(Integer.parseInt(country.getId()));
        mCountryLabel.setText(country.getName());
    }

    class DownloadThread extends Thread {

        private ArrayList<CountryCode> mCountryCodes;

        public DownloadThread(ArrayList<CountryCode> photos) {
            mCountryCodes = photos;
        }

        @Override
        public void run() {
            for (CountryCode countryCode : mCountryCodes) {
                try {
                    String url = countryCode.getFlagUrl();
                    URL biImageUlr = new URL(url);
                    File image = new File(mActivity.getCacheDir(), urlToFileName(url));
                    if (!image.exists()) {
                        image.createNewFile();
                        BufferedInputStream inputStream = new BufferedInputStream(biImageUlr.openStream());
                        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(image));
                        byte[] buffer = new byte[1024];
                        int byteCount = 0;
                        while ((byteCount = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, byteCount);
                        }
                        inputStream.close();
                        outputStream.flush();
                        outputStream.close();
                    }
                    countryCode.setFlagImage(BitmapFactory.decodeFile(image.getAbsolutePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String urlToFileName(String url) {
            return url.replace("/", "").replace(".", "") + ".png";
        }
    }
}
