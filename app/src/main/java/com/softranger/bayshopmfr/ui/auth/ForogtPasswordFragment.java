package com.softranger.bayshopmfr.ui.auth;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForogtPasswordFragment extends ParentFragment {

    @BindView(R.id.restorePassEmailInput)
    EditText mEmailInput;
    @BindView(R.id.restorePasswordProgressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.forgotRestoreButton)
    Button mRestoreBtn;
    private LoginActivity mActivity;
    private Unbinder mUnbinder;
    private Call<ServerResponse> mCall;

    public ForogtPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forogt_password, container, false);
        mActivity = (LoginActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);

        IntentFilter intentFilter = new IntentFilter(Application.ACTION_RETRY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);
        return view;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.forgotRestoreButton)
    void restoreUserPassword() {
        String email = String.valueOf(mEmailInput.getText());
        if (!Application.isValidEmail(email)) {
            mEmailInput.setError(getString(R.string.enter_valid_email));
            return;
        }

        mRestoreBtn.setVisibility(View.INVISIBLE);
        mRestoreBtn.setClickable(false);
        mProgressBar.setVisibility(View.VISIBLE);

        mCall = Application.apiInterface().requestPasswordRestoring(email);
        mCall.enqueue(mResponseCallback);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                    restoreUserPassword();
                    break;
            }
        }
    };

    private ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.showResultActivity(getString(R.string.email_has_been_sent), R.mipmap.ic_mail_has_be_sent_250dp,
                    getString(R.string.reset_your_password));
            mProgressBar.setVisibility(View.GONE);
            mRestoreBtn.setVisibility(View.VISIBLE);
            mRestoreBtn.setClickable(true);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.GONE);
            mRestoreBtn.setVisibility(View.VISIBLE);
            mRestoreBtn.setClickable(true);
        }

        @Override
        public void onError(Call call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.GONE);
            mRestoreBtn.setVisibility(View.VISIBLE);
            mRestoreBtn.setClickable(true);
        }
    };

    @Override
    public String getFragmentTitle() {
        return getString(R.string.forgot_password);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.forgot_password;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
