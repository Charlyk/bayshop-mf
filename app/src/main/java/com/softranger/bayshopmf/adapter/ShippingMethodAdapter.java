package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.ShippingMethod;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 5/26/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ShippingMethodAdapter extends RecyclerView.Adapter<ShippingMethodAdapter.ViewHolder> {

    private ArrayList<ShippingMethod> mShippingMethods;
    private OnShippingClickListener mOnShippingClickListener;

    public ShippingMethodAdapter(ArrayList<ShippingMethod> shippingMethods) {
        mShippingMethods = shippingMethods;
    }

    public void setOnShippingClickListener(OnShippingClickListener onShippingClickListener) {
        mOnShippingClickListener = onShippingClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipping_method_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mShippingMethodObj = mShippingMethods.get(position);
        holder.mShippingMethod.setText(holder.mShippingMethodObj.getName());
        String price = holder.mShippingMethodObj.getCurrency() + holder.mShippingMethodObj.getPrice();
        holder.mMethodPrice.setText(price);
    }

    @Override
    public int getItemCount() {
        return mShippingMethods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mShippingMethod;
        final TextView mMethodPrice;
        final Button mDetailsButton;
        final Button mSelectButton;
        ShippingMethod mShippingMethodObj;
        public ViewHolder(View itemView) {
            super(itemView);
            mShippingMethod = (TextView) itemView.findViewById(R.id.shippingMethodItemNameLabel);
            mMethodPrice = (TextView) itemView.findViewById(R.id.shippingMethodItemPriceLabel);
            mDetailsButton = (Button) itemView.findViewById(R.id.shippingMethodDetailsBtn);
            mSelectButton = (Button) itemView.findViewById(R.id.shippingMethodSelectBtn);
            mDetailsButton.setOnClickListener(this);
            mSelectButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.shippingMethodDetailsBtn:
                    if (mOnShippingClickListener != null) {
                        mOnShippingClickListener.onDetailsClick(mShippingMethodObj, getAdapterPosition());
                    }
                    break;
                case R.id.shippingMethodSelectBtn:
                    if (mOnShippingClickListener != null) {
                        mOnShippingClickListener.onSelectClick(mShippingMethodObj, getAdapterPosition());
                    }
                    break;
            }
        }
    }

    public interface OnShippingClickListener {
        void onDetailsClick(ShippingMethod shippingMethod, int position);
        void onSelectClick(ShippingMethod shippingMethod, int position);
    }
}
