package com.softranger.bayshopmf.ui.auth;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends ParentFragment {

    private LoginActivity mActivity;

    @BindView(R.id.registerNameInput) EditText mFirstNameInput;
    @BindView(R.id.registerLastNameInput) EditText mLastNameInput;
    @BindView(R.id.registerEmailInput) EditText mEmailInput;
    @BindView(R.id.registerPasswordInput) EditText mPasswordInput;
    @BindView(R.id.registerConfirmPassword) EditText mConfirmPassInput;

    @BindView(R.id.registerProgressBar) ProgressBar mProgressBar;
    @BindView(R.id.registerConfirmButton) Button mConfirmButton;

    private String mEmail;
    private String mPassword;

    private static RequestStep requestStep;

    private Unbinder mUnbinder;

    public RegisterFragment() {
        // Required empty public constructor
    }

    // TODO: 9/21/16 change this fragment to use retrofit

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mActivity = (LoginActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.registerConfirmButton)
    void registerUserToApplication() {
        String firstName = String.valueOf(mFirstNameInput.getText());
        String lastName = String.valueOf(mLastNameInput.getText());
        String email = String.valueOf(mEmailInput.getText());
        String password = String.valueOf(mPasswordInput.getText());
        String confirmPass = String.valueOf(mConfirmPassInput.getText());

        if (firstName.equals("")) {
            mFirstNameInput.setError(mActivity.getString(R.string.enter_your_first_name));
            return;
        }

        if (lastName.equals("")) {
            mLastNameInput.setError(mActivity.getString(R.string.enter_last_name));
            return;
        }

        if (email.equals("") || !email.contains("@")) {
            mEmailInput.setError(mActivity.getString(R.string.enter_valid_email));
            return;
        }

        if (password.equals("") || password.length() < 6) {
            mPasswordInput.setError(mActivity.getString(R.string.enter_valid_password));
            return;
        }

        if (!confirmPass.equals(password)) {
            mConfirmPassInput.setError(mActivity.getString(R.string.passwords_does_not_match));
            return;
        }

        mConfirmButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        mEmail = email;
        mPassword = password;

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("first_name", firstName)
                .add("last_name", lastName)
                .add("password", password)
                .build();
        requestStep = RequestStep.REGISTER;
        ApiClient.getInstance().postRequest(body, Constants.Api.urlRegister(), mHandler);
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        switch (requestStep) {
            case REGISTER:
                requestStep = RequestStep.LOGIN;
                ApiClient.getInstance().logIn(mEmail, mPassword, mHandler);
                break;
            case LOGIN: {
                JSONObject data = response.getJSONObject("data");
                Application.currentToken = data.optString("access_token");
                Application.getInstance().setLoginStatus(true);
                Application.getInstance().setAuthToken(Application.currentToken);
                ApiClient.getInstance().getRequest(Constants.Api.urlParcelsCounter(), mHandler);
                break;
            }
            case COUNTERS: {
                JSONObject data = response.getJSONObject("data");
                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Application.counters.put(key, data.getInt(key));
                }
                mActivity.startActivity(new Intent(mActivity, MainActivity.class));
                mActivity.finish();
                break;
            }
        }
    }

    @Override
    public void finallyMethod() {
        mConfirmButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.register);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.register_user;
    }

    enum RequestStep {
        REGISTER, LOGIN, COUNTERS
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
