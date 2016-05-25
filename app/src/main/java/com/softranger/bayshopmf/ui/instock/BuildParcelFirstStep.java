package com.softranger.bayshopmf.ui.instock;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuildParcelFirstStep extends Fragment {

    private MainActivity mActivity;

    public BuildParcelFirstStep() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_build_parcel_first_step, container, false);
        mActivity = (MainActivity) getActivity();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.buildFirstStepItemsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        String listItems = getString(R.string.list_items);
        mActivity.setToolbarTitle(listItems, true);

        return view;
    }

}
