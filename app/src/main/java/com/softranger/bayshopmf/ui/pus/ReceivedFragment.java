package com.softranger.bayshopmf.ui.pus;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ItemAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedFragment extends ParentFragment implements ItemAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ParentActivity mActivity;
    private Unbinder mUnbinder;
    private ArrayList<Object> mPUSParcels;
    private ItemAdapter mAdapter;

    @BindView(R.id.fragmentRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.fragmentSwipeRefreshLayout) SwipeRefreshLayout mRefreshLayout;

    public ReceivedFragment() {
        // Required empty public constructor
    }

    public static ReceivedFragment newInstance() {
        Bundle args = new Bundle();
        ReceivedFragment fragment = new ReceivedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_and_refresh, container, false);
        mActivity = (ParentActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);
        mPUSParcels = new ArrayList<>();
        mAdapter = new ItemAdapter(mActivity);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mActivity.toggleLoadingProgress(true);
        ApiClient.getInstance().getRequest(Constants.Api.urlOutgoing(Constants.US, Constants.ParcelStatus.RECEIVED), mHandler);

        return view;
    }

    @Override
    public void onServerResponse(JSONObject response) throws Exception {
        JSONArray data = response.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject parcelJson = data.getJSONObject(i);
            PUSParcel pusParcel = new ObjectMapper().readValue(parcelJson.toString(), PUSParcel.class);
            pusParcel.setParcelStatus(Constants.ParcelStatus.RECEIVED);
            mPUSParcels.add(pusParcel);
        }
        mAdapter.refreshList(mPUSParcels);
    }

    @Override
    public void onHandleMessageEnd() {
        mActivity.toggleLoadingProgress(false);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.received);
    }

    @Override
    public MainActivity.SelectedFragment getSelectedFragment() {
        return MainActivity.SelectedFragment.received;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onInProcessingProductClick(PUSParcel processingPackage, int position) {
        mActivity.addFragment(InProcessingDetails.newInstance(processingPackage), true);
    }

    @Override
    public void onCombineClick() {

    }

    @Override
    public void onCheckOrderClick() {

    }

    @Override
    public void onAdditionalPhotosClick() {

    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(false);
    }
}
