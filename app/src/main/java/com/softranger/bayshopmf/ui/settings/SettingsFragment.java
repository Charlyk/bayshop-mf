package com.softranger.bayshopmf.ui.settings;


import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.SecondStepAdapter;
import com.softranger.bayshopmf.adapter.SettingsAdapter;
import com.softranger.bayshopmf.model.Setting;
import com.softranger.bayshopmf.ui.addresses.AddressesListFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends ParentFragment implements SettingsAdapter.OnSettingClickListener {

    private SettingsActivity mActivity;
    private String[] mSettingItems;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mActivity = (SettingsActivity) getActivity();
        mSettingItems = mActivity.getResources().getStringArray(R.array.settings_list);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragmentSettingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        SettingsAdapter adapter = new SettingsAdapter(buildSettingsList());
        adapter.setOnSettingClickListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private ArrayList<Setting> buildSettingsList() {
        ArrayList<Setting> settings = new ArrayList<>();
        for (int i = 0; i < mSettingItems.length; i++) {
            String s = mSettingItems[i];
            Setting setting = new Setting(s);
            setting.setSettingType(Setting.SettingType.getSettingType(i));
            setting.setIconId(Setting.SettingType.getSettingImage(setting.getSettingType()));
            settings.add(setting);
        }
        return settings;
    }

    @Override
    public void onSettingClick(Setting setting, int position) {
        switch (setting.getSettingType()) {
            case USER_DATA:
                mActivity.addFragment(new UserDataFragment(), true);
                break;
            case ADDRESSES:
                mActivity.addFragment(AddressesListFragment.newInstance(SecondStepAdapter.ButtonType.delete), false);
                break;
            case CHANGE_PASSWORD:
                mActivity.addFragment(ChangePassFragment.newInstance(), true);
                break;
            case REGIONAL_SETTINGS:
                mActivity.addFragment(RegionalFragment.newInstance(), false);
                break;
            case AUTO_PACKAGING:
                mActivity.addFragment(AutopackingFragment.newInstance(), true);
                break;
            case NOTIFICATIONS:
                mActivity.addFragment(NotificationsFragment.newInstance(), true);
                break;
            case SUBSCRIBE:
//                mActivity.changeToolbarTitle(mActivity.getString(R.string.subscribe));
                break;
            case LOG_OUT:
                Intent logOut = new Intent(SettingsActivity.ACTION_LOG_OUT);
                mActivity.sendBroadcast(logOut);
                mActivity.finish();
                break;
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {

    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.settings);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.settings;
    }
}
