package com.softranger.bayshopmf.ui.auth;


import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends ParentFragment {

    private LoginActivity mActivity;

    @BindView(R.id.registerNameInput)
    TextInputEditText mFirstNameInput;
    @BindView(R.id.registerEmailInput)
    TextInputEditText mEmailInput;
    @BindView(R.id.registerPasswordInput)
    TextInputEditText mPasswordInput;
    @BindView(R.id.registerConfirmPassword)
    TextInputEditText mConfirmPassInput;
    @BindView(R.id.registerNameInputLayout)
    TextInputLayout mNameLayout;
    @BindView(R.id.registerEmailInputLayout)
    TextInputLayout mEmailLayout;
    @BindView(R.id.registerPasswordInputLayout)
    TextInputLayout mPassLayout;
    @BindView(R.id.registerConfirmPasswordLayout)
    TextInputLayout mConfirmPassLayout;
    @BindView(R.id.registerFocusIndicator)
    View mFocusIndicator;
    @BindView(R.id.registerSubscribeCheckBox)
    CheckBox mSubscribe;

    @BindView(R.id.registerProgressBar) ProgressBar mProgressBar;
    @BindView(R.id.registerConfirmButton) Button mConfirmButton;

    private String mEmail;
    private String mPassword;
    private Call<ServerResponse> mRegisterCall;

    private static RequestStep requestStep;

    private Unbinder mUnbinder;

    public RegisterFragment() {
        // Required empty public constructor
    }

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
        String email = String.valueOf(mEmailInput.getText());
        String password = String.valueOf(mPasswordInput.getText());
        String confirmPass = String.valueOf(mConfirmPassInput.getText());

        if (firstName.equals("")) {
            mFirstNameInput.setError(mActivity.getString(R.string.enter_your_first_name));
            return;
        }

        if (!Application.isValidEmail(email)) {
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

        mConfirmButton.setVisibility(View.INVISIBLE);
        mConfirmButton.setClickable(false);
        mProgressBar.setVisibility(View.VISIBLE);


        mEmail = email;
        mPassword = password;

        String[] names = firstName.split(" ");
        String fName = names[0];
        String lName = names.length > 1 ? names[1] : "";

        mRegisterCall = Application.apiInterface().createNewAccount(email, fName, lName, password);
        mRegisterCall.enqueue(mRegisterCallback);
    }

    private ResponseCallback mRegisterCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.showResultActivity(getString(R.string.registration_completed), R.mipmap.ic_registration_completed_250dp,
                    getString(R.string.thank_you_register));
            mConfirmButton.setVisibility(View.VISIBLE);
            mConfirmButton.setClickable(true);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            mConfirmButton.setVisibility(View.VISIBLE);
            mConfirmButton.setClickable(true);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onError(Call call, Throwable t) {
            mConfirmButton.setVisibility(View.VISIBLE);
            mConfirmButton.setClickable(true);
            mProgressBar.setVisibility(View.GONE);
        }
    };

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


    @OnFocusChange({R.id.registerNameInput, R.id.registerEmailInput, R.id.registerPasswordInput, R.id.registerConfirmPassword})
    void inputFocusHasBeenChanged(View view, boolean hasFocus) {
        if (!hasFocus) return;
        if (mFocusIndicator.getVisibility() != View.VISIBLE)
            mFocusIndicator.setVisibility(View.VISIBLE);
        switch (view.getId()) {
            case R.id.registerNameInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mNameLayout.getY()).setDuration(300).start();
                break;
            case R.id.registerEmailInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mEmailLayout.getY()).setDuration(300).start();
                break;
            case R.id.registerPasswordInput:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mPassLayout.getY()).setDuration(300).start();
                break;
            case R.id.registerConfirmPassword:
                ObjectAnimator.ofFloat(mFocusIndicator, "y", mConfirmPassLayout.getY()).setDuration(300).start();
                break;
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
        if (mRegisterCall != null) mRegisterCall.cancel();
        mUnbinder.unbind();
    }
}
