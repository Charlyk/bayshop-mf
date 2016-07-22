package com.softranger.bayshopmf.ui.auth;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Country;
import com.softranger.bayshopmf.model.CountryCode;
import com.softranger.bayshopmf.model.Language;
import com.softranger.bayshopmf.model.User;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.FacebookAuth;
import com.softranger.bayshopmf.util.FacebookAuth.OnLoginDataReadyListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        OnLoginDataReadyListener {

    private static final int RC_SIGN_IN = 1537;
    public static LoginActivity instance;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;
    private LoginFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance = this;

        mLoginFragment = new LoginFragment();
        addFragment(mLoginFragment, false);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_ME), new Scope(Scopes.EMAIL))
                .requestServerAuthCode(getString(R.string.server_client_id), false)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mCallbackManager = CallbackManager.Factory.create();
    }

    public String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    /**
     * Called when facebook button is clicked
     *
     * @param view facebook button
     */
    public void loginWithFacebook(View view) {
        FacebookAuth.getInstance().facebookLogin(this, this, mCallbackManager);
        mLoginFragment.showLoading();
    }

    /**
     * Called when google button is clicked
     *
     * @param view google button
     */
    public void loginWithGoogle(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        mLoginFragment.showLoading();
    }

    /**
     * Called when log in button is clicked
     *
     * @param view login button
     */
    public void loginWithBayApi(View view) {
        String email = String.valueOf(mLoginFragment.mEmailInput.getText());
        String password = String.valueOf(mLoginFragment.mPasswordInput.getText());
        if (!Application.isValidEmail(email)) {
            mLoginFragment.mEmailInput.setError(getString(R.string.enter_valid_email));
            return;
        }
        if (password == null || password.equals("")) {
            mLoginFragment.mPasswordInput.setError(getString(R.string.enter_valid_password));
            return;
        }
        ApiClient.getInstance().logIn(email, password, mAuthHandler);
        mLoginFragment.showLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String serverCode = acct.getServerAuthCode();
            String name = acct.getDisplayName();
            RequestBody body = new FormBody.Builder()
                    .add("code", serverCode)
                    .add("type", "google")
                    .build();
            ApiClient.getInstance().postRequest(body, Constants.Api.urlAuth(), mAuthHandler);
        } else {
            Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            mLoginFragment.hideLoading();
        }
    }

    public void addFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.loginActivityContainer, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack)
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    /**
     * Called when sign up is clicked
     *
     * @param view sign up button
     */
    public void signUpToBayShop(View view) {
        addFragment(new RegisterFragment(), true);
    }

    private Handler mAuthHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        if (message.equalsIgnoreCase("Ok")) {
                            JSONObject data = response.getJSONObject("data");
                            Application.currentToken = data.optString("access_token");
                            Application.getInstance().setUserId(data.getString("uid").toUpperCase());
                            Application.getInstance().setLoginStatus(true);
                            Application.getInstance().setAuthToken(Application.currentToken);
                            ApiClient.getInstance().getRequest(Constants.Api.urlPersonalData(), mHandler);
                        } else {
                            message = response.optString("message", getString(R.string.unknown_error));
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            mLoginFragment.hideLoading();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        mLoginFragment.hideLoading();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR:
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    mLoginFragment.hideLoading();
                    break;
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    IOException exception = (IOException) msg.obj;
                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    mLoginFragment.hideLoading();
                    break;
                }
            }
        }
    };

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        if (message.equalsIgnoreCase("ok")) {
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
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = LoginActivity.this.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        if (message.equalsIgnoreCase("ok")) {
                            JSONObject data = response.getJSONObject("data");
                            Iterator<String> keys = data.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                Application.counters.put(key, data.optInt(key, 0));
                            }
                            Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainActivity);
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    mLoginFragment.hideLoading();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = LoginActivity.this.getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    mLoginFragment.hideLoading();
                    break;
                }
            }
            finish();
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void forgotPassword(View view) {
        addFragment(new ForogtPasswordFragment(), true);
    }

    @Override
    public void onLoginDataReady(String facebookData) {
        RequestBody body = new FormBody.Builder()
                .add("code", facebookData)
                .add("type", "facebook")
                .build();
        ApiClient.getInstance().postRequest(body, Constants.Api.urlAuth(), mAuthHandler);
    }

    @Override
    public void onCanceled() {
        mLoginFragment.hideLoading();
    }
}
