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
import com.softranger.bayshopmf.model.PUSParcel;
import com.softranger.bayshopmf.util.widget.ParcelStatusBarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    private static SimpleDateFormat serverFormat;
    private static SimpleDateFormat friendlyFormat;

    public PUSParcelsAdapter(ArrayList<PUSParcel> pusParcels, Context context) {
        mPUSParcels = pusParcels;
        mContext = context;
        mAnimatedItems = new SparseBooleanArray();

        serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        friendlyFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
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

        holder.mStatusBarView.setProgress(holder.mPUSParcel);

        holder.mDateLabel.setText(getFormattedDate(holder.mPUSParcel.getFieldTime()));

        holder.mCodeLabel.setText(holder.mPUSParcel.getCodeNumber());

        // compute kilos from grams and set the result in weight label
        double realWeight = Double.parseDouble(holder.mPUSParcel.getRealWeight());
        double kg = realWeight / 1000;
        holder.mWeightLabel.setText(kg + "kg.");

        holder.mPriceLabel.setText(holder.mPUSParcel.getCurrency() + holder.mPUSParcel.getPrice());
    }

    private String getFormattedDate(String createdDate) {
        Date today  = new Date();
        Date date = new Date();
        String formattedDate = "";
        try {
            if (createdDate != null && !createdDate.equals(""))
                date = serverFormat.parse(createdDate);
            formattedDate = friendlyFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long diff = today.getTime() - date.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (days > 0) {
                formattedDate = formattedDate + " (" + days + " " + mContext.getString(R.string.days_ago) + ")";
            } else {
                formattedDate = formattedDate + " (" + mContext.getString(R.string.today) + ")";
            }
        }
        return formattedDate;
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
                mStatusBarView.setProgress(mPUSParcel);
            }
        }
    }

    public interface OnPusItemClickListener {
        void onPusItemClick(PUSParcel pusParcel, int position);
    }
}
