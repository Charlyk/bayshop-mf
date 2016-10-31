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
import com.softranger.bayshopmfr.adapter.ItemAdapter;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.pus.PUSParcel;
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


public class ReceivedFragment extends ParentFragment implements ItemAdapter.OnItemClickListener,
        PullToRefreshLayout.PullToRefreshListener {

    public static final String ACTION_UPDATE = "com.softranger.bayshopmf.ui.pus.UPDATE_LIST";
    private MainActivity mActivity;
    private Unbinder mUnbinder;
    private ArrayList<PUSParcel> mPUSParcels;
    private ItemAdapter mAdapter;
    private Call<ServerResponse<ArrayList<PUSParcel>>> mCall;
    private ImageView mNoItemsImage;

    @BindView(R.id.fragmentRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.jellyPullToRefresh)
    JellyRefreshLayout mRefreshLayout;
    @BindView(R.id.fragmentFrameLayout)
    FrameLayout mRootLayout;

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
        intentFilter.addAction(Application.ACTION_RETRY);
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

    private ResponseCallback<ArrayList<PUSParcel>> mResponseCallback = new ResponseCallback<ArrayList<PUSParcel>>() {
        @Override
        public void onSuccess(ArrayList<PUSParcel> data) {
            mPUSParcels = data;
            mAdapter.refreshList(mPUSParcels);
            mActivity.toggleLoadingProgress(false);
            toggleNoItemVisibility(mPUSParcels.size() <= 0);
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
            mActivity.updateParcelCounters(Constants.ParcelStatus.RECEIVED);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(Call<ServerResponse<ArrayList<PUSParcel>>> call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
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
                    break;
            }
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
        Intent showDetails = new Intent(mActivity, PUSParcelActivity.class);
        showDetails.putExtra("id", processingPackage.getId());
        mActivity.startActivity(showDetails);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mCall = Application.apiInterface().getParcelsByStatus(Constants.ParcelStatus.RECEIVED);
        mCall.enqueue(mResponseCallback);
    }
}
