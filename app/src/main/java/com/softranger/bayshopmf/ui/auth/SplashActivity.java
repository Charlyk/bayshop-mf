package com.softranger.bayshopmf.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.app.ParcelsCount;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.user.User;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;

import retrofit2.Call;

public class SplashActivity extends AppCompatActivity {

    private Intent mIntent;

    private Call<ServerResponse<User>> mPersonalDataCall;
    private Call<ServerResponse<ParcelsCount>> mCountersCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Application.getInstance().isLoggedIn()) {
            mIntent = new Intent(this, MainActivity.class);

            mPersonalDataCall = Application.apiInterface().getUserPersonalData();
            mPersonalDataCall.enqueue(mPersonalDataCallback);
        } else {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        mIntent = new Intent(this, LoginActivity.class);
        startActivity(mIntent);
        finish();
    }

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
            startLoginActivity();
        }

        @Override
        public void onError(Call<ServerResponse<User>> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(getBaseContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            startLoginActivity();
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
    }
}
