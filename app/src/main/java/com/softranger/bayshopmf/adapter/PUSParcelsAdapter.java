package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.pus.PUSParcel;
import com.softranger.bayshopmf.util.Application;
import com.softranger.bayshopmf.util.widget.ParcelStatusBarView;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Eduard Albu on 9/15/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class PUSParcelsAdapter extends RecyclerView.Adapter<PUSParcelsAdapter.ViewHolder> {

    private ArrayList<PUSParcel> mPUSParcels;
    private OnPusItemClickListener mOnPusItemClickListener;
    private Context mContext;
    private SparseBooleanArray mAnimatedItems;

    public PUSParcelsAdapter(ArrayList<PUSParcel> pusParcels, Context context) {
        mPUSParcels = pusParcels;
        mContext = context;
        mAnimatedItems = new SparseBooleanArray();


    }

    public void setOnPusItemClickListener(OnPusItemClickListener onPusItemClickListener) {
        mOnPusItemClickListener = onPusItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pus_parcel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mPUSParcel = mPUSParcels.get(position);

        holder.mStatusBarView.setProgress(holder.mPUSParcel.getParcelStatus().index(),
                mContext.getString(holder.mPUSParcel.getParcelStatus().statusName()));

        // TODO: 9/20/16 set parcel date
        holder.mDateLabel.setText(Application.getFormattedDate(new Date()));

        holder.mCodeLabel.setText(holder.mPUSParcel.getCodeNumber());

        // compute kilos from grams and set the result in weight label
        double realWeight = Double.parseDouble(holder.mPUSParcel.getRealWeight());
        double kg = realWeight / 1000;
        holder.mWeightLabel.setText(kg + "kg.");

        holder.mPriceLabel.setText(holder.mPUSParcel.getCurrency() + holder.mPUSParcel.getPrice());
    }

    @Override
    public int getItemCount() {
        return mPUSParcels.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.mStatusBarView.stopAnimations();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            ParcelStatusBarView.OnStatusBarReadyListener {

        @BindView(R.id.pusItemStatusBar) ParcelStatusBarView mStatusBarView;
        @BindView(R.id.pusItemDateLabel) TextView mDateLabel;
        @BindView(R.id.pusItemWeightLabel) TextView mWeightLabel;
        @BindView(R.id.pusItemPriceLabel) TextView mPriceLabel;
        @BindView(R.id.pusItemCodeNumberLabel) TextView mCodeLabel;
        @BindView(R.id.pusItemHolder) LinearLayout mHolderLayout;

        PUSParcel mPUSParcel;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mStatusBarView.setOnStatusBarReadyListener(this);
        }

        private int getPixelsFromDp(int dp) {
            Resources r = mContext.getResources();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        }

        @Override
        public void onClick(View view) {
            if (mOnPusItemClickListener != null) {
                mOnPusItemClickListener.onPusItemClick(mPUSParcel, getAdapterPosition());
            }
        }

        @Override
        public void onStatusBarReady() {
            if (mPUSParcel != null) {
                mStatusBarView.setProgress(mPUSParcel.getParcelStatus().index(),
                        mContext.getString(mPUSParcel.getParcelStatus().statusName()));
            }
        }
    }

    public interface OnPusItemClickListener {
        void onPusItemClick(PUSParcel pusParcel, int position);
    }
}
