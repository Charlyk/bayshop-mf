package com.softranger.bayshopmf.ui.instock.buildparcel;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.general.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class NeedInsuranceFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;

    public NeedInsuranceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_need_insurance, container, false);
        mActivity = (MainActivity) getActivity();
        Button select = (Button) view.findViewById(R.id.needInsuranceSelectButton);
        select.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        mActivity.addFragment(new ConfirmationFragment(), true);
    }
}
