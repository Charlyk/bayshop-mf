package com.softranger.bayshopmf.ui.settings;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.ParentFragment;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AutopackingFragment extends ParentFragment implements View.OnClickListener {

    private SettingsActivity mActivity;

    private TextView mAddressesLabel;
    private TextView mShippingLabel;
    private TextView mInsuranceLabel;
    private SwitchCompat mSwitchCompat;
    private LinearLayout mLinearLayout;

    public AutopackingFragment() {
        // Required empty public constructor
    }

    public static AutopackingFragment newInstance() {
        Bundle args = new Bundle();
        AutopackingFragment fragment = new AutopackingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_autopacking, container, false);
        mActivity = (SettingsActivity) getActivity();

        mLinearLayout = (LinearLayout) view.findViewById(R.id.autopackingItemsLayout);

        mAddressesLabel = (TextView) view.findViewById(R.id.autopackingAddressLabel);
        mShippingLabel = (TextView) view.findViewById(R.id.autopackingShippingMethodLabel);
        mInsuranceLabel = (TextView) view.findViewById(R.id.autopackingInsuranceLabel);

        mSwitchCompat = (SwitchCompat) view.findViewById(R.id.autopackingUseSwitch);

        RelativeLayout onOff = (RelativeLayout) view.findViewById(R.id.autopackingOnOffButton);
        RelativeLayout address = (RelativeLayout) view.findViewById(R.id.autopackingAddressesButton);
        RelativeLayout shipping = (RelativeLayout) view.findViewById(R.id.autopackingShippingMethodBtn);
        RelativeLayout insurance = (RelativeLayout) view.findViewById(R.id.autopackingInsuraceBtn);

        Button save = (Button) view.findViewById(R.id.autopackingSaveBtn);

        save.setOnClickListener(this);
        onOff.setOnClickListener(this);
        address.setOnClickListener(this);
        shipping.setOnClickListener(this);
        insurance.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.autopackingOnOffButton:
                mSwitchCompat.setChecked(!mSwitchCompat.isChecked());
                if (mSwitchCompat.isChecked()) {
                    mLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    mLinearLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.autopackingAddressesButton:

                break;
            case R.id.autopackingShippingMethodBtn:

                break;
            case R.id.autopackingInsuraceBtn:

                break;
            case R.id.autopackingSaveBtn:
                mActivity.onBackPressed();
                break;
        }
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.changeToolbarTitle(mActivity.getString(R.string.settings));
    }
}
