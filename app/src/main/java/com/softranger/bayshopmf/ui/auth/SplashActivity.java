package com.softranger.bayshopmf.ui.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.app.ParcelsCount;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.user.User;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;
import retrofit2.Call;

public class SplashActivity extends ParentActivity {

    private Intent mIntent;
    private LoadingStep mLoadingStep;

    private Call<ServerResponse<User>> mPersonalDataCall;
    private Call<ServerResponse<ParcelsCount>> mCountersCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        IntentFilter intentFilter = new IntentFilter(Application.ACTION_RETRY);
        registerReceiver(mBroadcastReceiver, intentFilter);
        if (Application.getInstance().isLoggedIn()) {
            mIntent = new Intent(this, MainActivity.class);
            getPersonalData();
        } else {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        mIntent = new Intent(this, LoginActivity.class);
        startActivity(mIntent);
        finish();
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
            Intercom.client().registerIdentifiedUser(Registration.create()
                    .withUserId(Application.getInstance().getUserId()));
            getCounters();
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(getBaseContext(), errorData.getMessage(), Toast.LENGTH_SHORT).show();
            startLoginActivity();
        }

        @Override
        public void onError(Call<ServerResponse<User>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(getBaseContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            startLoginActivity();
        }
    };

    private void getPersonalData() {
        mPersonalDataCall = Application.apiInterface().getUserPersonalData();
        mLoadingStep = LoadingStep.personal_data;
        mPersonalDataCall.enqueue(mPersonalDataCallback);
    }

    private void getCounters() {
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
            startLoginActivity();
        }

        @Override
        public void onError(Call<ServerResponse<ParcelsCount>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(getBaseContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            startLoginActivity();
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
}
