package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsuranceAgreementFragment extends ParentFragment {


    private static final String DESCRIPTION_ARG = "description argument";

    public InsuranceAgreementFragment() {
        // Required empty public constructor
    }

    public static InsuranceAgreementFragment newInstance(String description) {
        Bundle args = new Bundle();
        args.putString(DESCRIPTION_ARG, description);
        InsuranceAgreementFragment fragment = new InsuranceAgreementFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insurance_agreement, container, false);
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
}
