package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.address.CountryCode;

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
        View row = LayoutInflater.from(mContext).inflate(R.layout.spinner_list_item, parent, false);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCountryClickListener != null) {
                    mOnCountryClickListener.onCountryClick(countryCode, position);
                }
            }
        });
        TextView codeLabel = (TextView) row.findViewById(R.id.spinnerCodeLabel);
        codeLabel.setText(countryCode.getCode());
        TextView name = (TextView) row.findViewById(R.id.spinnerCountryLabel);
        name.setText(countryCode.getName());
        ImageView icon = (ImageView)row.findViewById(R.id.spinnerFlagLabel);
        icon.setImageBitmap(countryCode.getImage());
        return row;
    }

    public interface OnCountryClickListener {
        void onCountryClick(CountryCode countryCode, int position);
    }
}
