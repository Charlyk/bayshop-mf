package com.softranger.bayshopmf.ui.auth;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForogtPasswordFragment extends ParentFragment implements View.OnClickListener {

    private EditText mEmailInput;
    private ProgressBar mProgressBar;
    private Button mRestoreBtn;
    private LoginActivity mActivity;

    public ForogtPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forogt_password, container, false);
        mActivity = (LoginActivity) getActivity();
        mRestoreBtn = (Button) view.findViewById(R.id.forgotRestoreButton);
        mRestoreBtn.setOnClickListener(this);
        mEmailInput = (EditText) view.findViewById(R.id.restorePasswordEmailInput);
        mProgressBar = (ProgressBar) view.findViewById(R.id.restorePasswordProgressBar);
        return view;
    }

    @Override
    public void onClick(View v) {
        String email = String.valueOf(mEmailInput.getText());
        if (email.equals("") && !email.contains("@")) {
            mEmailInput.setError(getString(R.string.enter_valid_email));
            return;
        }

        mRestoreBtn.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .build();
        ApiClient.getInstance().postRequest(body, Constants.Api.urlRestorePassword(), mHandler);
    }

    @Override
    public void onServerResponse(JSONObject response) {
        mActivity.addFragment(ForgotResultFragment.newInstance(getString(R.string.restore_password),
                R.drawable.ic_mail, getString(R.string.email_has_been_sent), getString(R.string.reset_your_password),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = mActivity.getFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                            fm.popBackStack();
                        }
                    }
                }), true);
    }

    @Override
    public void onHandleMessageEnd() {
        mProgressBar.setVisibility(View.GONE);
        mRestoreBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.forgot_password);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.forgot_password;
    }
}
