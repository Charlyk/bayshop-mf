package com.softranger.bayshopmf.ui.auth;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.softranger.bayshopmf.model.app.ParcelsCount;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.user.User;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.FacebookAuth;
import com.softranger.bayshopmf.util.FacebookAuth.OnLoginDataReadyListener;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import retrofit2.Call;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

public class LoginActivity extends ParentActivity implements GoogleApiClient.OnConnectionFailedListener,
        OnLoginDataReadyListener {

    private static final int RC_SIGN_IN = 1537;
    public static LoginActivity instance;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;
    private LoginFragment mLoginFragment;

    private Call<ServerResponse<User>> mLoginCall;
    private Call<ServerResponse<User>> mSocialLoginCall;
    private Call<ServerResponse<User>> mDataCall;
    private Call<ServerResponse<ParcelsCount>> mCountersCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance = this;

        mLoginFragment = LoginFragment.newInstance();
        replaceFragment(mLoginFragment);

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

        mLoginFragment.showLoading();
        mLoginCall = Application.apiInterface().loginWithCredentials(email, password);
        mLoginCall.enqueue(mLoginCallback);
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
            if (acct != null) {
                String serverCode = acct.getServerAuthCode();
                mSocialLoginCall = Application.apiInterface().loginWithSocialNetworks(serverCode, "google");
                mSocialLoginCall.enqueue(mLoginCallback);
            } else {
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                mLoginFragment.hideLoading();
            }
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

    /**
     * Callback for login request
     */
    private ResponseCallback<User> mLoginCallback = new ResponseCallback<User>() {
        @Override
        public void onSuccess(User user) {
            Application.getInstance().setUserId(user.getUserId());
            Application.getInstance().setLoginStatus(true);
            Application.currentToken = user.getToken();
            Application.getInstance().setAuthToken(user.getToken());
            mDataCall = Application.apiInterface().getUserPersonalData();
            mDataCall.enqueue(mPersonalDataCallback);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(getBaseContext(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mLoginFragment.hideLoading();
        }

        @Override
        public void onError(Call<ServerResponse<User>> call, Throwable t) {
            t.printStackTrace();
            // TODO: 9/21/16 handle errors
            mLoginFragment.hideLoading();
        }
    };

    /**
     * Callback for personal data request
     */
    private ResponseCallback<User> mPersonalDataCallback = new ResponseCallback<User>() {
        @Override
        public void onSuccess(User data) {
            Application.user = data;
            mCountersCall = Application.apiInterface().getParcelsCounters();
            mCountersCall.enqueue(mCountersCallback);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(getBaseContext(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mLoginFragment.hideLoading();
        }

        @Override
        public void onError(Call<ServerResponse<User>> call, Throwable t) {
            t.printStackTrace();
            // TODO: 9/21/16 handle errors
            mLoginFragment.hideLoading();
        }
    };

    /**
     * Callback for parcel count request
     */
    private ResponseCallback<ParcelsCount> mCountersCallback = new ResponseCallback<ParcelsCount>() {
        @Override
        public void onSuccess(ParcelsCount data) {
            try {
                Application.counters = data.getCountersMap();
                Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainActivity);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                mLoginFragment.hideLoading();
                // TODO: 9/21/16 handle the error
            }
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(getBaseContext(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mLoginFragment.hideLoading();
        }

        @Override
        public void onError(Call<ServerResponse<ParcelsCount>> call, Throwable t) {
            t.printStackTrace();
            // TODO: 9/21/16 handle errors
            mLoginFragment.hideLoading();
        }
    };


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void forgotPassword(View view) {
        addFragment(new ForogtPasswordFragment(), true);
    }

    @Override
    public void onLoginDataReady(String facebookData) {
        mSocialLoginCall = Application.apiInterface().loginWithSocialNetworks(facebookData, "facebook");
        mSocialLoginCall.enqueue(mLoginCallback);
    }

    @Override
    public void onCanceled() {
        mLoginFragment.hideLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoginCall != null) mLoginCall.cancel();
        if (mSocialLoginCall != null) mSocialLoginCall.cancel();
        if (mCountersCall != null) mCountersCall.cancel();
        if (mDataCall != null) mDataCall.cancel();
    }

    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.loginActivityContainer, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void toggleLoadingProgress(boolean show) {

    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.loginActivityContainer, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void onBackStackChanged() {

    }
}
