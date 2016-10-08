package com.softranger.bayshopmf.ui.settings;


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

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.address.Address;
import com.softranger.bayshopmf.ui.addresses.AddressesListFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
    public static final String SHIPPING_NAME = "shippingName";
    public static final String SHIPPING_ID = "shippingMethodId";
    public static final String INSURANCE = "insurance";

    private SettingsActivity mActivity;
    private Unbinder mUnbinder;

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
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        setValuesFromPreferences();
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
    }

    @OnClick(R.id.settingsAutopackagingAddressBtn)
    void changeAutopackagingAddress() {
        mActivity.addFragment(AddressesListFragment.newInstance(true), false);
    }

    @OnClick(R.id.settingsAutopackagingShippingBtn)
    void changeShippingMethod() {

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
        Application.notifyPrefs.edit().putBoolean(SMS, mSmsCheckBox.isChecked()).apply();
    }

    @OnClick(R.id.settingsNotificationsNotifyBtn)
    void togglePushNotifications() {
        mNotifyCheckBox.setChecked(!mNotifyCheckBox.isChecked());
        Application.notifyPrefs.edit().putBoolean(PUSH, mNotifyCheckBox.isChecked()).apply();
    }

    @OnClick(R.id.settingsNotificationsEmailsBtn)
    void toggleEmailNotifications() {
        mEmailsCheckbox.setChecked(!mEmailsCheckbox.isChecked());
        Application.notifyPrefs.edit().putBoolean(EMAILS, mEmailsCheckbox.isChecked()).apply();
    }

    // log out button click
    @OnClick(R.id.settingsLogOutBtn)
    void logOut() {

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION_CHANGE_ADDRESS:
                    Address address = intent.getExtras().getParcelable("address");
                    if (address != null) {
                        Application.autoPackPrefs.edit().putString(ADDRESS_NAME, address.getClientName()).apply();
                        Application.autoPackPrefs.edit().putString(ADDRESS_ID, String.valueOf(address.getId())).apply();
                        mAddressSubtitle.setText(address.getFirstName() + " " + address.getLastName());
                    }
                    break;
            }
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
        mUnbinder.unbind();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
