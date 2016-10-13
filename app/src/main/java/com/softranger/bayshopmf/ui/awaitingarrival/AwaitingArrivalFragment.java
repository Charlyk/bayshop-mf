package com.softranger.bayshopmf.ui.awaitingarrival;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.AwaitingArrivalAdapter;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.AwaitingArrival;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.util.widget.ParcelStatusBarView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import uk.co.imallan.jellyrefresh.JellyRefreshLayout;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;


public class AwaitingArrivalFragment extends ParentFragment implements PullToRefreshLayout.PullToRefreshListener,
        AwaitingArrivalAdapter.OnAwaitingClickListener {

    public static final String ACTION_SHOW_BTN = "SHOW FLOATING BUTTON";
    public static final String ACTION_ITEM_ADDED = "ADDED NEW AWAITING PARCEL";
    public static final int ADD_PARCEL_RC = 1101;

    private MainActivity mActivity;
    private Unbinder mUnbinder;
    private AlertDialog mAlertDialog;

    private Call<ServerResponse<ArrayList<AwaitingArrival>>> mWaitingListCall;
    private AwaitingArrivalAdapter mAdapter;
    private ArrayList<AwaitingArrival> mAwaitingArrivals;

    public static final SparseArray<ParcelStatusBarView.BarColor> COLOR_MAP = new SparseArray<ParcelStatusBarView.BarColor>() {{
        put(1, ParcelStatusBarView.BarColor.green);
        put(2, ParcelStatusBarView.BarColor.gray);
        put(3, ParcelStatusBarView.BarColor.red);
        put(4, ParcelStatusBarView.BarColor.green);
    }};

    @BindView(R.id.fragmentRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.addAwaitingFloatingButton) FloatingActionButton mActionButton;
    @BindView(R.id.jellyPullToRefresh) JellyRefreshLayout mRefreshLayout;

    public AwaitingArrivalFragment() {
        // Required empty public constructor
    }

    public static AwaitingArrivalFragment newInstance() {
        Bundle args = new Bundle();
        AwaitingArrivalFragment fragment = new AwaitingArrivalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_and_refresh, container, false);
        mActivity = (MainActivity) getActivity();
        IntentFilter intentFilter = new IntentFilter(ACTION_ITEM_ADDED);
        intentFilter.addAction(ACTION_SHOW_BTN);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);
        mUnbinder = ButterKnife.bind(this, view);

        mRefreshLayout.setPullToRefreshListener(this);

        mWaitingListCall = Application.apiInterface().getAwaitingArrivalItems();

        mActivity.toggleLoadingProgress(true);
        mWaitingListCall.enqueue(mResponseCallback);

        // show action button
        mActionButton.setVisibility(View.VISIBLE);

        // Create the adapter for this fragment and pass it to recycler view
        mAwaitingArrivals = new ArrayList<>();
        mAdapter = new AwaitingArrivalAdapter(mAwaitingArrivals, COLOR_MAP);
        mAdapter.setOnAwaitingClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.awaiting_arrival);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.awaiting_arrival;
    }

    @OnClick(R.id.addAwaitingFloatingButton)
    void addNewAwaitingParcel() {
        Intent addAwaiting = new Intent(mActivity, AddAwaitingActivity.class);
        addAwaiting.putExtra(AddAwaitingActivity.SHOW_TRACKING, true);
        mActivity.startActivityForResult(addAwaiting, ADD_PARCEL_RC);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mWaitingListCall != null) mWaitingListCall.cancel();
        mActivity.unregisterReceiver(mBroadcastReceiver);
        mUnbinder.unbind();
    }

    /**
     * Awaiting arrival list request callback
     */
    private ResponseCallback<ArrayList<AwaitingArrival>> mResponseCallback = new ResponseCallback<ArrayList<AwaitingArrival>>() {
        @Override
        public void onSuccess(ArrayList<AwaitingArrival> data) {
            mAdapter.refreshList(data);
            mRecyclerView.setItemViewCacheSize(mAdapter.getItemCount());
            mActivity.toggleLoadingProgress(false);
            mRefreshLayout.setRefreshing(false);
            mActivity.updateParcelCounters(Constants.ParcelStatus.AWAITING_ARRIVAL);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(Call<ServerResponse<ArrayList<AwaitingArrival>>> call, Throwable t) {
            mActivity.toggleLoadingProgress(false);
            mRefreshLayout.setRefreshing(false);
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_ITEM_ADDED:
                    onRefresh(mRefreshLayout);
                    break;
                case ACTION_SHOW_BTN:
                    mActionButton.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    public void onAwaitingClick(AwaitingArrival awaitingArrival, int position) {
        mActivity.addFragment(AwaitingArrivalProductFragment.newInstance(awaitingArrival), true);
    }

    @Override
    public void onDeleteAwaitingClick(AwaitingArrival awaitingArrival, int position) {
        deleteItem(awaitingArrival, position);
    }

    private void deleteItem(final AwaitingArrival product, final int position) {
        mAlertDialog = mActivity.getDialog(getString(R.string.delete), getString(R.string.confirm_deleting) + " "
                        + product.getTitle() + "?", R.mipmap.ic_delete_box_24dp,
                getString(R.string.yes), ((view) ->  {mActivity.toggleLoadingProgress(true);
                    new DeleteAsyncTask(position, mAwaitingArrivals, mActivity, mAdapter).execute(product);
                    // close the dialog
                    mAlertDialog.dismiss();

                }), getString(R.string.no), ((view) -> {if (mAlertDialog != null) mAlertDialog.dismiss();}), 0);
        if (mAlertDialog != null) mAlertDialog.show();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mWaitingListCall = Application.apiInterface().getAwaitingArrivalItems();
        mWaitingListCall.enqueue(mResponseCallback);
    }
}
