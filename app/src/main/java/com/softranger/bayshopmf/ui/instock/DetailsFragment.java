package com.softranger.bayshopmf.ui.instock;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Item;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private static final String ITEM_ARG = "ITEM ARGUMENT";

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(Item item) {
        Bundle args = new Bundle();
        args.putParcelable(ITEM_ARG, item);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        return view;
    }

}
