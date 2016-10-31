package com.softranger.bayshopmfr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.util.SpinnerObj;

import java.util.List;

/**
 * Created by macbook on 6/17/16.
 */
public class SpinnerAdapter<T extends SpinnerObj> extends ArrayAdapter<T> {

    private List<T> mCountries;
    private Context mContext;
    private OnCountryClickListener mOnCountryClickListener;

    public SpinnerAdapter(Context context, int resource, List<T> objects) {
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
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        final T object = mCountries.get(position);
        View row = LayoutInflater.from(mContext).inflate(R.layout.country_spinner_item, parent, false);
        row.setOnClickListener(v -> {
            if (mOnCountryClickListener != null) {
                mOnCountryClickListener.onCountryClick(object, position);
            }
        });
        TextView codeLabel = (TextView) row.findViewById(R.id.countryNameLabel);
        codeLabel.setText(object.getName());
        return row;
    }

    public interface OnCountryClickListener {
        <T extends SpinnerObj> void onCountryClick(T country, int position);
    }
}
