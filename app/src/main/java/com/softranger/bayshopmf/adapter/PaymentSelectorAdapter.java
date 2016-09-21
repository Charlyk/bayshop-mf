package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.payment.Currency;

import java.util.List;

/**
 * Created by Eduard Albu on 6/30/16, 06, 2016
 * for project BayShop MF
 * email eduard.albu@gmail.com
 */
public class PaymentSelectorAdapter extends ArrayAdapter<Currency> {

    private Context mContext;
    private List<Currency> mCountryCodes;
    private OnCurrencyClickListener mOnCountryClickListener;

    public PaymentSelectorAdapter(Context context, int resource, List<Currency> objects) {
        super(context, resource, objects);
        mContext = context;
        mCountryCodes = objects;
    }

    public void setOnCountryClickListener(OnCurrencyClickListener onCountryClickListener) {
        mOnCountryClickListener = onCountryClickListener;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        final Currency currency = mCountryCodes.get(position);
        View row = LayoutInflater.from(mContext).inflate(R.layout.currency_row_layout, parent, false);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCountryClickListener != null) {
                    mOnCountryClickListener.onCurrencyClick(currency, position);
                }
            }
        });

        TextView currencyNameLabel = (TextView) row.findViewById(R.id.currencyNameLabel);
        currencyNameLabel.setText(currency.getName());
        ImageView currencyIcon = (ImageView) row.findViewById(R.id.currencyIconLabel);
        currencyIcon.setImageResource(currency.getIconId());
        return row;
    }

    public interface OnCurrencyClickListener {
        void onCurrencyClick(Currency currency, int position);
    }
}
