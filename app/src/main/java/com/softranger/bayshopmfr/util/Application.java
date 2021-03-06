package com.softranger.bayshopmfr.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.TypedValue;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmfr.BuildConfig;
import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.user.User;
import com.softranger.bayshopmfr.network.BayShopApiInterface;
import com.softranger.bayshopmfr.ui.settings.SettingsFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.intercom.android.sdk.Intercom;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by eduard on 29.04.16.
 *
 */
public class Application extends MultiDexApplication {

    private static final String PREFERENCE_NAME = "BayShop MF preferences";
    private static final String AUTOPACK_PREFS = "com.softranger.bayshopmf.settings.AutopackagingPreferences";
    private static final String NOTIFICATIONS_PREFS = "com.softranger.bayshopmf.settings.NotificationsPreferences";
    private static final String AUTH_TOKEN = "Auth token for Bay";
    public static final String ACTION_RETRY = "com.softranger.bayshopmf.RETRY_TO_CONNECT";
    private static final String PRICES_PREFS = "com.softranger.bayshopmf.settings.AdditionalServicesPrices";
    ;
    private static Application instance;

    private static SharedPreferences preferences;
    public static SharedPreferences autoPackPrefs;
    public static SharedPreferences notifyPrefs;
    public static SharedPreferences pricesPrefs;

    public static String currentToken;
    public static User user;
    private static SimpleDateFormat friendlyFormat;
    public static HashMap<String, Integer> counters;
    public static HashMap<String, Double> servicesPrices;

    private static Retrofit retrofit;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        autoPackPrefs = getSharedPreferences(AUTOPACK_PREFS, MODE_PRIVATE);
        notifyPrefs = getSharedPreferences(NOTIFICATIONS_PREFS, MODE_PRIVATE);
        pricesPrefs = getSharedPreferences(PRICES_PREFS, MODE_PRIVATE);

        currentToken = preferences.getString(AUTH_TOKEN, "no token");

        servicesPrices();

