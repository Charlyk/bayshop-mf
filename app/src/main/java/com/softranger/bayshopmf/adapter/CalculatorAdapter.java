package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.ShippingMethod;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 7/19/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class CalculatorAdapter extends RecyclerView.Adapter<CalculatorAdapter.ViewHolder> {

    private ArrayList<ShippingMethod> mShippingMethods;
    private OnDetailsClickListener mOnDetailsClickListener;

    public CalculatorAdapter() {
        mShippingMethods = new ArrayList<>();
    }

    public void setOnDetailsClicListener(OnDetailsClickListener onDetailsClickListener) {
        mOnDetailsClickListener = onDetailsClickListener;
    }

    public void refreshList(ArrayList<ShippingMethod> shippingMethods) {
        mShippingMethods.clear();
        mShippingMethods.addAll(shippingMethods);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calculator_shipping_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mShippingMethods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mShippingMethodName;
        final TextView mShippingPrice;
        final ImageButton mDetailsBtn;
        ShippingMethod mShippingMethod;

        public ViewHolder(View itemView) {
            super(itemView);
            mShippingMethodName = (TextView) itemView.findViewById(R.id.calculatorShippingNameLabel);
            mShippingPrice = (TextView) itemView.findViewById(R.id.calculatorShippingPriceLabel);
            mDetailsBtn = (ImageButton) itemView.findViewById(R.id.calculatorShippingDetailsBtn);
            mDetailsBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnDetailsClickListener != null) {
                mOnDetailsClickListener.onDetailsClicked(mShippingMethod, getAdapterPosition());
            }
        }
    }

    public interface OnDetailsClickListener {
        void onDetailsClicked(ShippingMethod shippingMethod, int position);
    }
}
