package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.InStockItem;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/25/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class FirstStepAdapter extends RecyclerView.Adapter<FirstStepAdapter.ViewHolder> {

    private ArrayList<InStockItem> mInStockItems;
    private OnItemClickListener mOnItemClickListener;

    public FirstStepAdapter(ArrayList<InStockItem> inStockItems) {
        mInStockItems = inStockItems;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public InStockItem removeItem(int position) {
        InStockItem item = mInStockItems.get(position);
        mInStockItems.remove(position);
        notifyItemRemoved(position);
        return item;
    }

    public void insertItem(int position, InStockItem inStockItem) {
        mInStockItems.add(position, inStockItem);
        notifyItemInserted(position);
    }

    public void addItems(ArrayList<InStockItem> items) {
        mInStockItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.build_parcel_first_step_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mInStockItem = mInStockItems.get(position);
        holder.mMfLabel.setText(holder.mInStockItem.getParcelId());
        holder.mNameLabel.setText(holder.mInStockItem.getName());
        holder.mWeightLabel.setText(String.valueOf(((float) holder.mInStockItem.getWeight() / 1000)) + "kg.");
        holder.mPriceLabel.setText(String.valueOf(holder.mInStockItem.getCurrency()
                + holder.mInStockItem.getPrice()));
    }

    @Override
    public int getItemCount() {
        return mInStockItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mMfLabel;
        final TextView mNameLabel;
        final TextView mPriceLabel;
        final TextView mWeightLabel;
        final ImageButton mDeleteButton;
        InStockItem mInStockItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mMfLabel = (TextView) itemView.findViewById(R.id.buildFirstStepMfIdLabel);
            mNameLabel = (TextView) itemView.findViewById(R.id.buildFirstStepNameLabel);
            mPriceLabel = (TextView) itemView.findViewById(R.id.buildFirstStepPriceLabel);
            mWeightLabel = (TextView) itemView.findViewById(R.id.buildFirstStepWeghtLabel);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.buildFirstDeleteButton);
            mDeleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onDeleteClick(mInStockItem, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(InStockItem inStockItem, int position);
    }
}
