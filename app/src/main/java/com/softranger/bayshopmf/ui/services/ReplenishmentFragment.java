package com.softranger.bayshopmf.ui.services;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.ParentActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReplenishmentFragment extends Fragment {

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
        mActivity.setToolbarTitle(getString(R.string.account_replenishment), false);

        return view;
    }

}
