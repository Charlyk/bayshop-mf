package com.softranger.bayshopmfr.ui.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;
import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.PagerAdapter;
import com.softranger.bayshopmfr.model.app.ParcelsCount;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.user.User;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.auth.intro.IntroFragment;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;
import retrofit2.Call;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends ParentActivity {

    private Intent mIntent;
    private LoadingStep mLoadingStep;
    private IntroStep mIntroStep;

    @BindView(R.id.splashActivityViewPager)
    ViewPager mViewPager;
    @BindView(R.id.splashActivityDotsView)
    DotIndicator mDotIndicator;
    @BindView(R.id.splashActivityNextBtn)
    Button mButton;
    @BindView(R.id.splashActivityProgressBar)
    ProgressBar mProgressBar;

    long personalDataStart;
    long countersStart;
    private boolean isIntroVisible;

    private Call<ServerResponse<User>> mPersonalDataCall;
    private Call<ServerResponse<ParcelsCount>> mCountersCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        IntentFilter intentFilter = new IntentFilter(Application.ACTION_RETRY);
        registerReceiver(mBroadcastReceiver, intentFilter);

        if (Application.getInstance().isIntroShown()) {
            if (Application.getInstance().isLoggedIn()) {
                mIntent = new Intent(this, MainActivity.class);
                getPersonalData();
            } else {
                startLoginActivity();
            }
        } else {
            mButton.setVisibility(View.VISIBLE);
            mIntroStep = IntroStep.delivery;
            isIntroVisible = true;
            initIntroScreens();
        }

        mShowUpdateDialog = false;
    }

    private void changeStatusBarColor(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    private void initIntroScreens() {
        PagerAdapter adapter = new PagerAdapter(this, getSupportFragmentManager());
        adapter.setShowTitle(false);
        adapter.addFragment(IntroFragment.newInstance(R.mipmap.ic_presentation_250dp, getString(R.string.intro_first_title),
                getString(R.string.intro_first_description),
                R.color.colorAccent), "");

        adapter.addFragment(IntroFragment.newInstance(R.mipmap.ic_sent_250dp, getString(R.string.intro_second_title),
                getString(R.string.intro_second_description),
                R.color.colorGreenAction), "");

        adapter.addFragment(IntroFragment.newInstance(R.mipmap.ic_notification_250dp, getString(R.string.intro_third_title),
                getString(R.string.intro_third_description),
                R.color.colorLocalDepot), "");

        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIntroStep = IntroStep.getIntroStep(position);
                mButton.setText(getString(mIntroStep.stringRes()));
                changeStatusBarColor(mIntroStep.statusBarColor());
                mDotIndicator.setSelectedItem(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void startLoginActivity() {
        Application.autoPackPrefs.edit().clear().apply();
        Application.notifyPrefs.edit().clear().apply();
        mIntent = new Intent(this, LoginActivity.class);
        if (!isUpdateRequired) {
            startActivity(mIntent);
            finish();
        } else {
            runOnUiThread(() -> mProgressBar.setVisibility(View.INVISIBLE));
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Application.ACTION_RETRY:
                    removeNoConnectionView();
                    if (mLoadingStep == LoadingStep.personal_data) {
                        getPersonalData();
                    } else {
                        getCounters();
                    }
                    break;
            }
        }
    };


    /**
     * Callback for personal data request
     */
    private ResponseCallback<User> mPersonalDataCallback = new ResponseCallback<User>() {
        @Override
        public void onSuccess(User data) {
            Application.user = data;
            // register user do intercom
            Intercom.client().registerIdentifiedUser(Registration.create()
                    .withUserId(Application.getInstance().getUserId().toLowerCase()));
            // update his name in intercom database
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("name", data.getFullName());
            userData.put("language_override", Application.getDeviceLanguage());
            try {
                PackageInfo packageInfo = Application.getInstance().getPackageManager().getPackageInfo(Application.getInstance().getPackageName(), 0);
                int codeVersion = packageInfo.versionCode;
                userData.put("code_version", codeVersion);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Intercom.client().updateUser(userData);
            long duration = System.currentTimeMillis() - personalDataStart;
            Log.d("Personal data", duration + "");
            getCounters();
            // get additional services prices
            Application.apiInterface().getAdditionalServicesPrice().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(hashMapServerResponse -> {
                        Application.getInstance().setAdditionalServicesPrices(hashMapServerResponse.getData());
                    }, error -> {
                        error.printStackTrace();
                        // TODO: 11/30/16 send error to server
                        Toast.makeText(Application.getInstance(), getString(R.string.cant_obtain_prices), Toast.LENGTH_SHORT).show();
                    });
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(getBaseContext(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
            if (!isUpdateRequired) {
                startLoginActivity();
            } else {
                runOnUiThread(() -> mProgressBar.setVisibility(View.INVISIBLE));
            }
        }

        @Override
        public void onError(Call<ServerResponse<User>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(getBaseContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            if (!isUpdateRequired) {
                startLoginActivity();
            } else {
                runOnUiThread(() -> mProgressBar.setVisibility(View.INVISIBLE));
            }
        }
    };

    private void getPersonalData() {
        personalDataStart = System.currentTimeMillis();
        mPersonalDataCall = Application.apiInterface().getUserPersonalData();
        mLoadingStep = LoadingStep.personal_data;
        mPersonalDataCall.enqueue(mPersonalDataCallback);
    }

    private void getCounters() {
        countersStart = System.currentTimeMillis();
        mCountersCall = Application.apiInterface().getParcelsCounters();
        mLoadingStep = LoadingStep.counters;
        mCountersCall.enqueue(mCountersCallback);
    }

    /**
     * Callback for parcel count request
     */
    private ResponseCallback<ParcelsCount> mCountersCallback = new ResponseCallback<ParcelsCount>() {
        @Override
        public void onSuccess(ParcelsCount data) {
            try {
                long duration = System.currentTimeMillis() - countersStart;
                Log.d("Counters", duration + "");
                Application.counters = data.getCountersMap();
                startActivity(mIntent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                startLoginActivity();
            }
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(getBaseContext(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
            if (!isUpdateRequired) {
                startLoginActivity();
            } else {
                runOnUiThread(() -> mProgressBar.setVisibility(View.INVISIBLE));
            }
        }

        @Override
        public void onError(Call<ServerResponse<ParcelsCount>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(getBaseContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            if (!isUpdateRequired) {
                startLoginActivity();
            } else {
                runOnUiThread(() -> mProgressBar.setVisibility(View.INVISIBLE));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountersCall != null) mCountersCall.cancel();
        if (mPersonalDataCall != null) mPersonalDataCall.cancel();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onBackStackChanged() {

    }

    @OnClick(R.id.splashActivityNextBtn)
    public void onNextClick(View view) {
        switch (mIntroStep) {
            case sent:
            case delivery:
                mViewPager.setCurrentItem(mIntroStep.stepIndex + 1, true);
                break;
            case notifications:
                new Handler().postDelayed(() -> {
                    mViewPager.setVisibility(View.GONE);
                    mButton.setVisibility(View.GONE);
                    Application.getInstance().setIntroShown(true);
                    changeStatusBarColor(R.color.colorAccent);
                    if (Application.getInstance().isLoggedIn()) {
                        mIntent = new Intent(this, MainActivity.class);
                        getPersonalData();
                    } else {
                        startLoginActivity();
                    }
                }, 202);
                mViewPager.animate().alpha(0f).setDuration(200).start();
                isIntroVisible = false;
                break;
        }
    }

    enum LoadingStep {
        personal_data, counters
    }

    @Override
    public void setToolbarTitle(String title) {

    }

    @Override
    public void addFragment(ParentFragment fragment, boolean showAnimation) {

    }

    @Override
    public void toggleLoadingProgress(boolean show) {

    }

    @Override
    public void replaceFragment(ParentFragment fragment) {

    }

    @Override
    public void onBackPressed() {
        if (isIntroVisible) {
            int currentItem = mViewPager.getCurrentItem();
            if (currentItem > 0) {
                mViewPager.setCurrentItem(currentItem - 1);
            }
        }
    }

    enum IntroStep {
        delivery(0, R.string.next, R.color.colorAccent),
        sent(1, R.string.next, R.color.colorGreenAction),
        notifications(2, R.string.done, R.color.colorLocalDepot);

        private int stepIndex;
        @StringRes
        private int btnText;
        @ColorRes
        private int color;

        IntroStep(int stepIndex, @StringRes int btnText, @ColorRes int color) {
            this.stepIndex = stepIndex;
            this.btnText = btnText;
            this.color = color;
        }

        @StringRes
        public int stringRes() {
            return btnText;
        }

        @ColorRes
        public int statusBarColor() {
            return color;
        }

        public static IntroStep getIntroStep(int stepIndex) {
            for (IntroStep step : values()) {
                if (stepIndex == step.stepIndex) {
                    return step;
                }
            }

            return delivery;
        }
    }

    @Override
    protected void cancelAllRequests() {
        if (mPersonalDataCall != null) mPersonalDataCall.cancel();
        if (mCountersCall != null) mCountersCall.cancel();
    }
}
