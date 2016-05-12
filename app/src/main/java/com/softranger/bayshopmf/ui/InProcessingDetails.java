package com.softranger.bayshopmf.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.InProcessingProduct;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProcessingDetails extends Fragment {


    private static final String PRODUCT_ARG = "in processing arguments";

    public InProcessingDetails() {
        // Required empty public constructor
    }

    public static InProcessingDetails newInstance(InProcessingProduct product) {

        Bundle args = new Bundle();
        args.putParcelable(PRODUCT_ARG, product);
        InProcessingDetails fragment = new InProcessingDetails();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        InProcessingProduct processingProduct = getArguments().getParcelable(PRODUCT_ARG);
        return inflater.inflate(R.layout.fragment_in_processing_details, container, false);
    }

}
