package com.softranger.bayshopmf.ui.awaitingarrival;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.AwaitingArrivalAdapter;
import com.softranger.bayshopmf.model.AwaitingArrival;
import com.softranger.bayshopmf.model.ServerResponse;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.Constants;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.util.widget.ParcelStatusBarView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AwaitingArrivalFragment extends ParentFragment implements SwipeRefreshLayout.OnRefreshListener,
        Callback<ServerResponse<ArrayList<AwaitingArrival>>>, AwaitingArrivalAdapter.OnAwaitingClickListener {

    private MainActivity mActivity;
    private Unbinder mUnbinder;
    private AlertDialog mAlertDialog;

    private Call<ServerResponse<ArrayList<AwaitingArrival>>> mWaitingListCall;
    private AwaitingArrivalAdapter mAdapter;
    private ArrayList<AwaitingArrival> mAwaitingArrivals;

    private static final SparseArray<ParcelStatusBarView.BarColor> COLOR_MAP = new SparseArray<ParcelStatusBarView.BarColor>() {{
        put(1, ParcelStatusBarView.BarColor.green);
        put(2, ParcelStatusBarView.BarColor.green);
        put(3, ParcelStatusBarView.BarColor.green);
        put(4, ParcelStatusBarView.BarColor.green);
    }};

    @BindView(R.id.fragmentRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.fragmentSwipeRefreshLayout) SwipeRefreshLayout mRefreshLayout;

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
        mUnbinder = ButterKnife.bind(this, view);

        mWaitingListCall = Application.apiInterface().getAwaitingArrivalItems(Application.currentToken);

        mActivity.toggleLoadingProgress(true);
        mWaitingListCall.enqueue(this);

        // Create the adapter for this fragment and pass it to recycler view
        mAwaitingArrivals = new ArrayList<>();
        mAdapter = new AwaitingArrivalAdapter(mAwaitingArrivals, COLOR_MAP);
        mAdapter.setOnAwaitingClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRefreshLayout.setOnRefreshListener(this);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWaitingListCall.cancel();
        mUnbinder.unbind();
    }

    @Override
    public void onResponse(Call<ServerResponse<ArrayList<AwaitingArrival>>> call,
                           Response<ServerResponse<ArrayList<AwaitingArrival>>> response) {
        ServerResponse<ArrayList<AwaitingArrival>> serverResponse = response.body();
        if (serverResponse.getMessage().equals(Constants.ApiResponse.OK_MESSAGE)) {
            mAdapter.refreshList(serverResponse.getData());
            mRecyclerView.setItemViewCacheSize(mAdapter.getItemCount());
        } else {
            Toast.makeText(mActivity, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
        }
        mActivity.toggleLoadingProgress(false);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(Call<ServerResponse<ArrayList<AwaitingArrival>>> call, Throwable t) {
        mActivity.toggleLoadingProgress(false);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        mWaitingListCall.clone().enqueue(this);
    }

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
}
