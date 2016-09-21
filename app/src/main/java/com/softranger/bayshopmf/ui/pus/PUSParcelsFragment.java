package com.softranger.bayshopmf.ui.pus;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.PUSParcelsAdapter;
import com.softranger.bayshopmf.model.pus.PUSParcel;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PUSParcelsFragment extends ParentFragment implements PUSParcelsAdapter.OnPusItemClickListener {

    private Unbinder mUnbinder;
    private ParentActivity mActivity;
    private ArrayList<PUSParcel> mPUSParcels;
    private PUSParcelsAdapter mAdapter;

    @BindView(R.id.fragmentRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.fragmentSwipeRefreshLayout) SwipeRefreshLayout mRefreshLayout;

    public PUSParcelsFragment() {
        // Required empty public constructor
    }

    public static PUSParcelsFragment newInstance() {
        Bundle args = new Bundle();
        PUSParcelsFragment fragment = new PUSParcelsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_and_refresh, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (ParentActivity) getActivity();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mPUSParcels = new ArrayList<>();
        mAdapter = new PUSParcelsAdapter(mPUSParcels, mActivity);
        mAdapter.setOnPusItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mActivity.toggleLoadingProgress(true);
        ApiClient.getInstance().getRequest(Constants.Api.urlOutgoing(), mHandler);
        return view;
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject data = response.getJSONObject("data");

        for (PUSParcel.PUSStatus status : PUSParcel.PUSStatus.values()) {
            JSONArray parcelsArray = data.optJSONArray(status.toString());
            if (parcelsArray != null) {
                for (int i = 0; i < parcelsArray.length(); i++) {
                    JSONObject parcelJson = parcelsArray.getJSONObject(i);
                    PUSParcel pusParcel = new ObjectMapper().readValue(parcelJson.toString(), PUSParcel.class);
                    pusParcel.setParcelStatus(status.toString());
                    mPUSParcels.add(pusParcel);
                }
            }
        }

        mAdapter.notifyDataSetChanged();
        mRecyclerView.setItemViewCacheSize(mAdapter.getItemCount());
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.parcels);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.parcels;
    }

    @Override
    public void onPusItemClick(PUSParcel pusParcel, int position) {
        mActivity.addFragment(InProcessingDetails.newInstance(pusParcel), true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
