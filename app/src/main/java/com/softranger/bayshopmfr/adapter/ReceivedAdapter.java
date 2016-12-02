package com.softranger.bayshopmfr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.pus.PUSParcel;
import com.softranger.bayshopmfr.util.Application;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by eduard on 28.04.16.
 */
public class ReceivedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<PUSParcel> mInStockItems;
    private OnItemClickListener mOnItemClickListener;

    private static final int HEADER = -1, PUS_PARCEL = 2;

    public ReceivedAdapter(Context context) {
        mContext = context;
        mInStockItems = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mInStockItems.get(position) != null) {
            return PUS_PARCEL;
        }
        return HEADER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_list_item, parent, false);
        return new InProcessingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        InProcessingViewHolder itemHolder = (InProcessingViewHolder) holder;
        itemHolder.mPUSParcel = mInStockItems.get(position);
        // set name id and date in position
        itemHolder.mUidLabel.setText(String.valueOf(itemHolder.mPUSParcel.getCodeNumber()));

        itemHolder.mRatingBar.setRating(itemHolder.mPUSParcel.getRating());
        itemHolder.mDateLabel.setText(Application.getFormattedDate(itemHolder.mPUSParcel.getFieldTime()));
        itemHolder.mPriceLabel.setText(itemHolder.mPUSParcel.getCurrency() + itemHolder.mPUSParcel.getPrice());

        // compute kilos from grams and set the result in weight label
        double realWeight = Double.parseDouble(itemHolder.mPUSParcel.getRealWeight());
        double kg = realWeight / 1000;
        double vkg = itemHolder.mPUSParcel.getVolumeWeight() / 1000;

        String volumeAndWeight = Application.round(kg, 2) + mContext.getString(R.string.kilos) + " / "
                + Application.round(vkg, 2) + mContext.getString(R.string.vkg);

        itemHolder.mWeightLabel.setText(volumeAndWeight);

        String description = itemHolder.mPUSParcel.getDescription();
        if (description == null || description.equalsIgnoreCase("null")) {
            description = "";
        }
        itemHolder.mDescription.setText(description);
    }

    @Override
    public int getItemCount() {
        return mInStockItems.size();
    }

    public void refreshList(ArrayList<PUSParcel> objects) {
        mInStockItems.clear();
        mInStockItems.addAll(objects);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        mInStockItems.remove(position);
        notifyItemRemoved(position);
    }

    class InProcessingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.receivedParcelRatigBar)
        RatingBar mRatingBar;
        @BindView(R.id.receivedItemUidLabel)
        TextView mUidLabel;
        @BindView(R.id.receivedItemWeightLabel)
        TextView mWeightLabel;
        @BindView(R.id.receivedItemPriceLabel)
        TextView mPriceLabel;
        @BindView(R.id.receivedItemDateLabel)
        TextView mDateLabel;
        @BindView(R.id.receivedItemDescriptionLabel)
        TextView mDescription;
        PUSParcel mPUSParcel;

        public InProcessingViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onInProcessingProductClick(mPUSParcel, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {

        void onInProcessingProductClick(PUSParcel processingPackage, int position);
    }
}
