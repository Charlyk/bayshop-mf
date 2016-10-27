package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Shipper;
import com.softranger.bayshopmf.util.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Eduard Albu on 7/19/16, 07, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class CalculatorAdapter extends RecyclerView.Adapter<CalculatorAdapter.ViewHolder> {

    private ArrayList<Shipper> mShippingMethods;
    private OnDetailsClickListener mOnDetailsClickListener;

    public CalculatorAdapter() {
        mShippingMethods = new ArrayList<>();
    }

    public void setOnDetailsClicListener(OnDetailsClickListener onDetailsClickListener) {
        mOnDetailsClickListener = onDetailsClickListener;
    }

    public void refreshList(ArrayList<Shipper> shippingMethods) {
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
        holder.mShipper = mShippingMethods.get(position);
        holder.mShippingMethodName.setText(holder.mShipper.getTitle());
        holder.mShippingPrice.setText(Constants.USD_SYMBOL + holder.mShipper.getPrice());
    }

    @Override
    public int getItemCount() {
        return mShippingMethods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.calculatorShippingNameLabel)
        TextView mShippingMethodName;
        @BindView(R.id.calculatorShippingPriceLabel)
        TextView mShippingPrice;
        Shipper mShipper;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.calculatorShippingDetailsBtn)
        public void onClick(View v) {
            if (mOnDetailsClickListener != null) {
                mOnDetailsClickListener.onDetailsClicked(mShipper, getAdapterPosition());
            }
        }
    }

    public interface OnDetailsClickListener {
        void onDetailsClicked(Shipper shippingMethod, int position);
    }
}
