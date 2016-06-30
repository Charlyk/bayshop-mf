package com.softranger.bayshopmf.ui.settings;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.ParentFragment;
import com.softranger.bayshopmf.util.Constants;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends ParentFragment implements View.OnClickListener {

    private SettingsActivity mActivity;

    private SwitchCompat mInternalMail;
    private SwitchCompat mSms;
    private SwitchCompat mPushNotifications;
    private SwitchCompat mEmails;

    private ProgressBar mProgressBar;
    private Button mSaveButton;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        Bundle args = new Bundle();
        NotificationsFragment fragment = new NotificationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        mActivity = (SettingsActivity) getActivity();

        mProgressBar = (ProgressBar) view.findViewById(R.id.notificationsProgressBar);

        mInternalMail = (SwitchCompat) view.findViewById(R.id.notificationsInternalMailSwitch);
        mSms = (SwitchCompat) view.findViewById(R.id.notificationsSmsSwitch);
        mPushNotifications = (SwitchCompat) view.findViewById(R.id.notificationsPushNotifSwitch);
        mEmails = (SwitchCompat) view.findViewById(R.id.notificationsEmailsSwitch);

        RelativeLayout internalMail = (RelativeLayout) view.findViewById(R.id.notificationsInternalMailButton);
        RelativeLayout sms = (RelativeLayout) view.findViewById(R.id.notificationsSmsButton);
        RelativeLayout pushNotify = (RelativeLayout) view.findViewById(R.id.notificationsPushNotifButton);
        RelativeLayout emails = (RelativeLayout) view.findViewById(R.id.notificationsEmailsButton);

        mSaveButton = (Button) view.findViewById(R.id.notificationsSaveButton);
        mSaveButton.setOnClickListener(this);

        internalMail.setOnClickListener(this);
        sms.setOnClickListener(this);
        pushNotify.setOnClickListener(this);
        emails.setOnClickListener(this);

        ApiClient.getInstance().getRequest(Constants.Api.urlMailOptions(), mHandler);
        return view;
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject data = response.getJSONObject("data");
        boolean isInternalMail = data.getInt("alertOnSystem") == 1;
        boolean isSms = data.getInt("obtainSms") == 1;
        boolean isPushNotif = data.getInt("obtainGcm") == 1;
        boolean isEmails = data.getInt("obtainMails") == 1;

        mInternalMail.setChecked(isInternalMail);
        mSms.setChecked(isSms);
        mPushNotifications.setChecked(isPushNotif);
        mEmails.setChecked(isEmails);
    }

    @Override
    public void onHandleMessageEnd() {
        mProgressBar.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notificationsInternalMailButton:
                mInternalMail.setChecked(!mInternalMail.isChecked());
                break;
            case R.id.notificationsSmsButton:
                mSms.setChecked(!mSms.isChecked());
                break;
            case R.id.notificationsPushNotifButton:
                mPushNotifications.setChecked(!mPushNotifications.isChecked());
                break;
            case R.id.notificationsEmailsButton:
                mEmails.setChecked(!mEmails.isChecked());
                break;
            case R.id.notificationsSaveButton:
                RequestBody body = new FormBody.Builder()
                        .add("alertOnSystem", String.valueOf(mInternalMail.isChecked() ? 1 : 0))
                        .add("obtainSms", String.valueOf(mSms.isChecked() ? 1 : 0))
                        .add("obtainGcm", String.valueOf(mPushNotifications.isChecked() ? 1 : 0))
                        .add("obtainMails", String.valueOf(mEmails.isChecked() ? 1 : 0))
                        .build();
                mSaveButton.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                ApiClient.getInstance().postRequest(body, Constants.Api.urlMailOptions(), mHandler);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.changeToolbarTitle(mActivity.getString(R.string.settings));
    }
}
