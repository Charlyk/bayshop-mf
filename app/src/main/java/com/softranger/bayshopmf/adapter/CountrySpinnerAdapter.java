package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.Country;
import com.softranger.bayshopmf.model.CountryCode;

import java.util.List;

/**
 * Created by macbook on 6/17/16.
 */
public class CountrySpinnerAdapter extends ArrayAdapter<Country> {

    private List<Country> mCountries;
    private Context mContext;
    private OnCountryClickListener mOnCountryClickListener;

    public CountrySpinnerAdapter(Context context, int resource, List<Country> objects) {
        super(context, resource, objects);
        mCountries = objects;
        mContext = context;
    }

    public void setOnCountryClickListener(OnCountryClickListener onCountryClickListener) {
        mOnCountryClickListener = onCountryClickListener;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);
        final Country countryCode = mCountries.get(position);
        View row = LayoutInflater.from(mContext).inflate(R.layout.country_spinner_item, parent, false);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCountryClickListener != null) {
                    mOnCountryClickListener.onCountryClick(countryCode, position);
                }
            }
        });
        TextView codeLabel = (TextView) row.findViewById(R.id.countryNameLabel);
        codeLabel.setText(countryCode.getName());
        return row;
    }

    public interface OnCountryClickListener {
        void onCountryClick(Country country, int position);
    }
}
