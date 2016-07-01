package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.History;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by macbook on 6/30/16.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<History> mHistories;
    private OnHistoryClickListener mOnHistoryClickListener;
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
    private static final SimpleDateFormat SERVER_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private Context mContext;

    public HistoryAdapter(Context context, ArrayList<History> histories) {
        mHistories = new ArrayList<>();
        mHistories.addAll(histories);
        mContext = context;
    }

    public void setOnHistoryClickListener(OnHistoryClickListener onHistoryClickListener) {
        mOnHistoryClickListener = onHistoryClickListener;
    }

    public void refreshList(ArrayList<History> histories) {
        mHistories.clear();
        mHistories.addAll(histories);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_history_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mHistory = mHistories.get(position);
        holder.mDescription.setText(holder.mHistory.getComment());
        Date date = new Date();
        try {
            date = SERVER_FORMAT.parse(holder.mHistory.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String strDate = FORMAT.format(date);
        holder.mDate.setText(strDate);
        String summ = holder.mHistory.getCurrency() + holder.mHistory.getSumm();
        holder.mSumm.setText(summ);
        holder.mSumm.setTextColor(getTextColor(holder.mHistory.getPaymentType()));
        holder.mImageView.setImageResource(getTransactionIcon(holder.mHistory.getPaymentType()));
    }

    @DrawableRes
    private int getTransactionIcon(History.PaymentType paymentType) {
        switch (paymentType) {
            case minus:
                return R.mipmap.ic_arrow_red_24dpi;
            default: return R.mipmap.ic_arrow_green_24dpi;
        }
    }

    @ColorInt
    private int getTextColor(History.PaymentType paymentType) {
        switch (paymentType) {
            case minus:
                return mContext.getResources().getColor(R.color.colorAccent);
            default: return mContext.getResources().getColor(R.color.colorGreenAction);
        }
    }

    @Override
    public int getItemCount() {
        return mHistories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mDescription;
        final TextView mDate;
        final TextView mSumm;
        final ImageView mImageView;
        History mHistory;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mDescription = (TextView) itemView.findViewById(R.id.paymentHistoryItemDescriptionLabel);
            mDate = (TextView) itemView.findViewById(R.id.paymentHistoryItemDateLabel);
            mSumm = (TextView) itemView.findViewById(R.id.paymentHistoryItemAmountLabel);
            mImageView = (ImageView) itemView.findViewById(R.id.paymentHistoryItemSpentImage);
        }

        @Override
        public void onClick(View v) {
            if (mOnHistoryClickListener != null) {
                mOnHistoryClickListener.onHistoryClick(mHistory, getAdapterPosition());
            }
        }
    }

    public interface OnHistoryClickListener {
        void onHistoryClick(History history, int position);
    }
}
