package com.softranger.bayshopmf.ui.pus;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.ItemAdapter;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.pus.PUSParcel;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import uk.co.imallan.jellyrefresh.JellyRefreshLayout;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;


public class ReceivedFragment extends ParentFragment implements ItemAdapter.OnItemClickListener,
        PullToRefreshLayout.PullToRefreshListener {

    public static final String ACTION_UPDATE = "com.softranger.bayshopmf.ui.pus.UPDATE_LIST";
    private MainActivity mActivity;
    private Unbinder mUnbinder;
    private ArrayList<PUSParcel> mPUSParcels;
    private ItemAdapter mAdapter;
    private Call<ServerResponse<ArrayList<PUSParcel>>> mCall;

    @BindView(R.id.fragmentRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.jellyPullToRefresh)
    JellyRefreshLayout mRefreshLayout;

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
        mActivity = (MainActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, view);

        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mPUSParcels = new ArrayList<>();
        mAdapter = new ItemAdapter(mActivity);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setPullToRefreshListener(this);

        mActivity.toggleLoadingProgress(true);
        onRefresh(mRefreshLayout);
        return view;
    }

    private ResponseCallback<ArrayList<PUSParcel>> mResponseCallback = new ResponseCallback<ArrayList<PUSParcel>>() {
        @Override
        public void onSuccess(ArrayList<PUSParcel> data) {
            mPUSParcels = data;
            mAdapter.refreshList(mPUSParcels);
            mActivity.toggleLoadingProgress(false);
            mRefreshLayout.setRefreshing(false);
            mActivity.updateParcelCounters(Constants.ParcelStatus.RECEIVED);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(Call<ServerResponse<ArrayList<PUSParcel>>> call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            mRefreshLayout.setRefreshing(false);
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefresh(mRefreshLayout);
        }
    };

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
        mActivity.unregisterReceiver(mBroadcastReceiver);
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
    }

    @Override
    public void onInProcessingProductClick(PUSParcel processingPackage, int position) {
        processingPackage.setParcelStatus(Constants.ParcelStatus.RECEIVED);
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
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mCall = Application.apiInterface().getParcelsByStatus(Constants.ParcelStatus.RECEIVED);
        mCall.enqueue(mResponseCallback);
    }
}
