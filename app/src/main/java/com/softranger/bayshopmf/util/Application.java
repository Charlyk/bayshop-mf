package com.softranger.bayshopmf.util;

import android.content.SharedPreferences;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.User;
import com.softranger.bayshopmf.network.BayShopApiInterface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by eduard on 29.04.16.
 */
public class Application extends android.app.Application {

    public static final String TOKEN_READY = "TOKEN_READY_BROADCAST";
    private static final String PREFERENCE_NAME = "BayShop MF preferences";
    private static final String AUTH_TOKEN = "Auth token for Bay";
    private static Application instance;
    private static SharedPreferences preferences;
    public static String currentToken;
    public static User user;
    private static SimpleDateFormat serverFormat;
    private static SimpleDateFormat friendlyFormat;
    public static HashMap<String, Integer> counters;

    private static Retrofit retrofit;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ObjectMapper objectMapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        objectMapper.setDateFormat(dateFormat);
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.Api.URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        friendlyFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        currentToken = preferences.getString(AUTH_TOKEN, "no token");
        counters = new HashMap<>();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public void setAuthToken(String authToken) {
        preferences.edit().putString(AUTH_TOKEN, authToken).apply();
    }

    public void setLoginStatus(boolean isLoggedIn) {
        preferences.edit().putBoolean("is logged in", isLoggedIn).apply();
    }

    public void setUserId(String userId) {
        preferences.edit().putString("userId", userId).apply();
    }

    public String getUserId() {
        return preferences.getString("userId", "");
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean("is logged in", false);
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static int getCount(String parcelStatus) {
        return counters.get(parcelStatus);
    }

    public static BayShopApiInterface apiInterface() {
        return retrofit.create(BayShopApiInterface.class);
    }

    public static String getFormattedDate(Date date) {
        Date today = new Date();
        String formattedDate = "";
        formattedDate = friendlyFormat.format(date);
        long diff = today.getTime() - date.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if (days > 0) {
            formattedDate = formattedDate + " (" + days + " " + Application.getInstance().getString(R.string.days_ago) + ")";
        } else {
            formattedDate = formattedDate + " (" + Application.getInstance().getString(R.string.today) + ")";
        }

        return formattedDate;
    }
}
