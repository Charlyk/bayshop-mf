package com.softranger.bayshopmf.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Country;
import com.softranger.bayshopmf.model.CountryCode;
import com.softranger.bayshopmf.model.Language;
import com.softranger.bayshopmf.model.User;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Application.getInstance().isLoggedIn()) {
            mIntent = new Intent(this, MainActivity.class);
            ApiClient.getInstance().getRequest(Constants.Api.urlPersonalData(), mHandler);
        } else {
            mIntent = new Intent(this, LoginActivity.class);
            startActivity(mIntent);
            finish();
        }
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
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
                            ArrayList<Country> countries = new ArrayList<>();
                            ArrayList<Language> languages = new ArrayList<>();
                            ArrayList<CountryCode> countryCodes = new ArrayList<>();

                            int currentLanguageId = data.getInt("languageId");
                            int currentCountryId = data.getInt("countryId");

                            String languageName = "";
                            String countryName = "";
                            // build countries list
                            JSONArray jsonCountries = data.getJSONArray("countries");
                            for (int i = 0; i < jsonCountries.length(); i++) {
                                JSONObject object = jsonCountries.getJSONObject(i);
                                Country country = new Country.Builder()
                                        .id(object.getInt("id"))
                                        .name(object.getString("title"))
                                        .build();
                                if (currentCountryId == country.getId())
                                    countryName = country.getName();
                                countries.add(country);
                            }
                            // build languages array list
                            JSONObject jsonLanguages = data.getJSONObject("languages");
                            Iterator<String> keys = jsonLanguages.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                Language language = new Language.Builder()
                                        .id(Integer.parseInt(key))
                                        .name(jsonLanguages.getString(key))
                                        .build();
                                if (currentLanguageId == language.getId())
                                    languageName = language.getName();
                                languages.add(language);
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
                                countryCodes.add(countryCode);
                            }
                            // build user
                            Application.user = new User.Builder()
                                    .firstName(data.getString("name"))
                                    .lastName(data.getString("surname"))
                                    .countryId(data.getInt("countryId"))
                                    .phoneCode(data.getString("phoneCode"))
                                    .phoneNumber(data.getString("phone"))
                                    .languageId(data.getInt("languageId"))
                                    .countryName(countryName)
                                    .languageName(languageName)
                                    .countries(countries)
                                    .languages(languages)
                                    .countryCodes(countryCodes)
                                    .build();
                            ApiClient.getInstance().getRequest(Constants.Api.urlParcelsCounter(), mCounterHandler);
                        } else {
                            Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = SplashActivity.this.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    private Handler mCounterHandler = new Handler(Looper.getMainLooper()) {
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
                            Iterator<String> keys = data.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                Application.counters.put(key, data.getInt(key));
                            }
                            startActivity(mIntent);
                        } else {
                            Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = SplashActivity.this.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            finish();
        }
    };
}
