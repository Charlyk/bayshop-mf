package com.softranger.bayshopmf.ui.instock;


import android.animation.Animator;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.adapter.InStockAdapter;
import com.softranger.bayshopmf.model.InStockList;
import com.softranger.bayshopmf.model.app.ServerResponse;
import com.softranger.bayshopmf.model.box.InStock;
import com.softranger.bayshopmf.network.ResponseCallback;
import com.softranger.bayshopmf.ui.addresses.AddressesListFragment;
import com.softranger.bayshopmf.ui.awaitingarrival.AddAwaitingFragment;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.steps.ConfirmationFragment;
import com.softranger.bayshopmf.ui.steps.CreateParcelActivity;
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

    public static final String ACTION_CREATE_PARCEL = "START CREATING PARCEL";
    public static final String ACTION_HIDE_TOTALS = "HIDE TOTALS VIEW";

    private Unbinder mUnbinder;
    private MainActivity mActivity;
    private Call<ServerResponse<InStockList>> mCall;
    private InStockAdapter mAdapter;
    private ArrayList<InStock> mInStocks;
    private static ArrayList<InStock> selectedItems;

    private ValueAnimator mShowAnimation;

    @BindView(R.id.fragmentRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragmentFrameLayout)
    FrameLayout mRootLayout;
    @BindView(R.id.jellyPullToRefresh)
    JellyRefreshLayout mRefreshLayout;

    private TotalsView mTotalsView;
    public static float totalsY;

    public static boolean isTotalVisible;

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

        selectedItems = new ArrayList<>();

        mRefreshLayout.setPullToRefreshListener(this);

        mTotalsView = new TotalsView(mActivity);
        mTotalsView.setOnCreateParcelClickListener(this);

        // register broadcast receiver to get notified when an item is changed
        IntentFilter intentFilter = new IntentFilter(AddAwaitingFragment.ACTION_ITEM_ADDED);
        intentFilter.addAction(AddressesListFragment.ACTION_START_ANIM);
        intentFilter.addAction(ACTION_CREATE_PARCEL);
        intentFilter.addAction(ACTION_HIDE_TOTALS);
        intentFilter.addAction(ConfirmationFragment.ACTION_BUILD_FINISHED);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mInStocks = new ArrayList<>();
        mAdapter = new InStockAdapter(mInStocks, mActivity);
        mAdapter.setOnInStockClickListener(this);

        mRecyclerView.setAdapter(mAdapter);

        mCall = Application.apiInterface().getInStockItems();
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
            selectedItems.clear();
            mTotalsView.resetTotals();
            mInStocks.addAll(data.getInStocks());
            mAdapter.notifyDataSetChanged();
            mRecyclerView.setItemViewCacheSize(mInStocks.size());
            toggleTotalsVisibility(false);
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
        public void onError(Call<ServerResponse<InStockList>> call, Throwable t) {
            Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            mRefreshLayout.setRefreshing(false);
        }
    };

    /**
     * Broadcast used to receive item update messages
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CREATE_PARCEL)) {
                mTotalsView.toggleOnClick();
            } else if (intent.getAction().equals(ACTION_HIDE_TOTALS)) {
                toggleTotalsVisibility(false);
                selectedItems.clear();
                for (InStock inStock : mInStocks) {
                    inStock.setSelected(false);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                // also using action ConfirmationFragment.ACTION_BUILD_FINISHED
                mCall = Application.apiInterface().getInStockItems();
                mCall.enqueue(mResponseCallback);
            }
        }
    };

    public static boolean canHideTotals() {
        return isTotalVisible && selectedItems.size() > 0;
    }


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
        double price = Double.parseDouble(inStock.getPrice());
        int grams = Integer.parseInt(inStock.getWeight());
        double kilos = grams / 1000;
        if (isSelected) {
            mTotalsView.increasePrice(price);
            mTotalsView.increaseWeight(kilos);
            selectedItems.add(inStock);
        } else {
            mTotalsView.decreasePrice(price);
            mTotalsView.decreaseWeight(kilos);
            selectedItems.remove(inStock);
        }

        toggleTotalsVisibility(selectedItems.size() > 0);
    }

    private void toggleTotalsVisibility(boolean show) {
        int from;
        int to;

        // if show is requested and view is already visible just return from here
        if (show && isTotalVisible) return;
        if (!show) mActivity.toggleActionMenuVisibility(false);

        // set this variable to use it in onAnimationEnd() method
        isTotalVisible = show;

        // set start and end value for animation
        if (show) {
            from = 0;
            to = Application.getPixelsFromDp(55);
        } else {
            from = Application.getPixelsFromDp(55);
            to = 0;
        }

        // create and set layout params for our totals view
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;
        layoutParams.height = Application.getPixelsFromDp(55);

        RelativeLayout.LayoutParams frameParams = (RelativeLayout.LayoutParams) mActivity.mFrameLayout.getLayoutParams();

        // if show is requested then add our view to root layout
        if (show)
            mActivity.mFrameLayout.addView(mTotalsView, 0, layoutParams);

        // build a value animator, it is global so we can cancel it if user clicks multiple
        // times on selection buttons
        mShowAnimation = ValueAnimator.ofFloat(from, to);
        mShowAnimation.setDuration(200);
        mShowAnimation.addListener(this);
        mShowAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mShowAnimation.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            frameParams.height = (int) animatedValue;
            mActivity.mFrameLayout.setLayoutParams(frameParams);
        });

        mShowAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (show) mActivity.toggleActionMenuVisibility(true);
                totalsY = mTotalsView.getY();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

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
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!isTotalVisible) {
            mActivity.mFrameLayout.removeView(mTotalsView);
            mTotalsView.resetTotals();
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
        mCall = Application.apiInterface().getInStockItems();
        mCall.enqueue(mResponseCallback);
    }

    @Override
    public void onCreateParcelClick() {
        Intent buildParcel = new Intent(mActivity, CreateParcelActivity.class);
        buildParcel.putExtra(CreateParcelActivity.SELECTED_ITEMS, selectedItems);
        mActivity.startActivity(buildParcel);
    }

    @Override
    public void onNavIconClick() {

    }
}
