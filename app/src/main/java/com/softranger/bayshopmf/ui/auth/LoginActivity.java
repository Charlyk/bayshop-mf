package com.softranger.bayshopmf.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1537;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private Button mLoginButton;
    private ProgressBar mLoginProgressBar;
    public static LoginActivity instance;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance = this;
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_ME), new Scope(Scopes.EMAIL))
                .requestServerAuthCode(getString(R.string.server_client_id), false)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        initializeViews();
    }

    /**
     * Bind all needed views from this activity
     */
    private void initializeViews() {
        mEmailInput = (EditText) findViewById(R.id.loginEmailInput);
        mPasswordInput = (EditText) findViewById(R.id.loginPasswordInput);
        mLoginProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        Button signUpButton = (Button) findViewById(R.id.signUpButton);
        String actionColor = "#e64d50";
        String questionText = getColoredSpanned(getString(R.string.haveAnAccountText), actionColor);
        String blackColor = "#000000";
        String signUpText = getColoredSpanned(getString(R.string.signup), blackColor);
        String underlinedText = "<u>" + signUpText + "</u>";
        signUpButton.setText(Html.fromHtml(questionText + " " + underlinedText));
    }

    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    /**
     * Hides login button and shows a progressbar
     */
    private void showLoading() {
        mLoginButton.setVisibility(View.GONE);
        mLoginProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hides progress bar and shwos login button
     */
    private void hideLoading() {
        mLoginProgressBar.setVisibility(View.GONE);
        mLoginButton.setVisibility(View.VISIBLE);
    }

    /**
     * Called when facebook button is clicked
     * @param view facebook button
     */
    public void loginWithFacebook(View view) {
        Toast.makeText(this, "Soon", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when google button is clicked
     * @param view google button
     */
    public void loginWithGoogle(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        showLoading();
    }

    /**
     * Called when log in button is clicked
     * @param view login button
     */
    public void loginWithBayApi(View view) {
        String email = String.valueOf(mEmailInput.getText());
        String password = String.valueOf(mPasswordInput.getText());
        if (!Application.isValidEmail(email)) {
            mEmailInput.setError(getString(R.string.enter_valid_email));
            return;
        }
        if (password == null || password.equals("")) {
            mPasswordInput.setError(getString(R.string.enter_valid_password));
            return;
        }
        ApiClient.getInstance().logIn(email, password, mAuthHandler);
        showLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
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
            ApiClient.getInstance().sendRequest(body, Constants.Api.getAuthUrl(), mAuthHandler);
        } else {

        }
    }

    /**
     * Called when sign up is clicked
     * @param view sign up button
     */
    public void signUpToBayShop(View view) {

    }

    private Handler mAuthHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        boolean error = response.getBoolean("error");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            Application.currentToken = data.optString("access_token");
                            Application.getInstance().setLoginStatus(true);
                            Application.getInstance().setAuthToken(Application.currentToken);
                            LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            String message = response.optString("message", getString(R.string.unknown_error));
                            Snackbar.make(mLoginButton, message, Snackbar.LENGTH_SHORT).show();
                            hideLoading();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(mLoginButton, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        hideLoading();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR:
                    Response response = (Response) msg.obj;
                    Snackbar.make(mLoginButton, response.message(), Snackbar.LENGTH_SHORT).show();
                    hideLoading();
                    break;
                case Constants.ApiResponse.RESPONSE_FAILED: {
                    IOException exception = (IOException) msg.obj;
                    Snackbar.make(mLoginButton, exception.getMessage(), Snackbar.LENGTH_SHORT).show();
                    hideLoading();
                    break;
                }
            }
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
