package com.softranger.bayshopmfr.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softranger.bayshopmfr.R;

import butterknife.ButterKnife;

import static com.softranger.bayshopmfr.model.address.Address.AddressAction;

/**
 * Created by Eduard Albu on 10/6/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class AddressSpinnerAdapter extends ArrayAdapter<AddressAction> {
    private AddressAction[] mCounting;
    Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public AddressSpinnerAdapter(Context context, int resource, AddressAction[] objects) {
        super(context, resource, objects);
        mCounting = objects;
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.spinner_list_item, parent, false);

            row.setOnClickListener((view) -> {
                if (mOnItemClickListener != null) mOnItemClickListener.onItemClicked(position);
            });

            holder = new ViewHolder();
            holder.mImageView = ButterKnife.findById(row, R.id.spinnerFlagLabel);
            holder.mItemName = ButterKnife.findById(row, R.id.spinnerCodeLabel);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.mItemName.setText(mContext.getString(mCounting[position].getTitle()));
        holder.mImageView.setImageResource(mCounting[position].getIconId());
        return row;
    }

    class ViewHolder {
        TextView mItemName;
        ImageView mImageView;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }
}
