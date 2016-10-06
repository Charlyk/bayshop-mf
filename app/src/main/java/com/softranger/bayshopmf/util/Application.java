package com.softranger.bayshopmf.util;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.TypedValue;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.user.User;
import com.softranger.bayshopmf.network.BayShopApiInterface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by eduard on 29.04.16.
 */
public class Application extends android.app.Application {

    private static final String PREFERENCE_NAME = "BayShop MF preferences";
    private static final String AUTOPACK_PREFS = "com.softranger.bayshopmf.settings.AutopackagingPreferences";
    private static final String NOTIFICATIONS_PREFS = "com.softranger.bayshopmf.settings.NotificationsPreferences";
    private static final String AUTH_TOKEN = "Auth token for Bay";
    private static Application instance;

    private static SharedPreferences preferences;
    public static SharedPreferences autoPackPrefs;
    public static SharedPreferences notifyPrefs;

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

        preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        autoPackPrefs = getSharedPreferences(AUTOPACK_PREFS, MODE_PRIVATE);
        notifyPrefs = getSharedPreferences(NOTIFICATIONS_PREFS, MODE_PRIVATE);

        currentToken = preferences.getString(AUTH_TOKEN, "no token");
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
                .baseUrl(Constants.Api.URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();

        serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        friendlyFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
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
        try {
            return counters.get(parcelStatus);
        } catch (Exception e) {
            return 0;
        }
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

    public static BayShopApiInterface apiInterface() {
        return retrofit.create(BayShopApiInterface.class);
    }

    public static String getFormattedDate(Date date) {
        Date today = new Date();
        if (date == null) date = new Date();
        String formattedDate;
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
