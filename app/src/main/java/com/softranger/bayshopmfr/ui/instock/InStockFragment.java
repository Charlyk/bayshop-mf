package com.softranger.bayshopmfr.ui.instock;


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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.adapter.InStockAdapter;
import com.softranger.bayshopmfr.model.box.InStockList;
import com.softranger.bayshopmfr.model.app.ServerResponse;
import com.softranger.bayshopmfr.model.box.InStock;
import com.softranger.bayshopmfr.network.ResponseCallback;
import com.softranger.bayshopmfr.ui.addresses.AddressesListFragment;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.ui.steps.ConfirmationFragment;
import com.softranger.bayshopmfr.ui.steps.CreateParcelActivity;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.Constants;
import com.softranger.bayshopmfr.util.ParentActivity;
import com.softranger.bayshopmfr.util.ParentFragment;
import com.softranger.bayshopmfr.util.widget.TotalsView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import uk.co.imallan.jellyrefresh.JellyRefreshLayout;
import uk.co.imallan.jellyrefresh.PullToRefreshLayout;

public class InStockFragment extends ParentFragment implements PullToRefreshLayout.PullToRefreshListener,
        InStockAdapter.OnInStockClickListener, Animator.AnimatorListener, TotalsView.OnCreateParcelClickListener,
        InStockAdapter.OnAdditionalBtnsClickListener {

    public static final String ACTION_CREATE_PARCEL = "START CREATING PARCEL";
    public static final String ACTION_HIDE_TOTALS = "HIDE TOTALS VIEW";
    public static final String ACTION_UPDATE_LIST = "com.softranger.bayshopmf.ui.instock.UPDATE_LIST";

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

    private ImageView mNoItemImage;

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
        ImageView imageView = new ImageView(mActivity);
        imageView.setImageResource(R.mipmap.ic_nothing_225dp);

        // register broadcast receiver to get notified when an item is changed
        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_LIST);
        intentFilter.addAction(AddressesListFragment.ACTION_START_ANIM);
        intentFilter.addAction(ACTION_CREATE_PARCEL);
        intentFilter.addAction(ACTION_HIDE_TOTALS);
        intentFilter.addAction(ConfirmationFragment.ACTION_BUILD_FINISHED);
        intentFilter.addAction(Application.ACTION_RETRY);
        mActivity.registerReceiver(mBroadcastReceiver, intentFilter);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mInStocks = new ArrayList<>();
        mAdapter = new InStockAdapter(mInStocks, mActivity);
        mAdapter.setOnInStockClickListener(this);
        mAdapter.setOnAdditionalBtnsClickListener(this);

        mRecyclerView.setAdapter(mAdapter);

        mActivity.toggleLoadingProgress(true);
        onRefresh(mRefreshLayout);
        return view;
    }

    private void toggleNoItemsVisibility(boolean show) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = 30;
        if (show && mNoItemImage == null) {
            mNoItemImage = new ImageView(mActivity);
            mNoItemImage.setLayoutParams(layoutParams);
            mNoItemImage.setImageResource(R.mipmap.ic_nothing_225dp);
            mRootLayout.addView(mNoItemImage, 0);
        } else if (!show) {
            mRootLayout.removeView(mNoItemImage);
            mNoItemImage = null;
        }
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
            toggleNoItemsVisibility(mInStocks.size() <= 0);
            toggleTotalsVisibility(false);
            mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
            Application.counters.put(Constants.ParcelStatus.IN_STOCK, mInStocks.size());
            mActivity.updateParcelCounters(Constants.ParcelStatus.IN_STOCK);
        }

        @Override
        public void onFailure(ServerResponse errorData) {
            Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(Call<ServerResponse<InStockList>> call, Throwable t) {
            Toast.makeText(mActivity, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            mActivity.toggleLoadingProgress(false);
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
        }
    };

    /**
     * Broadcast used to receive item update messages
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_CREATE_PARCEL:
                    mTotalsView.toggleOnClick();
                    break;
                case ACTION_HIDE_TOTALS:
                    toggleTotalsVisibility(false);
                    selectedItems.clear();
                    for (InStock inStock : mInStocks) {
                        inStock.setSelected(false);
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                case Application.ACTION_RETRY:
                    mActivity.toggleLoadingProgress(true);
                    mActivity.removeNoConnectionView();
                default:
                    onRefresh(mRefreshLayout);
                    break;
            }
        }
    };

    public static boolean canHideTotals() {
        return isTotalVisible && selectedItems.size() > 0;
    }


    /*Adapter callbacks*/

    @Override
    public void onItemClick(InStock inStock, int position) {
        Intent showDetails = new Intent(mActivity, InStockActivity.class);
        showDetails.putExtra("id", inStock.getId());
        mActivity.startActivity(showDetails);
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
        Intent showDetails = new Intent(mActivity, InStockActivity.class);
        showDetails.putExtra("id", inStock.getId());
        mActivity.startActivity(showDetails);
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

    @Override
    public void additionalPhotoClick() {
        mActivity.showServicesInfo(getString(R.string.additional_photo),
                getString(R.string.additional_photo_description), R.mipmap.ic_photo_product_250dp);
    }

    @Override
    public void verificationClick() {
        mActivity.showServicesInfo(getString(R.string.check_product),
                getString(R.string.check_product_description), R.mipmap.ic_check_product_250dp);
    }

    @Override
    public void divideParcelClick() {
        mActivity.showServicesInfo(getString(R.string.divide_parcel),
                getString(R.string.divide_parcel_details), R.mipmap.ic_divide_250dp);
    }

    @Override
    public void repackingClick() {
        mActivity.showServicesInfo(getString(R.string.repacking),
                getString(R.string.repacking_description), R.mipmap.ic_repacking_250dp);
    }
}
