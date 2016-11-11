package com.softranger.bayshopmfr.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.payment.History;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by macbook on 6/30/16.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<History> mHistories;
    private OnHistoryClickListener mOnHistoryClickListener;
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
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
        String strDate = FORMAT.format(holder.mHistory.getDate());
        holder.mDate.setText(strDate);
        String summ = holder.mHistory.getCurrency() + holder.mHistory.getSumm();
        holder.mSumm.setText(summ);
        holder.mSumm.setTextColor(getTextColor(holder.mHistory.getPaymentType()));
        holder.mImageView.setImageResource(getTransactionIcon(holder.mHistory.getPaymentType()));
        Picasso.with(mContext).load(holder.mHistory.getImageUrl()).into(holder.mIcon);
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
        @BindView(R.id.paymentHistoryItemDescriptionLabel)
        TextView mDescription;
        @BindView(R.id.paymentHistoryItemDateLabel)
        TextView mDate;
        @BindView(R.id.paymentHistoryItemAmountLabel)
        TextView mSumm;
        @BindView(R.id.paymentHistoryItemSpentImage)
        ImageView mImageView;
        @BindView(R.id.paymentHistoryItemMethodIcon)
        ImageView mIcon;
        History mHistory;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
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
