package com.softranger.bayshopmf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.PUSParcel;
import com.softranger.bayshopmf.util.widget.ParcelStatusBarView;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by Eduard Albu on 9/15/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class ParcelListAdapter extends BaseAdapter {

    private ArrayList<PUSParcel> mPUSParcels;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public ParcelListAdapter(Context context, ArrayList<PUSParcel> pusParcels) {
        mPUSParcels = pusParcels;
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPUSParcels.size();
    }

    @Override
    public Object getItem(int i) {
        return mPUSParcels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View content = view;
        if (content == null) {
            content = mLayoutInflater.inflate(R.layout.pus_parcel_item, viewGroup, false);
        }

        PUSParcel pusParcel = mPUSParcels.get(i);

        TextView codeNumber = ButterKnife.findById(content, R.id.pusItemCodeNumberLabel);
        TextView filedDate = ButterKnife.findById(content, R.id.pusItemDateLabel);
        TextView weight = ButterKnife.findById(content, R.id.pusItemWeightLabel);
        TextView price = ButterKnife.findById(content, R.id.pusItemPriceLabel);
        ParcelStatusBarView statusBarView = ButterKnife.findById(content, R.id.pusItemStatusBar);

        codeNumber.setText(pusParcel.getCodeNumber());
        filedDate.setText(pusParcel.getFieldTime());
        weight.setText(pusParcel.getRealWeight());
        price.setText(pusParcel.getCurrency() + pusParcel.getPrice());
        statusBarView.setProgress(pusParcel);

        return content;
    }
}
