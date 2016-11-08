package com.softranger.bayshopmfr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.Shipper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Eduard Albu on 5/26/16, 05, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ShippingMethodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Shipper> mShippingMethods;
    private OnShippingClickListener mOnShippingClickListener;
    private String mCurrency;
    private boolean mShowPrice = true;
    private boolean mCalculatorPrice;

    public ShippingMethodAdapter(ArrayList<Shipper> shippingMethods, String currency) {
        mShippingMethods = shippingMethods;
        mCurrency = currency;
    }

    public void setOnShippingClickListener(OnShippingClickListener onShippingClickListener) {
        mOnShippingClickListener = onShippingClickListener;
    }

    public void setCalculatorPrice(boolean calculatorPrice) {
        mCalculatorPrice = calculatorPrice;
    }

    public void setShowPrice(boolean showPrice) {
        mShowPrice = showPrice;
    }

    @Override
    public int getItemViewType(int position) {
        if (mCalculatorPrice) return 1;

        switch (position) {
            case 0:
                return 0;
            default:
                return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.warning_item, parent, false);
                return new WarningHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipping_list_item, parent, false);
                return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            int index = mCalculatorPrice ? position : position - 1;
            ViewHolder itemHolder = (ViewHolder) holder;
            itemHolder.mShippingMethodObj = mShippingMethods.get(index);
            itemHolder.mShippingMethod.setText(itemHolder.mShippingMethodObj.getTitle());
            itemHolder.mVolumeLabel.setText(String.valueOf(itemHolder.mShippingMethodObj.getMaxVolume()));
            itemHolder.mTimeLabel.setText(itemHolder.mShippingMethodObj.getTime());

            if (mShowPrice) {
                String price = String.valueOf(mCurrency);
                if (mCalculatorPrice) {
                    price = price + itemHolder.mShippingMethodObj.getPrice();
                } else {
                    price = price + itemHolder.mShippingMethodObj.getCalculatedPrice();
                }
                itemHolder.mMethodPrice.setText(price);
            }

            itemHolder.mMethodPrice.setVisibility(mShowPrice ? View.VISIBLE : View.GONE);
            itemHolder.mDetailsButton.setVisibility(mShowPrice ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (mCalculatorPrice) return mShippingMethods.size();
        else return mShippingMethods.size() + 1;
    }

    public void refreshList() {
        notifyDataSetChanged();
    }

    public class WarningHolder extends RecyclerView.ViewHolder {

        public WarningHolder(View itemView) {
            super(itemView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.shippingMethodNameLabel) TextView mShippingMethod;
        @BindView(R.id.shippingMethodPriceLabel) TextView mMethodPrice;
        @BindView(R.id.shippingMethodTimeLabel) TextView mTimeLabel;
        @BindView(R.id.shippingMethodVolumeLabel) TextView mVolumeLabel;
        @BindView(R.id.shippingMethodDetailsBtn) ImageButton mDetailsButton;
        Shipper mShippingMethodObj;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnShippingClickListener != null) {
                mOnShippingClickListener.onSelectClick(mShippingMethodObj, getAdapterPosition() - 1);
            }
        }

        @OnClick(R.id.shippingMethodDetailsBtn)
        void showShippingMethodDetails() {
            if (mOnShippingClickListener != null) {
                mOnShippingClickListener.onDetailsClick(mShippingMethodObj, getAdapterPosition() - 1, mDetailsButton);
            }
        }
    }

    public interface OnShippingClickListener {
        void onDetailsClick(Shipper shippingMethod, int position, ImageButton detailsButton);
        void onSelectClick(Shipper shippingMethod, int position);
    }
}
