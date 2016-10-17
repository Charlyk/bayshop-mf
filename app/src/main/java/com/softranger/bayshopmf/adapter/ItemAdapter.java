package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.pus.PUSParcel;
import com.softranger.bayshopmf.util.Application;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by eduard on 28.04.16.
 */
public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<PUSParcel> mInStockItems;
    private OnItemClickListener mOnItemClickListener;

    private static final int HEADER = -1, PUS_PARCEL = 2;

    public ItemAdapter(Context context) {
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
        View view;

        switch (viewType) {
            case PUS_PARCEL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_procesing_list_item, parent, false);
                return new InProcessingViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.additional_services_layout, parent, false);
                return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        InProcessingViewHolder processingHolder = (InProcessingViewHolder) holder;
        processingHolder.mProduct = mInStockItems.get(position);
        // set name id and date in position
        processingHolder.mUidLabel.setText(String.valueOf(processingHolder.mProduct.getCodeNumber()));
        String name = processingHolder.mProduct.getGeneralDescription();
        if (name == null || name.equals("") || name.equals("null")) {
            processingHolder.mDescriptionLabel.setText(mContext.getString(R.string.no_description));
            processingHolder.mDescriptionLabel.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
        } else {
            processingHolder.mDescriptionLabel.setText(processingHolder.mProduct.getGeneralDescription());
            processingHolder.mDescriptionLabel.setTextColor(mContext.getResources().getColor(android.R.color.black));
        }
        processingHolder.mRatingBar.setRating(processingHolder.mProduct.getRating());
        processingHolder.mDateLabel.setText(Application.getFormattedDate(processingHolder.mProduct.getFieldTime()));
        processingHolder.mPriceLabel.setText(processingHolder.mProduct.getCurrency() + processingHolder.mProduct.getPrice());

        // compute kilos from grams and set the result in weight label
        double realWeight = Double.parseDouble(processingHolder.mProduct.getRealWeight());
        double kg = realWeight / 1000;
        processingHolder.mWeightLabel.setText(kg + "kg.");
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
        TextView mDescriptionLabel;
        PUSParcel mProduct;

        public InProcessingViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onInProcessingProductClick(mProduct, getAdapterPosition());
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final Button mCombineBtn;
        final Button mCheckBtn;
        final Button mPhotosBtn;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mCombineBtn = (Button) itemView.findViewById(R.id.inStockAddsCombineParcelsBtn);
            mCheckBtn = (Button) itemView.findViewById(R.id.inStockAddsOrderCheckBtn);
            mPhotosBtn = (Button) itemView.findViewById(R.id.inStockAddsAdditionalPhotosBtn);

            mCombineBtn.setOnClickListener(this);
            mCheckBtn.setOnClickListener(this);
            mPhotosBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.inStockAddsCombineParcelsBtn:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onCombineClick();
                    }
                    break;
                case R.id.inStockAddsOrderCheckBtn:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onCheckOrderClick();
                    }
                    break;
                case R.id.inStockAddsAdditionalPhotosBtn:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onAdditionalPhotosClick();
                    }
                    break;
            }
        }
    }

    public interface OnItemClickListener {

        void onInProcessingProductClick(PUSParcel processingPackage, int position);

        void onCombineClick();

        void onCheckOrderClick();

        void onAdditionalPhotosClick();
    }
}
