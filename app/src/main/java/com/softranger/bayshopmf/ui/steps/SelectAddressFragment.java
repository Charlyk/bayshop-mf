package com.softranger.bayshopmf.ui.steps;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.instock.InStockFragment;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.util.widget.SlidingRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SelectAddressFragment extends ParentFragment {

    private ParentActivity mActivity;
    private Unbinder mUnbinder;

    @BindView(R.id.selectAddressRecyclerView) RecyclerView mRecyclerView;

    public SelectAddressFragment() {
        // Required empty public constructor
    }

    public static SelectAddressFragment newInstance() {
        Bundle args = new Bundle();
        SelectAddressFragment fragment = new SelectAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SlidingRelativeLayout view = (SlidingRelativeLayout) inflater
                .inflate(R.layout.fragment_select_address, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (ParentActivity) getActivity();

        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(mRecyclerView, "y",
                InStockFragment.totalsY + MainActivity.toolbarHeight, Application.getPixelsFromDp(60));
        recyclerAnimation.setDuration(500);
        recyclerAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        recyclerAnimation.start();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
