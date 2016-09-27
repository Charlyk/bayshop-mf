package com.softranger.bayshopmf.ui.steps;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SelectAddressFragment extends ParentFragment {

    private ParentActivity mActivity;
    private Unbinder mUnbinder;

    public SelectAddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_address, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (ParentActivity) getActivity();
        return view;
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.select_address);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.select_address;
    }
}
