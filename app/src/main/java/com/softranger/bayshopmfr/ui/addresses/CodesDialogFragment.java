package com.softranger.bayshopmfr.ui.addresses;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.CodesListAdapter;
import com.softranger.bayshopmfr.model.address.CountryCode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Eduard Albu on 9/30/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class CodesDialogFragment extends DialogFragment implements CodesListAdapter.OnCodeClickListener {

    private static final String COUTRY_CODES = "COUNTRY CODES LIST";
    private Unbinder mUnbinder;
    private OnCodeSelectedListener mOnCodeSelectedListener;
    private ArrayList<CountryCode> mCountryCodes;

    @BindView(R.id.codesDialogRecyclerView) RecyclerView mRecyclerView;

    public static CodesDialogFragment newInstance(@NonNull ArrayList<CountryCode> countryCodes) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(COUTRY_CODES, countryCodes);
        CodesDialogFragment fragment = new CodesDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.codes_dialog_layout, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mCountryCodes = getArguments().getParcelableArrayList(COUTRY_CODES);
        CodesListAdapter adapter = new CodesListAdapter(getActivity(), mCountryCodes);
        adapter.setOnCodeClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    public void setOnCodeSelectedListener(OnCodeSelectedListener onCodeSelectedListener) {
        mOnCodeSelectedListener = onCodeSelectedListener;
    }

    @Override
    public void onCodeClicked(CountryCode countryCode, int position) {
        if (mOnCodeSelectedListener != null) {
            mOnCodeSelectedListener.onCodeSelected(countryCode);
        }
        this.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public interface OnCodeSelectedListener {
        void onCodeSelected(CountryCode countryCode);
    }
}
