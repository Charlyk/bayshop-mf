package com.softranger.bayshopmf.ui.instock;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.view.animation.AccelerateDecelerateInterpolator;
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
import uk.co.imallan.jellyrefresh.JellyRefreshLayout;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

public class InStockFragment extends ParentFragment implements PullToRefreshLayout.PullToRefreshListener,
        InStockAdapter.OnInStockClickListener, Animator.AnimatorListener, TotalsView.OnCreateParcelClickListener {

    private Unbinder mUnbinder;
    private MainActivity mActivity;
    private Call<ServerResponse<InStockList>> mCall;
    private InStockAdapter mAdapter;
    private ArrayList<InStock> mInStocks;
    private ArrayList<InStock> mSelectedItems;

    private ValueAnimator mShowAnimation;

    @BindView(R.id.fragmentRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.jellyPullToRefresh) JellyRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.fragmentFrameLayout) FrameLayout mRootLayout;

    private TotalsView mTotalsView;

    private boolean mIsTotalVisible;

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

        mSelectedItems = new ArrayList<>();

        mTotalsView = new TotalsView(mActivity);
        mTotalsView.setOnCreateParcelClickListener(this);

        // register broadcast receiver to get notified when an item is changed
        IntentFilter intentFilter = new IntentFilter(AddAwaitingFragment.ACTION_ITEM_ADDED);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mInStocks = new ArrayList<>();
        mAdapter = new InStockAdapter(mInStocks, mActivity);
        mAdapter.setOnInStockClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setPullToRefreshListener(this);

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
        // if we have already an animation in progress we should cancel it first
        if (mShowAnimation != null) mShowAnimation.cancel();

        // then check if item is either selected or not
        // if yes add it to our ArrayList of selected items and try to show totals view
        // otherwise remove it from lis and if the list is empty hide totals view
        if (isSelected) {
            mTotalsView.increasePrice(2);
            mTotalsView.increaseWeight(2);
            mSelectedItems.add(inStock);
        } else {
            mTotalsView.decreasePrice(2);
            mTotalsView.decreaseWeight(2);
            mSelectedItems.remove(inStock);
        }

        toggleTotalsVisibility(mSelectedItems.size() > 0);
    }

    private void toggleTotalsVisibility(boolean show) {
        int from;
        int to;

        // if show is requested and view is already visible just return from here
        if (show && mIsTotalVisible) return;

        // set this variable to use it in onAnimationEnd() method
        mIsTotalVisible = show;

        // set start and end value for animation
        if (show) {
            from = 0;
            to = Application.getPixelsFromDp(60);
        } else {
            from = Application.getPixelsFromDp(60);
            to = 0;
        }

        // create and set layout params for our totals view
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.height = from;

        // if show is requested then add our view to root layout
        if (show)
            mActivity.mFrameLayout.addView(mTotalsView, layoutParams);

        // build a value animator, it is global so we can cancel it if user clicks multiple
        // times on selection buttons
        mShowAnimation = ValueAnimator.ofFloat(from, to);
        mShowAnimation.setDuration(200);
        mShowAnimation.addListener(this);
        mShowAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mShowAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                layoutParams.height = (int) animatedValue;
                mTotalsView.setLayoutParams(layoutParams);
            }
        });

        // start the animation
        mShowAnimation.start();
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
        mActivity.mFrameLayout.removeView(mTotalsView);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!mIsTotalVisible) {
            mActivity.mFrameLayout.removeView(mTotalsView);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mCall = Application.apiInterface().getInStockItems(Application.currentToken);
        mCall.enqueue(mResponseCallback);
    }

    @Override
    public void onCreateParcelClick() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTotalsView, "y", mTotalsView.getY(), 300);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }
}
