package com.softranger.bayshopmf.ui;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.PUSParcelsAdapter;
import com.softranger.bayshopmf.adapter.ParcelListAdapter;
import com.softranger.bayshopmf.model.PUSParcel;
import com.softranger.bayshopmf.network.ApiClient;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class PUSParcelsFragment extends ParentFragment implements PUSParcelsAdapter.OnPusItemClickListener {

    private ParentActivity mActivity;
    private ArrayList<PUSParcel> mPUSParcels;
    private PUSParcelsAdapter mAdapter;

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
        View view = inflater.inflate(R.layout.fragment_pusparcels, container, false);
        mActivity = (ParentActivity) getActivity();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.pusItemsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        mPUSParcels = new ArrayList<>();
        mAdapter = new PUSParcelsAdapter(mPUSParcels, mActivity);
        mAdapter.setOnPusItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        mActivity.toggleLoadingProgress(true);
        ApiClient.getInstance().getRequest(Constants.Api.urlOutgoing(), mHandler);
        return view;
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONObject data = response.getJSONObject("data");

        for (PUSParcel.PUSStatus status : PUSParcel.PUSStatus.values()) {
            JSONArray parcelsArray = data.getJSONArray(status.toString());
            for (int i = 0; i < parcelsArray.length(); i++) {
                JSONObject parcelJson = parcelsArray.getJSONObject(i);
                PUSParcel pusParcel = new ObjectMapper().readValue(parcelJson.toString(), PUSParcel.class);
                pusParcel.setParcelStatus(status.toString());
                mPUSParcels.add(pusParcel);
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public void onPusItemClick(PUSParcel pusParcel, int position) {

    }
}
