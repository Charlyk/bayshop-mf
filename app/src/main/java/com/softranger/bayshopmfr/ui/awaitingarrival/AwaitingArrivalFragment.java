package com.softranger.bayshopmfr.ui.awaitingarrival;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.AwaitingArrivalAdapter;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.box.AwaitingArrival;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.general.DeclarationActivity;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;
import com.softranger.bayshopmfr.util.widget.ParcelStatusBarView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import uk.co.imallan.jellyrefresh.JellyRefreshLayout;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;


public class AwaitingArrivalFragment extends ParentFragment implements PullToRefreshLayout.PullToRefreshListener,
        AwaitingArrivalAdapter.OnAwaitingClickListener, DeleteAsyncTask.OnDeleteListener {

    public static final String ACTION_SHOW_BTN = "SHOW FLOATING BUTTON";
    public static final String ACTION_LIST_CHANGED = "com.softranger.bayshopmf.ui.awaitingarrival.LIST_CHANGED";
    public static final int ADD_PARCEL_RC = 1101;

    private MainActivity mActivity;
    private Unbinder mUnbinder;
    private AlertDialog mAlertDialog;

    private Call<ServerResponse<ArrayList<AwaitingArrival>>> mWaitingListCall;
    private AwaitingArrivalAdapter mAdapter;
    private ArrayList<AwaitingArrival> mAwaitingArrivals;
    private View mPlaceHolder;

    public static final SparseArray<ParcelStatusBarView.BarColor> COLOR_MAP = new SparseArray<ParcelStatusBarView.BarColor>() {{
        put(0, ParcelStatusBarView.BarColor.gray);
        put(1, ParcelStatusBarView.BarColor.yellow);
        put(2, ParcelStatusBarView.BarColor.green);
    }};

    @BindView(R.id.fragmentRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.addAwaitingFloatingButton)
    FloatingActionButton mActionButton;
    @BindView(R.id.jellyPullToRefresh)
    JellyRefreshLayout mRefreshLayout;
    @BindView(R.id.fragmentFrameLayout)
    FrameLayout mFrameLayout;

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
        IntentFilter intentFilter = new IntentFilter(ACTION_LIST_CHANGED);
        intentFilter.addAction(ACTION_SHOW_BTN);
        intentFilter.addAction(Application.ACTION_RETRY);
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
        Intent addAwaiting = new Intent(mActivity, DeclarationActivity.class);
        addAwaiting.putExtra(DeclarationActivity.SHOW_TRACKING, true);
        mActivity.startActivityForResult(addAwaiting, ADD_PARCEL_RC);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mWaitingListCall != null) mWaitingListCall.cancel();
        mActivity.unregisterReceiver(mBroadcastReceiver);
        mUnbinder.unbind();
    }

    private void togglePlaceholder(boolean add) {
        if (add) {
            // add a placeholder to layout so user can know that there are no parcels
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mPlaceHolder = inflater.inflate(R.layout.no_awaiting_placeholder, null, false);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            mFrameLayout.addView(mPlaceHolder, layoutParams);
        } else {
            // remove place holder from layout if it is added
            if (mPlaceHolder != null) {
                mFrameLayout.removeView(mPlaceHolder);
            }
        }
    }

    /**
     * Awaiting arrival list request callback
     */
    private ResponseCallback<ArrayList<AwaitingArrival>> mResponseCallback = new ResponseCallback<ArrayList<AwaitingArrival>>() {
        @Override
        public void onSuccess(ArrayList<AwaitingArrival> data) {
            if (data != null && data.size() > 0) {
                togglePlaceholder(false);
                // add items to adapter
                mAdapter.refreshList(data);
                if (mRecyclerView != null) {
                    mRecyclerView.setItemViewCacheSize(mAdapter.getItemCount());
                }
            } else {
                togglePlaceholder(true);
            }
            mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
            Application.counters.put(Constants.ParcelStatus.AWAITING_ARRIVAL, data != null ? data.size() : 0);
            mActivity.updateParcelCounters(Constants.ParcelStatus.AWAITING_ARRIVAL);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(Call<ServerResponse<ArrayList<AwaitingArrival>>> call, Throwable t) {
            mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_LIST_CHANGED:
                    onRefresh(mRefreshLayout);
                    mActivity.updateParcelCounters(Constants.ParcelStatus.AWAITING_ARRIVAL);
                    break;
                case ACTION_SHOW_BTN:
                    mActionButton.setVisibility(View.VISIBLE);
                    break;
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    onRefresh(mRefreshLayout);
                    break;
            }
        }
    };

    @Override
    public void onAwaitingClick(AwaitingArrival awaitingArrival, int position) {
        Intent showDetails = new Intent(mActivity, AwaitingArrivalActivity.class);
        showDetails.putExtra("id", awaitingArrival.getId());
        mActivity.startActivity(showDetails);
    }

    @Override
    public void onDeleteAwaitingClick(AwaitingArrival awaitingArrival, int position) {
        deleteItem(awaitingArrival, position);
    }

    private void deleteItem(final AwaitingArrival product, final int position) {
        mAlertDialog = mActivity.getDialog(getString(R.string.delete), getString(R.string.confirm_deleting) + " "
                        + product.getUid() + "?", R.mipmap.ic_delete_parcel_popup_30dp,
                getString(R.string.yes), ((view) -> {
                    mActivity.toggleLoadingProgress(true);
                    DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask(position, mAwaitingArrivals, mActivity, mAdapter);
                    deleteAsyncTask.setOnDeleteListener(this);
                    deleteAsyncTask.execute(product);
                    // close the dialog
                    mAlertDialog.dismiss();

                }), getString(R.string.no), ((view) -> {
                    if (mAlertDialog != null) mAlertDialog.dismiss();
                }), 0);
        if (mAlertDialog != null) mAlertDialog.show();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mActivity.removeNoConnectionView();
        mWaitingListCall = Application.apiInterface().getAwaitingArrivalItems();
        mWaitingListCall.enqueue(mResponseCallback);
    }

    @Override
    public void onItemDeleted() {
        togglePlaceholder(mAdapter.getItemCount() <= 0);
    }
}
