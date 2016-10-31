package com.softranger.bayshopmfr.ui.addresses;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.CountriesDialogAdapter;
import com.softranger.bayshopmfr.model.address.Country;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Eduard Albu on 9/30/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class CountriesDialogFragment extends DialogFragment implements CountriesDialogAdapter.OnCoutryClickListener {

    private static final String COUNTRIES = "COUNTRIES LIST ARG";
    private Unbinder mUnbinder;
    private ArrayList<Country> mCountries;
    private OnCountrySelectListener mOnCountrySelectListener;

    @BindView(R.id.countriesDialogRecyclerView) RecyclerView mRecyclerView;

    public static CountriesDialogFragment newInstance(ArrayList<Country> countries) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(COUNTRIES, countries);
        CountriesDialogFragment fragment = new CountriesDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.countries_dialog_layout, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mCountries = getArguments().getParcelableArrayList(COUNTRIES);
        CountriesDialogAdapter adapter = new CountriesDialogAdapter(mCountries);
        adapter.setOnCoutryClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    public void setOnCountrySelectListener(OnCountrySelectListener onCountrySelectListener) {
        mOnCountrySelectListener = onCountrySelectListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onCountryClicked(Country country, int position) {
        if (mOnCountrySelectListener != null) {
            mOnCountrySelectListener.onCountrySelected(country);
        }
        this.dismiss();
    }

    public interface OnCountrySelectListener {
        void onCountrySelected(Country country);
    }
}
