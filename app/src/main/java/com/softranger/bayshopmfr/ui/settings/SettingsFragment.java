package com.softranger.bayshopmfr.ui.settings;


import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.NotificationSettings;
import com.softranger.bayshopmfr.model.Shipper;
import com.softranger.bayshopmfr.model.address.Address;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.addresses.AddressesListFragment;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.intercom.android.sdk.Intercom;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends ParentFragment {

    public static final String SMS = "sms";
    public static final String PUSH = "push";
    public static final String EMAILS = "emails";
    public static final String AUTOPACKAGING = "autopackaging";
    public static final String ADDRESS_NAME = "addressName";
    public static final String ADDRESS_ID = "addressId";
    public static final String ADDRESS_COUNTRY = "addressCountryId";
    public static final String SHIPPING_NAME = "shippingName";
    public static final String SHIPPING_ID = "shippingMethodId";
    public static final String INSURANCE = "insurance";

    private static final String _SMS = "obtainSms";
    private static final String _PUSH = "obtainGcm";
    private static final String _EMAIL = "obtainMails";

    private SettingsActivity mActivity;
    private Unbinder mUnbinder;
    private Call<ServerResponse<NotificationSettings>> mCall;

    // autopackaging views
    @BindView(R.id.settingsAutopackagingAddressSubtitle)
    TextView mAddressSubtitle;
    @BindView(R.id.settingsAutopackagingShippingSubtitle)
    TextView mShippingSubtitle;
    @BindView(R.id.settingsAutopackagingInsuranceCheckBox)
    CheckBox mInsuranceCheckbox;
    @BindView(R.id.settingsAutopackagingSwitch)
    SwitchCompat mAutopackagingSwitch;
    @BindView(R.id.settingsAutoPackagingLayout)
    LinearLayout mAutopackagingLayout;

    // notifications views
    @BindView(R.id.settingsNotificationsSmsCheckBox)
    CheckBox mSmsCheckBox;
    @BindView(R.id.settingsNotificationsNotifyCheckBox)
    CheckBox mNotifyCheckBox;
    @BindView(R.id.settingsNotificationsEmailsCheckBox)
    CheckBox mEmailsCheckbox;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (SettingsActivity) getActivity();

        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_CHANGE_ADDRESS);
        intentFilter.addAction(ShippersFragment.SHIPPER_SELECTED);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        setValuesFromPreferences();

        Application.apiInterface().getUserNotificationsSettings().enqueue(mResponseCallback);
        return view;
    }

    private void setValuesFromPreferences() {
        // notifications
        mSmsCheckBox.setChecked(Application.notifyPrefs.getBoolean(SMS, false));
        mNotifyCheckBox.setChecked(Application.notifyPrefs.getBoolean(PUSH, false));
        mEmailsCheckbox.setChecked(Application.notifyPrefs.getBoolean(EMAILS, false));
        // autopackaging
        mInsuranceCheckbox.setChecked(Application.autoPackPrefs.getBoolean(INSURANCE, false));
        mAutopackagingSwitch.setChecked(Application.autoPackPrefs.getBoolean(AUTOPACKAGING, false));
        mAutopackagingLayout.setVisibility(mAutopackagingSwitch.isChecked() ? View.VISIBLE : View.GONE);

        mAddressSubtitle.setText(Application.autoPackPrefs.getString(ADDRESS_NAME, ""));
        mShippingSubtitle.setText(Application.autoPackPrefs.getString(SHIPPING_NAME, ""));
    }

    // User data click listeners
    @OnClick(R.id.settingsUserDataBtn)
    void showUserData() {
        mActivity.addFragment(UserDataFragment.newInstance(), false);
    }

    @OnClick(R.id.settingsChangePassBtn)
    void showChangePassScreen() {
        mActivity.addFragment(ChangePassFragment.newInstance(), false);
    }

    @OnClick(R.id.settingsAddressesBtn)
    void showUserAddresses() {
        mActivity.addFragment(AddressesListFragment.newInstance(false), false);
    }

    // Autopackaging click listeners
    @OnClick(R.id.settingsAutopackagingBtn)
    void toggleAutoPackaging() {
        mAutopackagingSwitch.setChecked(!mAutopackagingSwitch.isChecked());
        mAutopackagingLayout.setVisibility(mAutopackagingSwitch.isChecked() ? View.VISIBLE : View.GONE);
        Application.autoPackPrefs.edit().putBoolean(AUTOPACKAGING, mAutopackagingSwitch.isChecked()).apply();
        Application.setAskedAutoPackaging(true);
    }

    @OnClick(R.id.settingsAutopackagingAddressBtn)
    void changeAutopackagingAddress() {
        mActivity.addFragment(AddressesListFragment.newInstance(true), false);
    }

    @OnClick(R.id.settingsAutopackagingShippingBtn)
    void changeShippingMethod() {
        mActivity.addFragment(ShippersFragment.newInstance(), false);
    }

    @OnClick(R.id.settingsAutopackagingInsuranceBtn)
    void toggleInsuranceSelection() {
        mInsuranceCheckbox.setChecked(!mInsuranceCheckbox.isChecked());
        Application.autoPackPrefs.edit().putBoolean(INSURANCE, mInsuranceCheckbox.isChecked()).apply();
    }

    // notifications click listeners
    @OnClick(R.id.settingsNotificationsSmsBtn)
    void toggleSmsNotifications() {
        mSmsCheckBox.setChecked(!mSmsCheckBox.isChecked());
        saveNotificationsSettings();
    }

    @OnClick(R.id.settingsNotificationsNotifyBtn)
    void togglePushNotifications() {
        mNotifyCheckBox.setChecked(!mNotifyCheckBox.isChecked());
        saveNotificationsSettings();
    }

    @OnClick(R.id.settingsNotificationsEmailsBtn)
    void toggleEmailNotifications() {
        mEmailsCheckbox.setChecked(!mEmailsCheckbox.isChecked());
        saveNotificationsSettings();
    }

    private void saveNotificationSettiongsToPreferences() {
        if (mSmsCheckBox != null)
            Application.notifyPrefs.edit().putBoolean(SMS, mSmsCheckBox.isChecked()).apply();
        if (mNotifyCheckBox != null)
            Application.notifyPrefs.edit().putBoolean(PUSH, mNotifyCheckBox.isChecked()).apply();
        if (mEmailsCheckbox != null)
            Application.notifyPrefs.edit().putBoolean(EMAILS, mEmailsCheckbox.isChecked()).apply();
    }

    // log out button click
    @OnClick(R.id.settingsLogOutBtn)
    void logOut() {
        Application.apiInterface().logOut(Application.getInstance().getPushToken()).enqueue(mLogOutCallback);
        mActivity.toggleLoadingProgress(true);
        Intercom.client().reset();
    }

    private ResponseCallback mLogOutCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object data) {
            mActivity.setResult(Activity.RESULT_OK);
            mActivity.finish();
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
        }

        @Override
        public void onError(Call call, Throwable t) {
            Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
            mActivity.toggleLoadingProgress(false);
        }
    };

    private void saveNotificationsSettings() {
        int sms = mSmsCheckBox.isChecked() ? 1 : 0;
        int push = mNotifyCheckBox.isChecked() ? 1 : 0;
        int email = mEmailsCheckbox.isChecked() ? 1 : 0;
        mCall = Application.apiInterface().changeUserNotificationsSettings(sms, push, email);
        mCall.enqueue(mResponseCallback);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION_CHANGE_ADDRESS:
                    Address address = intent.getExtras().getParcelable("address");
                    if (address != null) {
                        Application.autoPackPrefs.edit().putString(ADDRESS_NAME, address.getClientName())
                                .putString(ADDRESS_ID, String.valueOf(address.getId()))
                                .putInt(ADDRESS_COUNTRY, address.getCountryId()).apply();
                        mAddressSubtitle.setText(address.getFirstName() + " " + address.getLastName());
                    }
                    break;
                case ShippersFragment.SHIPPER_SELECTED:
                    Shipper shipper = intent.getExtras().getParcelable("shipper");
                    if (shipper != null) {
                        Application.autoPackPrefs.edit().putString(SHIPPING_NAME, shipper.getTitle())
                                .putString(SHIPPING_ID, shipper.getId()).apply();
                        mShippingSubtitle.setText(shipper.getTitle());
                    }
                    break;
                case Application.ACTION_RETRY:
                    mActivity.removeNoConnectionView();
                    saveNotificationsSettings();
                    break;
            }
        }
    };

    private ResponseCallback<NotificationSettings> mResponseCallback = new ResponseCallback<NotificationSettings>() {
        @Override
        public void onSuccess(NotificationSettings data) {
            boolean sms = data.getObtainSms() != 0;
            boolean push = data.getObtainGCM() != 0;
            boolean mail = data.getObtainMails() != 0;

            if (mSmsCheckBox != null) mSmsCheckBox.setChecked(sms);
            if (mNotifyCheckBox != null) mNotifyCheckBox.setChecked(push);
            if (mEmailsCheckbox != null) mEmailsCheckbox.setChecked(mail);

            saveNotificationSettiongsToPreferences();
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Call<ServerResponse<NotificationSettings>> call, Throwable t) {
            Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public String getFragmentTitle() {
        return getString(R.string.settings);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.settings;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
