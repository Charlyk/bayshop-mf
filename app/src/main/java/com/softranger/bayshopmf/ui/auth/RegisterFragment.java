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

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

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
        if (lastName.equals("") || lastName.length() < 5) {
            mLastNameInput.setError(mActivity.getString(R.string.enter_phone_number));
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

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("first_name", firstName)
                .add("last_name", lastName)
                .add("password", password)
                .build();
        ApiClient.getInstance().sendRequest(body, Constants.Api.urlRegister(), mRegisterHandler);
    }

    public Handler mRegisterHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message");
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            ApiClient.getInstance().logIn(mEmail, mPassword, mAuthHandler);
                        } else {
                            mConfirmButton.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        mConfirmButton.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    mConfirmButton.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        message = ((Response) msg.obj).message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (IOException) msg.obj;
                        message = exception.getMessage();
                    }
                    mConfirmButton.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    private Handler mAuthHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.ApiResponse.RESPONSE_OK: {
                    try {
                        JSONObject response = new JSONObject((String) msg.obj);
                        String message = response.optString("message", getString(R.string.unknown_error));
                        boolean error = !message.equalsIgnoreCase("ok");
                        if (!error) {
                            JSONObject data = response.getJSONObject("data");
                            Application.currentToken = data.optString("access_token");
                            Application.getInstance().setLoginStatus(true);
                            Application.getInstance().setAuthToken(Application.currentToken);
                            mActivity.startActivity(new Intent(mActivity, MainActivity.class));
                            mActivity.finish();
                        } else {
                            message = response.optString("message", getString(R.string.unknown_error));
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case Constants.ApiResponse.RESPONSE_ERROR: {
                    String message = getString(R.string.unknown_error);
                    if (msg.obj instanceof Response) {
                        Response response = (Response) msg.obj;
                        message = response.message();
                    } else if (msg.obj instanceof Exception) {
                        Exception exception = (Exception) msg.obj;
                        message = exception.getMessage();
                    }
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            mConfirmButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    };
}
