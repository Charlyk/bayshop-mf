package com.softranger.bayshopmf.ui.auth;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.softranger.bayshopmf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public EditText mEmailInput;
    public EditText mPasswordInput;
    private Button mLoginButton;
    private ProgressBar mLoginProgressBar;

    private LoginActivity mActivity;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mActivity = (LoginActivity) getActivity();
        initializeViews(view);
        return view;
    }

    /**
     * Bind all needed views from this activity
     */
    private void initializeViews(View view) {
        mEmailInput = (EditText) view.findViewById(R.id.loginEmailInput);
        mPasswordInput = (EditText) view.findViewById(R.id.loginPasswordInput);
        mLoginProgressBar = (ProgressBar) view.findViewById(R.id.loginProgressBar);
        mLoginButton = (Button) view.findViewById(R.id.loginButton);
        Button signUpButton = (Button) view.findViewById(R.id.signUpButton);
        String actionColor = "#e64d50";
        String questionText = mActivity.getColoredSpanned(getString(R.string.haveAnAccountText), actionColor);
        String blackColor = "#000000";
        String signUpText = mActivity.getColoredSpanned(getString(R.string.signup), blackColor);
        String underlinedText = "<u>" + signUpText + "</u>";
        signUpButton.setText(Html.fromHtml(questionText + " " + underlinedText));
    }

    /**
     * Hides login button and shows a progressbar
     */
    public void showLoading() {
        mLoginButton.setVisibility(View.GONE);
        mLoginProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hides progress bar and shwos login button
     */
    public void hideLoading() {
        mLoginProgressBar.setVisibility(View.GONE);
        mLoginButton.setVisibility(View.VISIBLE);
    }
}
