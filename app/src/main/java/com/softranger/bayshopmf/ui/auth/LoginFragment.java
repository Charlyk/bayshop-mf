package com.softranger.bayshopmf.ui.auth;


import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends ParentFragment {

    @BindView(R.id.loginEmailInput) EditText mEmailInput;
    @BindView(R.id.loginPasswordInput) EditText mPasswordInput;
    @BindView(R.id.loginButton) Button mLoginButton;
    @BindView(R.id.loginProgressBar) ProgressBar mLoginProgressBar;

    private LoginActivity mActivity;
    private Unbinder mUnbinder;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (LoginActivity) getActivity();
        initializeViews(view);
        return view;
    }

    /**
     * Bind all needed views from this activity
     */
    private void initializeViews(View view) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.login);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.login_fragment;
    }
}
