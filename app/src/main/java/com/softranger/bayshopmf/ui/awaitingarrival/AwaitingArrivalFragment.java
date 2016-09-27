package com.softranger.bayshopmf.ui.awaitingarrival;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AwaitingArrivalFragment extends ParentFragment implements PullToRefreshLayout.PullToRefreshListener,
        AwaitingArrivalAdapter.OnAwaitingClickListener {

    public static final String ACTION_SHOW_BTN = "SHOW FLOATING BUTTON";

    private MainActivity mActivity;
    private Unbinder mUnbinder;
    private AlertDialog mAlertDialog;

    private Call<ServerResponse<ArrayList<AwaitingArrival>>> mWaitingListCall;
    private AwaitingArrivalAdapter mAdapter;
    private ArrayList<AwaitingArrival> mAwaitingArrivals;

    private static final SparseArray<ParcelStatusBarView.BarColor> COLOR_MAP = new SparseArray<ParcelStatusBarView.BarColor>() {{
        put(1, ParcelStatusBarView.BarColor.green);
        put(2, ParcelStatusBarView.BarColor.gray);
        put(3, ParcelStatusBarView.BarColor.red);
        put(4, ParcelStatusBarView.BarColor.green);
    }};

    @BindView(R.id.fragmentRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.jellyPullToRefresh) JellyRefreshLayout mRefreshLayout;
    @BindView(R.id.addAwaitingFloatingButton) FloatingActionButton mActionButton;

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
        IntentFilter intentFilter = new IntentFilter(AddAwaitingFragment.ACTION_ITEM_ADDED);
        intentFilter.addAction(ACTION_SHOW_BTN);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);
        mUnbinder = ButterKnife.bind(this, view);

        mWaitingListCall = Application.apiInterface().getAwaitingArrivalItems(Application.currentToken);

        mActivity.toggleLoadingProgress(true);
        mWaitingListCall.enqueue(mResponseCallback);

        // show action button
        mActionButton.setVisibility(View.VISIBLE);

        // Create the adapter for this fragment and pass it to recycler view
        mAwaitingArrivals = new ArrayList<>();
        mAdapter = new AwaitingArrivalAdapter(mAwaitingArrivals, COLOR_MAP);
        mAdapter.setOnAwaitingClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRefreshLayout.setPullToRefreshListener(this);
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
        mActivity.addFragment(AddAwaitingFragment.newInstance(), false);
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
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(Call<ServerResponse<ArrayList<AwaitingArrival>>> call, Throwable t) {
            // TODO: 9/21/16 handle errors
            mActivity.toggleLoadingProgress(false);
            mRefreshLayout.setRefreshing(false);
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case AddAwaitingFragment.ACTION_ITEM_ADDED:
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
                getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.toggleLoadingProgress(true);

                        new DeleteAsyncTask(position, mAwaitingArrivals, mActivity, mAdapter).execute(product);

                        // close the dialog
                        mAlertDialog.dismiss();
                    }
                }, getString(R.string.no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAlertDialog != null) mAlertDialog.dismiss();
                    }
                }, 0);
        if (mAlertDialog != null) mAlertDialog.show();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mWaitingListCall.clone().enqueue(mResponseCallback);
    }
}
