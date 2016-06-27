package com.softranger.bayshopmf.ui.auth;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.softranger.bayshopmf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForogtPasswordFragment extends Fragment implements View.OnClickListener {

    private LoginActivity mActivity;

    public ForogtPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forogt_password, container, false);
        mActivity = (LoginActivity) getActivity();
        Button restore = (Button) view.findViewById(R.id.forgotRestoreButton);
        restore.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        mActivity.addFragment(new ForgotResultFragment(), true);
    }
}
