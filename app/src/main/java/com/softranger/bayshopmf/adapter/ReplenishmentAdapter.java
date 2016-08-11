package com.softranger.bayshopmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.PaymentMethod;

import java.util.ArrayList;

/**
 * Created by Eduard Albu on 8/11/16, 08, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class ReplenishmentAdapter extends RecyclerView.Adapter<ReplenishmentAdapter.ViewHolder> {

    private ArrayList<PaymentMethod> mPaymentMethods;
    private OnMethodClickListener mOnMethodClickListener;

    public ReplenishmentAdapter(ArrayList<PaymentMethod> paymentMethods) {
        mPaymentMethods = paymentMethods;
    }

    public void setOnMethodClickListener(OnMethodClickListener onMethodClickListener) {
        mOnMethodClickListener = onMethodClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_method_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mPaymentMethod = mPaymentMethods.get(position);
        holder.mMethodImage.setImageResource(holder.mPaymentMethod.getImage());
        holder.mNameLabel.setText(holder.mPaymentMethod.getName());
        holder.mCommissionLabel.setText(holder.mPaymentMethod.getCommission() + "% commission");
        holder.mAmountLabel.setText(holder.mPaymentMethod.getCurrency() + holder.mPaymentMethod.getAmount());
    }

    @Override
    public int getItemCount() {
        return mPaymentMethods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mMethodImage;
        final TextView mNameLabel;
        final TextView mCommissionLabel;
        final TextView mAmountLabel;
        final RadioButton mRadioButton;
        final ImageButton mDetailsBtn;
        PaymentMethod mPaymentMethod;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mMethodImage = (ImageView) itemView.findViewById(R.id.replenishmentMethodTypeImage);
            mNameLabel = (TextView) itemView.findViewById(R.id.replenishmentMethodName);
            mCommissionLabel = (TextView) itemView.findViewById(R.id.replenishmentCommissionLabel);
            mAmountLabel = (TextView) itemView.findViewById(R.id.replenishmentAmountLabel);
            mRadioButton = (RadioButton) itemView.findViewById(R.id.replenishmentRadioBtn);
            mDetailsBtn = (ImageButton) itemView.findViewById(R.id.replenishmentDetailsBtn);
            mDetailsBtn.setOnClickListener(this);
            mRadioButton.setClickable(false);
        }

        @Override
        public void onClick(View v) {
            if (mOnMethodClickListener == null) return;
            switch (v.getId()) {
                case R.id.replenishmentDetailsBtn:
                    mOnMethodClickListener.onMethodDetailsClicked(mPaymentMethod, getAdapterPosition());
                    break;
                default:
                    mOnMethodClickListener.onMethodClicked(mPaymentMethod, getAdapterPosition());
                    break;
            }
        }
    }

    public interface OnMethodClickListener {
        void onMethodClicked(PaymentMethod method, int position);
        void onMethodDetailsClicked(PaymentMethod method, int position);
    }
}
