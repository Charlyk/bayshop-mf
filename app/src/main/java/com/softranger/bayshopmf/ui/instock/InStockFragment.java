package com.softranger.bayshopmf.ui.instock;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.InStockAdapter;
import com.softranger.bayshopmf.model.InStockList;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.InStock;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.awaitingarrival.AddAwaitingFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ParentActivity;
import com.softranger.bayshopmf.util.ParentFragment;
import com.softranger.bayshopmf.util.widget.TotalsView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;

public class InStockFragment extends ParentFragment implements SwipeRefreshLayout.OnRefreshListener,
        InStockAdapter.OnInStockClickListener {

    private Unbinder mUnbinder;
    private MainActivity mActivity;
    private Call<ServerResponse<InStockList>> mCall;
    private InStockAdapter mAdapter;
    private ArrayList<InStock> mInStocks;

    @BindView(R.id.fragmentRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.fragmentSwipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.fragmentFrameLayout) FrameLayout mRootLayout;
    private TotalsView mTotalsView;

    public InStockFragment() {
        // Required empty public constructor
    }

    public static InStockFragment newInstance() {
        Bundle args = new Bundle();
        InStockFragment fragment = new InStockFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_and_refresh, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mActivity = (MainActivity) getActivity();

        mTotalsView = new TotalsView(mActivity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;

        mRootLayout.addView(mTotalsView, layoutParams);

        // register broadcast receiver to get notified when an item is changed
        IntentFilter intentFilter = new IntentFilter(AddAwaitingFragment.ACTION_ITEM_ADDED);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mInStocks = new ArrayList<>();
        mAdapter = new InStockAdapter(mInStocks, mActivity);
        mAdapter.setOnInStockClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mCall = Application.apiInterface().getInStockItems(Application.currentToken);
        mActivity.toggleLoadingProgress(true);
        mCall.enqueue(mResponseCallback);
        return view;
    }

    /**
     * Response callbacks
     */
    private ResponseCallback<InStockList> mResponseCallback = new ResponseCallback<InStockList>() {
        @Override
        public void onSuccess(InStockList data) {
            mInStocks.clear();
            mInStocks.addAll(data.getInStocks());
            mAdapter.notifyDataSetChanged();
            mRecyclerView.setItemViewCacheSize(mInStocks.size());
            mActivity.toggleLoadingProgress(false);
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onError(Call<ServerResponse<InStockList>> call, Throwable t) {
            // TODO: 9/21/16 handle errors
            mActivity.toggleLoadingProgress(false);
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    /**
     * Broadcast used to receive item update messages
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mCall = Application.apiInterface().getInStockItems(Application.currentToken);
            mCall.enqueue(mResponseCallback);
        }
    };

    /*Adapter callbacks*/

    @Override
    public void onItemClick(InStock inStock, int position) {
        mActivity.addFragment(DetailsFragment.newInstance(inStock), true);
    }

    @Override
    public void onIconClick(InStock inStock, boolean isSelected, int position) {
        if (isSelected) {
            mTotalsView.increasePrice(2);
            mTotalsView.increaseWeight(2);
        } else {
            mTotalsView.decreasePrice(2);
            mTotalsView.decreaseWeight(2);
        }
    }

    @Override
    public void onNoDeclarationClick(InStock inStock, int position) {
        mActivity.addFragment(DetailsFragment.newInstance(inStock), true);
        Toast.makeText(mActivity, getString(R.string.fill_in_the_declaration), Toast.LENGTH_SHORT).show();
    }

    /*Utility methods*/

    @Override
    public String getFragmentTitle() {
        return getString(R.string.warehouse_usa);
    }

    @Override
    public ParentActivity.SelectedFragment getSelectedFragment() {
        return ParentActivity.SelectedFragment.in_stock;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) mCall.cancel();
        mUnbinder.unbind();
    }

    @Override
    public void onRefresh() {
        mCall = Application.apiInterface().getInStockItems(Application.currentToken);
        mCall.enqueue(mResponseCallback);
    }
}
