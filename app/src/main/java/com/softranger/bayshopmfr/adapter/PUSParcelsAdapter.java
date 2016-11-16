package com.softranger.bayshopmfr.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.pus.PUSParcel;
import com.softranger.bayshopmfr.util.Application;
import com.softranger.bayshopmfr.util.widget.ParcelStatusBarView;

import java.util.ArrayList;

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

    public PUSParcelsAdapter(ArrayList<PUSParcel> pusParcels, Context context) {
        mPUSParcels = pusParcels;
        mContext = context;


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

        holder.mStatusBarView.setProgress(holder.mPUSParcel.getParcelStatus().index());
        holder.mStatusLabel.setText(mContext.getString(holder.mPUSParcel.getParcelStatus().statusName()));

        holder.mDateLabel.setText(Application.getFormattedDate(holder.mPUSParcel.getFieldTime()));

        holder.mCodeLabel.setText(holder.mPUSParcel.getCodeNumber());

        // compute kilos from grams and set the result in weight label
        double realWeight = Double.parseDouble(holder.mPUSParcel.getRealWeight());
        double kg = realWeight / 1000;
        double vkg = holder.mPUSParcel.getVolumeWeight() / 1000;

        String volumeAndWeight = Application.round(kg, 2) + mContext.getString(R.string.kilos) + " / "
                + Application.round(vkg, 2) + mContext.getString(R.string.vkg);
        holder.mWeightLabel.setText(volumeAndWeight);

        String price = holder.mPUSParcel.getCurrency() + holder.mPUSParcel.getPrice();
        holder.mPriceLabel.setText(price);
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
        @BindView(R.id.pusItemStatusLabel)
        TextView mStatusLabel;

        PUSParcel mPUSParcel;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mStatusBarView.setStatusNameLabel(mStatusLabel);
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
                mStatusLabel.setText(mContext.getString(mPUSParcel.getParcelStatus().statusName()));
                mStatusBarView.setProgress(mPUSParcel.getParcelStatus().index());
            }
        }
    }

    public interface OnPusItemClickListener {
        void onPusItemClick(PUSParcel pusParcel, int position);
    }
}
