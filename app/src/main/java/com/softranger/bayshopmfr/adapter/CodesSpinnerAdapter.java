package com.softranger.bayshopmfr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.address.CountryCode;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by macbook on 6/17/16.
 */
public class CodesSpinnerAdapter extends ArrayAdapter<CountryCode> {

    private Context mContext;
    private List<CountryCode> mCountryCodes;
    private OnCountryClickListener mOnCountryClickListener;

    public CodesSpinnerAdapter(Context context, int resource, List<CountryCode> objects) {
        super(context, resource, objects);
        mContext = context;
        mCountryCodes = objects;
    }

    public void setOnCountryClickListener(OnCountryClickListener onCountryClickListener) {
        mOnCountryClickListener = onCountryClickListener;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        final CountryCode countryCode = mCountryCodes.get(position);
        View row = LayoutInflater.from(mContext).inflate(R.layout.phone_code_item, parent, false);
        row.setOnClickListener(v -> {
            if (mOnCountryClickListener != null) {
                mOnCountryClickListener.onCountryClick(countryCode, position);
            }
        });
        TextView codeLabel = (TextView) row.findViewById(R.id.spinnerCodeLabel);
        codeLabel.setText(countryCode.getCode());
        TextView name = (TextView) row.findViewById(R.id.spinnerCountryLabel);
        name.setText(countryCode.getName());
        ImageView icon = (ImageView)row.findViewById(R.id.spinnerFlagLabel);
        Picasso.with(mContext).load(countryCode.getImageUrl()).into(icon);
        return row;
    }

    public interface OnCountryClickListener {
        void onCountryClick(CountryCode countryCode, int position);
    }
}
