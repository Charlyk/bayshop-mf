package com.softranger.bayshopmf.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailInput;
    private EditText mPasswordInput;
    private Button mLoginButton;
    private ProgressBar mLoginProgressBar;
    private ImageView mLogoImage;
    public static LoginActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance = this;
        supportPostponeEnterTransition();
        supportStartPostponedEnterTransition();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityCompat.finishAfterTransition(SplashActivity.instance);
            }
        }, 1000);
        initializeViews();
    }

    /**
     * Bind all needed views from this activity
     */
    private void initializeViews() {
        mEmailInput = (EditText) findViewById(R.id.loginEmailInput);
        mPasswordInput = (EditText) findViewById(R.id.loginPasswordInput);
        mLoginProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        mLogoImage = (ImageView) findViewById(R.id.logo_circle);
        mLoginButton = (Button) findViewById(R.id.loginButton);
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

    }

    /**
     * Called when google button is clicked
     * @param view google button
     */
    public void loginWithGoogle(View view) {

    }

    /**
     * Called when log in button is clicked
     * @param view login button
     */
    public void loginWithBayApi(View view) {
        showLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(mLogoImage);
            }
        }, 2000);

    }

    /**
     * Called when sign up is clicked
     * @param view sign up button
     */
    public void signUpToBayShop(View view) {

    }

    /**
     * Starts MainActivity with scene transition which moves the logo circle to toolbar
     * @param imageView which will be moved
     */
    private void startActivity(ImageView imageView) {
        Intent intent = new Intent(this, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, getString(R.string.transition_image_view));
            startActivity(intent, options.toBundle());
        }
        else {
            startActivity(intent);
        }
    }
}
