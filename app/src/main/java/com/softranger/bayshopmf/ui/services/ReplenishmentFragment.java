package com.softranger.bayshopmf.ui.services;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.general.ResultActivity;
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
        mActivity.setToolbarTitle(getString(R.string.account_replenishment), true);

        Button replenishBtn = (Button) view.findViewById(R.id.replenishAccountBtn);
        replenishBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {

    }

    @Override
    public void onClick(View v) {
        // TODO: 8/24/16 replace with translated text
        // build intent for result activity
        Intent showResult = new Intent(mActivity, ResultActivity.class);
        showResult.putExtra(ResultActivity.TOP_TITLE, "Account replenished");
        showResult.putExtra(ResultActivity.SECOND_TITLE, "Your account was successfully replenished.");
        showResult.putExtra(ResultActivity.IMAGE_ID, R.mipmap.ic_confirm_35dp);
        showResult.putExtra(ResultActivity.DESCRIPTION, "Thanks for replenishing your account, the money will arrive in you wallet in about twenty minutes.");

        // show result activity
        startActivity(showResult);
    }
}
