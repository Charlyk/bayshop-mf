package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.product.ShippingMethod;

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

    private ArrayList<ShippingMethod> mShippingMethods;
    private OnShippingClickListener mOnShippingClickListener;

    public ShippingMethodAdapter(ArrayList<ShippingMethod> shippingMethods) {
        mShippingMethods = shippingMethods;
    }

    public void setOnShippingClickListener(OnShippingClickListener onShippingClickListener) {
        mOnShippingClickListener = onShippingClickListener;
    }

    @Override
    public int getItemViewType(int position) {
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
            ViewHolder itemHolder = (ViewHolder) holder;
            itemHolder.mShippingMethodObj = mShippingMethods.get(position - 1);
            itemHolder.mShippingMethod.setText(itemHolder.mShippingMethodObj.getName());
            String price = itemHolder.mShippingMethodObj.getCurrency() + itemHolder.mShippingMethodObj.getCalculatedPrice();
            itemHolder.mMethodPrice.setText(price);
            String html = itemHolder.mShippingMethodObj.getDescription();
            html = html.replaceAll("<(.*?)\\>", " ");//Removes all items in brackets
            html = html.replaceAll("<(.*?)\\\n", " ");//Must be undeneath
            html = html.replaceFirst("(.*?)\\>", " ");//Removes any connected item to the last bracket
            html = html.replaceAll("&nbsp;", " ");
            html = html.replaceAll("&amp;", " ");
//            String description = Html.fromHtml(itemHolder.mShippingMethodObj.getDescription()).toString();
        }
    }

    @Override
    public int getItemCount() {
        return mShippingMethods.size() + 1;
    }

    public void refreshList(ArrayList<ShippingMethod> methods) {
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
        ShippingMethod mShippingMethodObj;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mDetailsButton.setOnClickListener(this);
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
        void onDetailsClick(ShippingMethod shippingMethod, int position, ImageButton detailsButton);
        void onSelectClick(ShippingMethod shippingMethod, int position);
    }
}
