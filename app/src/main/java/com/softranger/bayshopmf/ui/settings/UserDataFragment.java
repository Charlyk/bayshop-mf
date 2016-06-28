package com.softranger.bayshopmf.ui.settings;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDataFragment extends Fragment {


    public UserDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_address, container, false);

        return view;
    }
}
