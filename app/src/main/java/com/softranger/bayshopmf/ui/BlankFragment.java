package com.softranger.bayshopmf.ui;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.util.widget.ParcelStatusBarView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment implements View.OnClickListener {

    private ParcelStatusBarView mStatusBarView;

    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance() {
        Bundle args = new Bundle();
        BlankFragment fragment = new BlankFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        mStatusBarView = (ParcelStatusBarView) view.findViewById(R.id.parcelStatus);
        Button changeStatus = (Button) view.findViewById(R.id.changeStatusBtn);
        changeStatus.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        mStatusBarView.setProgress(mStatusBarView.getCurrentProgress() + 1, "Parcel status");
    }
}
