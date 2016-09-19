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

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends ParentFragment implements View.OnClickListener {

    private LoginActivity mActivity;

    private EditText mFirstNameInput;
    private EditText mLastNameInput;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private EditText mConfirmPassInput;

    private ProgressBar mProgressBar;
    private Button mConfirmButton;

    private String mEmail;
    private String mPassword;

    private static RequestStep requestStep;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mActivity = (LoginActivity) getActivity();
        mConfirmButton = (Button) view.findViewById(R.id.registerConfirmButton);
        mConfirmButton.setOnClickListener(this);

        mFirstNameInput = (EditText) view.findViewById(R.id.registerNameInput);
        mLastNameInput = (EditText) view.findViewById(R.id.registerLastNameInput);
        mEmailInput = (EditText) view.findViewById(R.id.registerEmailInput);
        mPasswordInput = (EditText) view.findViewById(R.id.registerPasswordInput);
        mConfirmPassInput = (EditText) view.findViewById(R.id.registerConfirmPassword);

        mProgressBar = (ProgressBar) view.findViewById(R.id.registerProgressBar);
        return view;
    }

    @Override
    public void onClick(View v) {
        String firstName = String.valueOf(mFirstNameInput.getText());
        if (firstName.equals("")) {
            mFirstNameInput.setError(mActivity.getString(R.string.enter_your_first_name));
            return;
        }

        String lastName = String.valueOf(mLastNameInput.getText());
        if (lastName.equals("")) {
            mLastNameInput.setError(mActivity.getString(R.string.enter_last_name));
            return;
        }

        String email = String.valueOf(mEmailInput.getText());
        if (email.equals("") || !email.contains("@")) {
            mEmailInput.setError(mActivity.getString(R.string.enter_valid_email));
            return;
        }

        String password = String.valueOf(mPasswordInput.getText());
        if (password.equals("") || password.length() < 6) {
            mPasswordInput.setError(mActivity.getString(R.string.enter_valid_password));
            return;
        }

        String confirmPass = String.valueOf(mConfirmPassInput.getText());
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
}
