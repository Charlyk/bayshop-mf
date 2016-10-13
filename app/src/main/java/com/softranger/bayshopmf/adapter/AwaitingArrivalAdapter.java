package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.box.AwaitingArrival;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.widget.ParcelStatusBarView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Eduard Albu on 9/20/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class AwaitingArrivalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<AwaitingArrival> mAwaitingArrivals;
    private OnAwaitingClickListener mOnAwaitingClickListener;
    SparseArray<ParcelStatusBarView.BarColor> mBarColorSparseArray;

    public AwaitingArrivalAdapter(ArrayList<AwaitingArrival> awaitingArrivals,
                                  SparseArray<ParcelStatusBarView.BarColor> barColorSparseArray) {
        mAwaitingArrivals = awaitingArrivals;
        mBarColorSparseArray = barColorSparseArray;
    }

    public void setOnAwaitingClickListener(OnAwaitingClickListener onAwaitingClickListener) {
        mOnAwaitingClickListener = onAwaitingClickListener;
    }

    public void refreshList(ArrayList<AwaitingArrival> awaitingArrivals) {
        mAwaitingArrivals.clear();
        mAwaitingArrivals.addAll(awaitingArrivals);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arrival_list_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.mAwaitingArrival = mAwaitingArrivals.get(position);
            itemHolder.mUidLabel.setText(itemHolder.mAwaitingArrival.getUid());
            itemHolder.mDescriptionLabel.setText(itemHolder.mAwaitingArrival.getTracking());
            itemHolder.mDateLabel.setText(Application.getFormattedDate(itemHolder.mAwaitingArrival.getCreatedDate()));
            itemHolder.mPriceLabel.setText("---");
            itemHolder.mWeightLabel.setText("---");
            itemHolder.mStatusBarView.setProgress(position + 1, "Some progress");
        }
    }

    @Override
    public int getItemCount() {
        return mAwaitingArrivals.size();
    }

    public void deleteItem(int position) {
        notifyItemRemoved(position);
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            ParcelStatusBarView.OnStatusBarReadyListener {

        @BindView(R.id.awaitingUidLabel) TextView mUidLabel;
        @BindView(R.id.awaitingDescriptionLabel) TextView mDescriptionLabel;
        @BindView(R.id.awaitingTrackingStatusBarView) ParcelStatusBarView mStatusBarView;
        @BindView(R.id.awaitingDateLabel) TextView mDateLabel;
        @BindView(R.id.awaitingWeightLabel) TextView mWeightLabel;
        @BindView(R.id.awaitingPriceLabel) TextView mPriceLabel;
        AwaitingArrival mAwaitingArrival;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mStatusBarView.setNewColorsMap(mBarColorSparseArray);
            mStatusBarView.setOnStatusBarReadyListener(this);
        }

        @OnClick(R.id.awaitingDeleteButton)
        void deleteAwaitingParcel() {
            if (mOnAwaitingClickListener != null) {
                mOnAwaitingClickListener.onDeleteAwaitingClick(mAwaitingArrival, getAdapterPosition());
            }
        }

        @Override
        public void onClick(View view) {
            if (mOnAwaitingClickListener != null) {
                mOnAwaitingClickListener.onAwaitingClick(mAwaitingArrival, getAdapterPosition());
            }
        }

        @Override
        public void onStatusBarReady() {
            mStatusBarView.setProgress(getAdapterPosition() + 1, "Some progress");
        }
    }

    public interface OnAwaitingClickListener {
        void onAwaitingClick(AwaitingArrival awaitingArrival, int position);
        void onDeleteAwaitingClick(AwaitingArrival awaitingArrival, int position);
    }
}