        // initialize counters hashmap
        counters = new HashMap<>();
        // initialize facebook sdk and app events logger
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // create a new okHttp client and set a request interceptor
        // so we could set custom header for every request we make
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(20, TimeUnit.SECONDS);
        httpClient.connectTimeout(20, TimeUnit.SECONDS);
        httpClient.writeTimeout(20, TimeUnit.SECONDS);
        httpClient.addInterceptor((chain) -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Bearer", currentToken)
                    .header("Accept", "application/vnd.MF+json; version=" + BuildConfig.API_VERSION)
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        });

        // create a object mapper and set the format of the date for it
        ObjectMapper objectMapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        objectMapper.setDateFormat(dateFormat);

        // build the client and set mapper and client to retrofit
        OkHttpClient client = httpClient.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();

        // build a client for tracking parcel
        OkHttpClient.Builder trackClientBuilder = new OkHttpClient.Builder();
        trackClientBuilder.readTimeout(20, TimeUnit.SECONDS);
        trackClientBuilder.connectTimeout(20, TimeUnit.SECONDS);
        trackClientBuilder.writeTimeout(20, TimeUnit.SECONDS);
        trackClientBuilder.addInterceptor((chain) -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("X-Authorization-Token", getString(R.string.gde_posylka_api_key))
                    .header("Content-Type", "application/json")
                    .header("Accept-Language", Application.getDeviceLanguage())
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        });

        friendlyFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Intercom.initialize(this, "android_sdk-7c1e0502c5d35150b1f9ef443859612dadf8aa73", "xht0fqt3");
    }

    public void setAdditionalServicesPrices(HashMap<String, Double> additionalServicesPrices) {
        try {
            File file = new File(getDir("data", MODE_PRIVATE), "map");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(additionalServicesPrices);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        servicesPrices = additionalServicesPrices;
    }

    private void servicesPrices() {
        try {
            File file = new File(getDir("data", MODE_PRIVATE), "map");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            Object object = inputStream.readObject();
            servicesPrices = (HashMap<String, Double>) object;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if autopackaging is enabled
     */
    public static boolean isAutopackaging() {
        return autoPackPrefs.getBoolean(SettingsFragment.AUTOPACKAGING, false);
    }

    public static boolean isAutopackagingAddressSelected() {
        return autoPackPrefs.contains(SettingsFragment.ADDRESS_ID);
    }

    public static int getSelectedAddressId() {
        String stringId = autoPackPrefs.getString(SettingsFragment.ADDRESS_ID, "-1");
        return Integer.parseInt(stringId);
    }

    public static int getSelectedAddressCountry() {
        return autoPackPrefs.getInt(SettingsFragment.ADDRESS_COUNTRY, -1);
    }

    public static boolean isAutopackagingShipperSelected() {
        return autoPackPrefs.contains(SettingsFragment.SHIPPING_ID);
    }

    public static String getSelectedShipperId() {
        return autoPackPrefs.getString(SettingsFragment.SHIPPING_ID, "-1");
    }

    public static boolean hasInsurance() {
        return autoPackPrefs.getBoolean(SettingsFragment.INSURANCE, false);
    }

    public static void setAskedAutoPackaging(boolean askedAutoPackaging) {
        autoPackPrefs.edit().putBoolean("asked", askedAutoPackaging).apply();
    }

    public static boolean askedAboutAutoPackaging() {
        return autoPackPrefs.getBoolean("asked", false);
    }

    public void setPushToken(String pushToken) {
        preferences.edit().putString("push", pushToken).apply();
    }

    public String getPushToken() {
        return preferences.getString("push", "");
    }

    public void setPushTokenSent(boolean sent) {
        preferences.edit().putBoolean("pushSent", sent).apply();
    }

    public boolean isPushSent() {
        return preferences.getBoolean("pushSent", false);
    }

    public void saveIntercomToken(String token) {
        preferences.edit().putString("intercom", token).apply();
    }

    public String getIntercomToken() {
        return preferences.getString("intercom", "");
    }

    public void setIntroShown(boolean shown) {
        preferences.edit().putBoolean("intro", shown).apply();
    }

    public boolean isIntroShown() {
        return preferences.getBoolean("intro", false);
    }

    /**
     * Save authentication token for current user
     *
     * @param authToken token obtained from server
     */
    public void setAuthToken(String authToken) {
        preferences.edit().putString(AUTH_TOKEN, authToken).apply();
    }

    /**
     * Set true if user is logged in otherwise set false
     * @param isLoggedIn user login status
     */
    public void setLoginStatus(boolean isLoggedIn) {
        preferences.edit().putBoolean("is logged in", isLoggedIn).apply();
    }

    /**
     * Save current user uid to set it in navigation header
     * @param userId logged in user id
     */
    public void setUserId(String userId) {
        preferences.edit().putString("userId", userId).apply();
    }

    /**
     * Get saved user uid or an empty string if it was not saved
     * @return either user id or an empty string
     */
    public String getUserId() {
        return preferences.getString("userId", "");
    }

    /**
     * Check if user is loged in
     * @return user login status
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean("is logged in", false);
    }

    /**
     * Check if an email address is valid
     * @param email address you want to check
     * @return validation result
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Get number of parcels for the given status
     * @param parcelStatus for which you need the count
     * @return number of parcels with gien status
     */
    public static int getCount(String parcelStatus) {
        try {
            return counters.get(parcelStatus);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void saveAvatarPath(String path) {
        preferences.edit().putString("imagePath", path).apply();
    }

    public static String getAvatarPath() {
        return preferences.getString("imagePath", null);
    }

    /**
     * Convert dp to pixels
     * @param dp to convert
     * @return value of passed dp in pixels
     */
    public static int getPixelsFromDp(int dp) {
        Resources r = Application.getInstance().getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    /**
     * Create an instance of BayShop api interface
     * @return {@link BayShopApiInterface}
     */
    public static BayShopApiInterface apiInterface() {
        return retrofit.create(BayShopApiInterface.class);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Returns a string with date formatted as "dd.MM.yyyy" and it also contains
     * how many days already elapsed from given date to current or if the date is today it just
     * return "Today"
     * @param date which you want to format
     * @return string with formatted date
     */
    public static String getFormattedDate(Date date) {
        Date today = new Date();
        if (date == null) date = new Date();
        String formattedDate;
        formattedDate = friendlyFormat.format(date);
        long diff = today.getTime() - date.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if (days == 0) {
            formattedDate = Application.getInstance().getString(R.string.today);
        } else if (days == 1) {
            formattedDate = Application.getInstance().getString(R.string.yesterday);
        }

        return formattedDate;
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) Application.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
