package com.softranger.bayshopmf.ui.settings;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.SettingsAdapter;
import com.softranger.bayshopmf.model.Setting;
import com.softranger.bayshopmf.ui.instock.buildparcel.SelectAddressFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements SettingsAdapter.OnSettingClickListener {

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
            Setting setting = new Setting(s, R.mipmap.ic_in_processing);
            setting.setSettingType(Setting.SettingType.getSettingType(i));
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
                mActivity.addFragment(SelectAddressFragment.newInstance(), true);
                break;
            case CHANGE_PASSWORD:
                mActivity.addFragment(ChangePassFragment.newInstance(), true);
                break;
            case REGIONAL_SETTINGS:

                break;
            case AUTO_PACKAGING:

                break;
            case NOTIFICATIONS:

                break;
            case SUBSCRIBE:

                break;
            case LOG_OUT:

                break;

        }
    }
}
