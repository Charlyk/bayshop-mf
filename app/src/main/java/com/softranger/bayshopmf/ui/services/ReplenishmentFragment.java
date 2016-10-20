package com.softranger.bayshopmf.ui.services;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;


public class ReplenishmentFragment extends ParentFragment implements View.OnClickListener {

    private ParentActivity mActivity;


    public ReplenishmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_replenishment, container, false);
        mActivity = (ParentActivity) getActivity();
        mActivity.setToolbarTitle(getString(R.string.account_replenishment));

        Button replenishBtn = (Button) view.findViewById(R.id.replenishAccountBtn);
        replenishBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {

    }

    @Override
    public String getFragmentTitle() {
        return null;
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return null;
    }

    @Override
    public void onClick(View v) {
        // build intent for result activity
        mActivity.showResultActivity(getString(R.string.account_replenished), R.mipmap.ic_confirm_replenish_250dp,
                getString(R.string.thanks_for_replenishing));
    }
}
