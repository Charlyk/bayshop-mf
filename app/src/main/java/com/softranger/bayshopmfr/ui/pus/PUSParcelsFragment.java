package com.softranger.bayshopmfr.ui.pus;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.PUSParcelsAdapter;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.pus.PUSParcel;
import com.softranger.bayshopmfr.model.pus.PUSStatuses;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ParentFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import uk.co.imallan.jellyrefresh.JellyRefreshLayout;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

public class PUSParcelsFragment extends ParentFragment implements PUSParcelsAdapter.OnPusItemClickListener,
        PullToRefreshLayout.PullToRefreshListener {

    public static final String ACTION_UPDATE = "com.softranger.bayshopmf.ui.pus.UPDATE_LIST";

    private Unbinder mUnbinder;
    private MainActivity mActivity;
    private ArrayList<PUSParcel> mPUSParcels;
    private PUSParcelsAdapter mAdapter;
    private Call<ServerResponse<PUSStatuses>> mCall;
    private ImageView mNoItemsImage;

    @BindView(R.id.fragmentRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.jellyPullToRefresh)
    JellyRefreshLayout mRefreshLayout;
    @BindView(R.id.fragmentFrameLayout)
    FrameLayout mRootLayout;

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
        mActivity = (MainActivity) getActivity();

        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE);
        intentFilter.addAction(Application.ACTION_RETRY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mPUSParcels = new ArrayList<>();
        mAdapter = new PUSParcelsAdapter(mPUSParcels, mActivity);
        mAdapter.setOnPusItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setPullToRefreshListener(this);

        mActivity.toggleLoadingProgress(true);
        onRefresh(mRefreshLayout);
        return view;
    }

    private void toggleNoItemVisibility(boolean show) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        if (show && mNoItemsImage == null) {
            mNoItemsImage = new ImageView(mActivity);
            mNoItemsImage.setImageResource(R.mipmap.ic_nothing_225dp);
            mRootLayout.addView(mNoItemsImage, layoutParams);
        } else {
            mRootLayout.removeView(mNoItemsImage);
            mNoItemsImage = null;
        }
    }

    private ResponseCallback<PUSStatuses> mResponseCallback = new ResponseCallback<PUSStatuses>() {
        @Override
        public void onSuccess(PUSStatuses data) {
            mPUSParcels.clear();
            mPUSParcels.addAll(data.getAllParcels());
            mAdapter.notifyDataSetChanged();
            mRecyclerView.setItemViewCacheSize(mPUSParcels.size());
            mActivity.toggleLoadingProgress(false);
            toggleNoItemVisibility(mPUSParcels.size() <= 0);
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
            mActivity.updateParcelCounters(Constants.PARCELS);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            if (isAdded()) {
                Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                mActivity.toggleLoadingProgress(false);
                if (mRefreshLayout != null)
                    mRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onError(Call<ServerResponse<PUSStatuses>> call, Throwable t) {
            if (isAdded()) {
                Toast.makeText(mActivity, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                mActivity.toggleLoadingProgress(false);
                if (mRefreshLayout != null)
                    mRefreshLayout.setRefreshing(false);
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                default:
                    onRefresh(mRefreshLayout);
                    mActivity.updateParcelCounters(Constants.PARCELS);
                    break;
            }
        }
    };

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
        Intent showDetails = new Intent(mActivity, PUSParcelActivity.class);
        showDetails.putExtra("id", pusParcel.getId());
        mActivity.startActivity(showDetails);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mActivity.unregisterReceiver(mBroadcastReceiver);
        mUnbinder.unbind();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mCall = Application.apiInterface().getAllParcelsFromServer();
        mCall.enqueue(mResponseCallback);
    }
}
