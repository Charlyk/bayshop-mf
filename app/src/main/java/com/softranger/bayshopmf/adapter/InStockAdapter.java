package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.box.InStock;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.ViewAnimator;
import com.softranger.bayshopmf.util.widget.ParcelStatusBarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Eduard Albu on 9/21/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class InStockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<InStock> mInStocks;
    private Context mContext;
    private OnInStockClickListener mOnInStockClickListener;

    public InStockAdapter(ArrayList<InStock> inStocks, Context context) {
        mInStocks = inStocks;
        mContext = context;
    }

    public void setOnInStockClickListener(OnInStockClickListener onInStockClickListener) {
        mOnInStockClickListener = onInStockClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_stock_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.mInStock = mInStocks.get(position);
            itemHolder.mUidLabel.setText(itemHolder.mInStock.getUid());
            itemHolder.mDescriptionLabel.setText(itemHolder.mInStock.getTitle());
            itemHolder.mDateLabel.setText(Application.getFormattedDate(itemHolder.mInStock.getCreatedDate()));
            itemHolder.mWeightLabel.setText(itemHolder.mInStock.getWeight());
            itemHolder.mPriceLabel.setText(itemHolder.mInStock.getPrice());

            // TODO: 9/21/16 check status bar text on left side of parrent
            int spent = getSpentDays(itemHolder.mInStock);
            int remaining  = 45 - spent;
            itemHolder.mStatusBarView.setProgress(spent, "Remaining " + remaining + " days");
        }
    }

    @Override
    public int getItemCount() {
        return mInStocks.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            ViewAnimator.AnimationListener, ParcelStatusBarView.OnStatusBarReadyListener {

        @BindView(R.id.inStockUidLabel) TextView mUidLabel;
        @BindView(R.id.inStockDescriptionlabel) TextView mDescriptionLabel;
        @BindView(R.id.inStockStatusBar) ParcelStatusBarView mStatusBarView;
        @BindView(R.id.inStockDateLabel) TextView mDateLabel;
        @BindView(R.id.inStockWeightLabel) TextView mWeightLabel;
        @BindView(R.id.inStockPriceLabel) TextView mPriceLabel;
        @BindView(R.id.inStockItemImage) ImageView mImageView;

        View mView;
        ViewAnimator mViewAnimator;
        InStock mInStock;

        ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
            mView.setOnClickListener(this);

            mStatusBarView.setOnStatusBarReadyListener(this);
            mStatusBarView.setNewColorsMap(getBarColors());

            mViewAnimator = new ViewAnimator();
            mViewAnimator.setAnimationListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnInStockClickListener != null) {
                mOnInStockClickListener.onItemClick(mInStock, getAdapterPosition());
            }
        }

        @OnClick(R.id.inStockItemImage)
        void toggleItemSelection() {
            if (mInStock.getDeclarationFilled() != 0) {
                mInStock.setSelected(!mInStock.isSelected());
                mViewAnimator.flip(mImageView);
                if (mOnInStockClickListener != null) {
                    mOnInStockClickListener.onIconClick(mInStock, mInStock.isSelected(), getAdapterPosition());
                }
            } else {
                if (mOnInStockClickListener != null) {
                    mOnInStockClickListener.onNoDeclarationClick(mInStock, getAdapterPosition());
                }
            }
        }

        @Override
        public void onAnimationStarted() {
            @ColorInt int color = mInStock.isSelected() ? mContext.getResources().getColor(R.color.colorSelection) :
                    mContext.getResources().getColor(R.color.colorPrimary);
            mView.setBackgroundColor(color);
        }

        @Override
        public void onAnimationFinished() {
            @DrawableRes int image = mInStock.isSelected() ? R.mipmap.ic_check_45dp : R.mipmap.ic_uncheck_45dp;
            mImageView.setImageResource(image);
        }

        @Override
        public void onStatusBarReady() {
            int spent = getSpentDays(mInStock);
            int remaining  = 45 - spent;
            mStatusBarView.setProgress(spent, "Remaining " + remaining + " days");
        }
    }

    private SparseArray<ParcelStatusBarView.BarColor> getBarColors() {
        SparseArray<ParcelStatusBarView.BarColor> barColors = new SparseArray<>();
        for (int i = 0; i < 45; i++) {
            if (i <= 15) {
                barColors.put(i, ParcelStatusBarView.BarColor.green);
            } else if (i > 15 && i <= 40) {
                barColors.put(i, ParcelStatusBarView.BarColor.yellow);
            } else {
                barColors.put(i, ParcelStatusBarView.BarColor.red);
            }
        }
        return barColors;
    }

    private int getSpentDays(InStock inStock) {
        if (inStock == null) return 1;
        Date today = new Date();
        Date created = inStock.getCreatedDate();
        if (created == null) return 16;
        long diff = today.getTime() - created.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public interface OnInStockClickListener {
        void onItemClick(InStock inStock, int position);
        void onIconClick(InStock inStock, boolean isSelected, int position);
        void onNoDeclarationClick(InStock inStock, int position);
    }
}
