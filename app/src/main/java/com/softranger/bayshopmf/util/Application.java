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
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.User;

import java.util.HashMap;

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
    public static HashMap<String, Integer> counters;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
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

    public boolean isLoggedIn() {
        return preferences.getBoolean("is logged in", false);
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static int getCount(String parcelStatus) {
        return counters.get(parcelStatus);
    }
}
