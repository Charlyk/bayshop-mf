package com.softranger.bayshopmf.ui.auth;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.softranger.bayshopmf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotResultFragment extends Fragment implements View.OnClickListener{

    private LoginActivity mActivity;

    public ForgotResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_result, container, false);
        mActivity = (LoginActivity) getActivity();
        Button button = (Button) view.findViewById(R.id.registerResultDoneButton);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
    }
}
