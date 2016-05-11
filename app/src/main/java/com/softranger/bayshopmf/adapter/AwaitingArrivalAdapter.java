package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Product;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/11/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class AwaitingArrivalAdapter extends RecyclerView.Adapter<AwaitingArrivalAdapter.ViewHolder> {

    private ArrayList<Product> mProducts;
    private OnItemClickListener mOnItemClickListener;

    public AwaitingArrivalAdapter(ArrayList<Product> products) {
        mProducts = products;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arrival_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mIdLabel;
        final TextView mNameLabel;
        final TextView mTrackingLabel;
        Product mProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mIdLabel = (TextView) itemView.findViewById(R.id.itemIdLabel);
            mNameLabel = (TextView) itemView.findViewById(R.id.itemNameLabel);
            mTrackingLabel = (TextView) itemView.findViewById(R.id.itemTrackingLabel);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mProduct, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Product product, int position);
    }
}
